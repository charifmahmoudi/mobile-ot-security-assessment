package com.atlasot.scout

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.atlasot.domain.AssessmentCase
import com.atlasot.domain.ActorId
import com.atlasot.domain.ActorRef
import com.atlasot.domain.ActorRole
import com.atlasot.domain.CaseCodec
import com.atlasot.domain.CaseId
import com.atlasot.domain.CaseIntegrityException
import com.atlasot.domain.CaseRepository
import com.atlasot.domain.CaseState
import com.atlasot.domain.CaseSummary
import com.atlasot.domain.CaseVersionConflictException
import net.zetetic.database.sqlcipher.SQLiteDatabase
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * SQLCipher-backed aggregate repository for the professional case lifecycle.
 *
 * The normalized evidence tables remain separate M1/M2 work. This table is the durable
 * aggregate checkpoint used to enforce optimistic versioning and verified restoration.
 */
class SqlCipherCaseRepository(context: Context) : CaseRepository {
    private val appContext = context.applicationContext
    private val keyManager = DatabaseKeyManager(appContext)
    private val databaseFile = appContext.getDatabasePath(DATABASE_NAME)

    override fun load(id: CaseId): AssessmentCase? = withDatabase { db ->
        db.rawQuery(
            "SELECT case_number, revision, state, version, payload, payload_sha256 FROM professional_cases WHERE id = ?",
            arrayOf(id.value),
        ).use { cursor ->
            if (!cursor.moveToFirst()) return@withDatabase null
            val caseNumber = cursor.getString(0)
            val revision = cursor.getInt(1)
            val stateName = cursor.getString(2)
            val version = cursor.getLong(3)
            val payload = cursor.getBlob(4)
            val storedPayloadHash = cursor.getString(5)
            if (CaseCodec.payloadHash(payload) != storedPayloadHash) {
                throw CaseIntegrityException("professional case row payload hash mismatch")
            }
            val restored = CaseCodec.decode(payload)
            if (restored.id != id || restored.caseNumber != caseNumber || restored.revision != revision ||
                restored.state.name != stateName || restored.version != version
            ) {
                throw CaseIntegrityException("professional case row metadata does not match its authenticated payload")
            }
            restored
        }
    }

    override fun list(): List<CaseSummary> = withDatabase { db ->
        db.rawQuery(
            "SELECT id, case_number, revision, state, version FROM professional_cases ORDER BY case_number, revision",
            null,
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    val stateName = cursor.getString(3)
                    val state = CaseState.entries.firstOrNull { it.name == stateName }
                        ?: throw CaseIntegrityException("unknown professional case state $stateName")
                    add(CaseSummary(CaseId(cursor.getString(0)), cursor.getString(1), cursor.getInt(2), state, cursor.getLong(4)))
                }
            }
        }
    }

    override fun save(case: AssessmentCase, expectedVersion: Long?) {
        val payload = CaseCodec.encode(case)
        val payloadHash = CaseCodec.payloadHash(payload)
        withDatabase { db ->
            db.beginTransaction()
            try {
                val actualVersion = currentVersion(db, case.id)
                if (actualVersion != expectedVersion) {
                    throw CaseVersionConflictException(case.id, expectedVersion, actualVersion)
                }
                if (expectedVersion != null && case.version <= expectedVersion) {
                    throw IllegalArgumentException("updated professional case version must advance beyond the stored version")
                }

                if (expectedVersion == null) {
                    try {
                        db.execSQL(
                            "INSERT INTO professional_cases " +
                                "(id, case_number, revision, state, version, created_at, payload, payload_sha256) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                            arrayOf<Any>(
                                case.id.value, case.caseNumber, case.revision, case.state.name, case.version,
                                case.createdAt.toString(), payload, payloadHash,
                            ),
                        )
                    } catch (error: SQLiteConstraintException) {
                        throw CaseIntegrityException("professional case identity or revision already exists", error)
                    }
                } else {
                    db.execSQL(
                        "UPDATE professional_cases SET case_number = ?, revision = ?, state = ?, version = ?, " +
                            "created_at = ?, payload = ?, payload_sha256 = ? WHERE id = ? AND version = ?",
                        arrayOf<Any>(
                            case.caseNumber, case.revision, case.state.name, case.version, case.createdAt.toString(),
                            payload, payloadHash, case.id.value, expectedVersion,
                        ),
                    )
                    if (changedRows(db) != 1L) {
                        throw CaseVersionConflictException(case.id, expectedVersion, currentVersion(db, case.id))
                    }
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }

    fun saveNewCase(case: AssessmentCase, participants: ProfessionalCaseParticipants) {
        val payload = CaseCodec.encode(case)
        val payloadHash = CaseCodec.payloadHash(payload)
        withDatabase { db ->
            db.beginTransaction()
            try {
                if (currentVersion(db, case.id) != null) {
                    throw CaseIntegrityException("professional case identity already exists")
                }
                try {
                    db.execSQL(
                        "INSERT INTO professional_cases " +
                            "(id, case_number, revision, state, version, created_at, payload, payload_sha256) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        arrayOf<Any>(
                            case.id.value, case.caseNumber, case.revision, case.state.name, case.version,
                            case.createdAt.toString(), payload, payloadHash,
                        ),
                    )
                    saveParticipants(db, case.id, participants)
                } catch (error: SQLiteConstraintException) {
                    throw CaseIntegrityException("professional case identity or revision already exists", error)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }

    fun saveParticipants(caseId: CaseId, participants: ProfessionalCaseParticipants) {
        withDatabase { db -> saveParticipants(db, caseId, participants) }
    }

    private fun saveParticipants(db: SQLiteDatabase, caseId: CaseId, participants: ProfessionalCaseParticipants) {
        db.execSQL(
            "INSERT OR REPLACE INTO professional_case_participants " +
                "(case_id, assessor_id, assessor_name, operational_id, operational_name, security_id, security_name, reviewer_id, reviewer_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
            arrayOf<Any>(
                caseId.value,
                participants.assessor.id.value,
                participants.assessor.displayName,
                participants.operationalApprover.id.value,
                participants.operationalApprover.displayName,
                participants.securityApprover.id.value,
                participants.securityApprover.displayName,
                participants.independentReviewer.id.value,
                participants.independentReviewer.displayName,
            ),
        )
    }

    fun loadParticipants(caseId: CaseId): ProfessionalCaseParticipants? = withDatabase { db ->
        db.rawQuery(
            "SELECT assessor_id, assessor_name, operational_id, operational_name, security_id, security_name, reviewer_id, reviewer_name " +
                "FROM professional_case_participants WHERE case_id = ?",
            arrayOf(caseId.value),
        ).use { cursor ->
            if (!cursor.moveToFirst()) return@withDatabase null
            ProfessionalCaseParticipants(
                ActorRef(ActorId(cursor.getString(0)), cursor.getString(1), ActorRole.ASSESSOR),
                ActorRef(ActorId(cursor.getString(2)), cursor.getString(3), ActorRole.OPERATIONAL_APPROVER),
                ActorRef(ActorId(cursor.getString(4)), cursor.getString(5), ActorRole.SECURITY_APPROVER),
                ActorRef(ActorId(cursor.getString(6)), cursor.getString(7), ActorRole.REVIEWER),
            )
        }
    }

    /** Performs SQLCipher page-HMAC verification plus SQLite logical integrity verification. */
    fun verifyIntegrity() {
        withDatabase { db ->
            val cipherErrors = db.rawQuery("PRAGMA cipher_integrity_check", null).use { cursor ->
                buildList { while (cursor.moveToNext()) add(cursor.getString(0)) }
            }
            if (cipherErrors.isNotEmpty()) {
                throw CaseIntegrityException("SQLCipher page integrity check failed: ${cipherErrors.joinToString("; ")}")
            }
            val logicalResult = db.rawQuery("PRAGMA integrity_check", null).use { cursor ->
                if (!cursor.moveToFirst()) null else cursor.getString(0)
            }
            if (logicalResult != "ok") throw CaseIntegrityException("SQLite logical integrity check failed: $logicalResult")
        }
    }

    private fun currentVersion(db: SQLiteDatabase, id: CaseId): Long? = db.rawQuery(
        "SELECT version FROM professional_cases WHERE id = ?", arrayOf(id.value)
    ).use { cursor -> if (cursor.moveToFirst()) cursor.getLong(0) else null }

    private fun changedRows(db: SQLiteDatabase): Long = db.rawQuery("SELECT changes()", null).use { cursor ->
        if (!cursor.moveToFirst()) 0L else cursor.getLong(0)
    }

    private fun <T> withDatabase(block: (SQLiteDatabase) -> T): T {
        SqlCipherRuntime.ensureLoaded()
        databaseFile.parentFile?.mkdirs()
        val databaseExisted = databaseFile.exists()
        val databaseKey = keyManager.databaseKey(databaseExisted)
        var db: SQLiteDatabase? = null
        try {
            db = SQLiteDatabase.openOrCreateDatabase(databaseFile, databaseKey, null, null, null)
            ensureSchema(db)
            return block(db)
        } catch (error: CaseIntegrityException) {
            throw error
        } catch (error: CaseVersionConflictException) {
            throw error
        } catch (error: SQLiteException) {
            val detail = error.message?.takeIf { it.isNotBlank() } ?: error.javaClass.simpleName
            throw CaseIntegrityException("encrypted professional case database operation failed: $detail", error)
        } finally {
            db?.close()
            databaseKey.fill(0)
        }
    }

    private fun ensureSchema(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
        val secureDelete = db.rawQuery("PRAGMA secure_delete = ON", null).use { cursor ->
            if (!cursor.moveToFirst()) throw CaseIntegrityException("unable to configure SQLite secure_delete")
            cursor.getInt(0)
        }
        if (secureDelete != 1) throw CaseIntegrityException("SQLite secure_delete did not enable")

        when (val version = db.version) {
            0 -> {
                db.beginTransaction()
                try {
                    db.execSQL(
                        "CREATE TABLE professional_cases (" +
                            "id TEXT PRIMARY KEY NOT NULL, " +
                            "case_number TEXT NOT NULL, " +
                            "revision INTEGER NOT NULL CHECK(revision >= 1), " +
                            "state TEXT NOT NULL, " +
                            "version INTEGER NOT NULL CHECK(version >= 1), " +
                            "created_at TEXT NOT NULL, " +
                            "payload BLOB NOT NULL, " +
                            "payload_sha256 TEXT NOT NULL CHECK(length(payload_sha256) = 64), " +
                            "UNIQUE(case_number, revision))"
                    )
                    db.execSQL("CREATE INDEX idx_professional_cases_state ON professional_cases(state)")
                    createParticipantsTable(db)
                    db.version = SCHEMA_VERSION
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
            }
            1 -> {
                db.beginTransaction()
                try {
                    createParticipantsTable(db)
                    db.version = SCHEMA_VERSION
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
            }
            SCHEMA_VERSION -> Unit
            else -> throw CaseIntegrityException("unsupported professional case database schema $version")
        }
    }

    private fun createParticipantsTable(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE professional_case_participants (" +
                "case_id TEXT PRIMARY KEY NOT NULL REFERENCES professional_cases(id) ON DELETE CASCADE, " +
                "assessor_id TEXT NOT NULL, assessor_name TEXT NOT NULL, " +
                "operational_id TEXT NOT NULL, operational_name TEXT NOT NULL, " +
                "security_id TEXT NOT NULL, security_name TEXT NOT NULL, " +
                "reviewer_id TEXT NOT NULL, reviewer_name TEXT NOT NULL)"
        )
    }

    companion object {
        const val DATABASE_NAME = "atlas-professional-v1.db"
        private const val SCHEMA_VERSION = 2
    }
}

private object SqlCipherRuntime {
    private val loaded: Unit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { System.loadLibrary("sqlcipher") }
    fun ensureLoaded() { loaded }
}

private class DatabaseKeyManager(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    }

    fun databaseKey(databaseExists: Boolean): ByteArray = synchronized(KEY_LOCK) {
        val ivText = preferences.getString(KEY_IV, null)
        val ciphertextText = preferences.getString(KEY_CIPHERTEXT, null)
        if ((ivText == null) != (ciphertextText == null)) {
            throw CaseIntegrityException("wrapped professional database key metadata is incomplete")
        }

        if (ivText != null && ciphertextText != null) {
            if (!databaseExists) {
                throw CaseIntegrityException("professional database file is missing while wrapped key metadata exists")
            }
            val wrappingKey = existingWrappingKey()
                ?: throw CaseIntegrityException("professional database wrapping key is missing")
            return decryptWrappedKey(wrappingKey, ivText, ciphertextText)
        }

        if (databaseExists) {
            throw CaseIntegrityException("professional database exists but wrapped key metadata is missing")
        }

        val wrappingKey = existingWrappingKey() ?: createWrappingKey()
        val raw = ByteArray(DATABASE_KEY_BYTES).also(SecureRandom()::nextBytes)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, wrappingKey)
        cipher.updateAAD(AAD)
        val ciphertext = cipher.doFinal(raw)
        val committed = preferences.edit()
            .putString(KEY_IV, Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .putString(KEY_CIPHERTEXT, Base64.encodeToString(ciphertext, Base64.NO_WRAP))
            .commit()
        if (!committed) {
            raw.fill(0)
            throw CaseIntegrityException("unable to persist wrapped professional database key")
        }
        raw
    }

    private fun decryptWrappedKey(wrappingKey: SecretKey, ivText: String, ciphertextText: String): ByteArray = try {
        val iv = Base64.decode(ivText, Base64.NO_WRAP)
        val ciphertext = Base64.decode(ciphertextText, Base64.NO_WRAP)
        require(iv.size == GCM_IV_BYTES) { "invalid wrapped database key nonce" }
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, wrappingKey, GCMParameterSpec(GCM_TAG_BITS, iv))
        cipher.updateAAD(AAD)
        cipher.doFinal(ciphertext).also {
            if (it.size != DATABASE_KEY_BYTES) {
                it.fill(0)
                throw CaseIntegrityException("invalid unwrapped professional database key length")
            }
        }
    } catch (error: CaseIntegrityException) {
        throw error
    } catch (error: Exception) {
        throw CaseIntegrityException("unable to unwrap professional database key", error)
    }

    private fun existingWrappingKey(): SecretKey? = try {
        keyStore.getKey(KEYSTORE_ALIAS, null) as? SecretKey
    } catch (error: Exception) {
        throw CaseIntegrityException("unable to access professional database wrapping key", error)
    }

    private fun createWrappingKey(): SecretKey {
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .setUnlockedDeviceRequired(true)
                .build()
        )
        return generator.generateKey()
    }

    companion object {
        private const val PREFERENCES_NAME = "atlas-professional-key-v1"
        private const val KEY_IV = "database_key_iv"
        private const val KEY_CIPHERTEXT = "database_key_ciphertext"
        private const val KEYSTORE_ALIAS = "atlas.professional.db.wrap.v1"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val DATABASE_KEY_BYTES = 32
        private const val GCM_IV_BYTES = 12
        private const val GCM_TAG_BITS = 128
        private val AAD = "ATLAS-PROFESSIONAL-DB-KEY-V1".toByteArray(Charsets.UTF_8)
        private val KEY_LOCK = Any()
    }
}
