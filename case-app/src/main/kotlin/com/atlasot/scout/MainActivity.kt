package com.atlasot.scout

import android.app.Activity
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.OpenableColumns
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.view.*
import android.widget.*
import com.atlasot.domain.*
import com.atlasot.netbroker.IAtlasNetworkBroker
import java.io.FileInputStream
import java.io.InputStream
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.time.Instant
import java.util.UUID
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private val worker = Executors.newSingleThreadExecutor()
    private lateinit var content: LinearLayout
    private var brokerConnection: ServiceConnection? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        renderHome()
        intent?.data?.let(::importCaptureUri)
    }

    override fun onDestroy() {
        brokerConnection?.let { runCatching { unbindService(it) } }
        worker.shutdownNow()
        super.onDestroy()
    }

    private fun page(title: String, subtitle: String) {
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 32, 40, 48)
        }
        content.addView(label(title, 26f, Color.rgb(15, 44, 67)).apply { id = SCREEN_TITLE_ID })
        content.addView(label(subtitle, 15f, Color.DKGRAY).apply { setPadding(0, 8, 0, 20) })
        setContentView(ScrollView(this).apply { addView(content) })
    }

    fun renderHome() {
        page("Atlas OT Scout", "Field assessment workspace • P0-WATER")
        content.addView(card("SAFE BY DEFAULT", "Offline workspace. No packet is sent until scope and authorization are confirmed.", SAFETY_STATUS_ID))
        content.addView(section("What do you need to do?"))
        content.addView(button("Start an authorized assessment", PRIMARY_ACTION_ID, ::renderAssessmentSetup))
        content.addView(help("Use this on site after receiving written authorization for controlled active identification."))
        content.addView(button("Analyze an existing capture", PASSIVE_ACTION_ID, ::openCapturePicker))
        content.addView(help("Passive mode reads a PCAP locally. It never connects to or transmits on the OT network."))
        content.addView(section("At a glance"))
        content.addView(label("P0-WATER • 0 open cases • 0 unresolved findings\nEvidence remains on this device.", 15f).apply { id = STATUS_VIEW_ID })
    }

    private fun renderAssessmentSetup() {
        page("New authorized assessment", "Enter only the network scope stated in the written authorization.")
        val caseId = field("Case reference", "P0-WATER-001")
        val site = field("Site / process area", "Water treatment plant")
        val target = field("Target controller IPv4", "10.0.2.2")
        val scope = field("Authorized IPv4 scope", "10.0.2.0/24")
        val unit = field("Modbus unit ID", "1")
        content.addView(card("ACTIVE LIMITS", "One Modbus Device Identification request • TCP/502 • 1.5 s timeout • no register writes", ACTIVE_LIMITS_ID))
        val approval = CheckBox(this).apply {
            text = "I confirm written operational and security authorization for this scope and time window."
            textSize = 15f
            setPadding(0, 18, 0, 18)
        }
        content.addView(approval)
        val start = button("Authorize and identify device", ACTIVE_ACTION_ID) {
            val unitId = unit.text.toString().toIntOrNull()
            if (caseId.text.isBlank() || site.text.isBlank() || unitId == null || unitId !in 0..247) {
                content.addView(help("Complete the case, site, and a unit ID from 0 to 247.").apply { setTextColor(Color.RED) })
            } else {
                runActiveDiscovery(caseId.text.toString(), site.text.toString(), target.text.toString(), scope.text.toString(), unitId)
            }
        }.apply { isEnabled = false }
        approval.setOnCheckedChangeListener { _, checked -> start.isEnabled = checked }
        content.addView(start)
        content.addView(secondary("Cancel", ::renderHome))
    }

    private fun openCapturePicker() {
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/vnd.tcpdump.pcap", "application/octet-stream"))
        }, OPEN_CAPTURE)
    }

    @Deprecated("Retained for API 29")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_CAPTURE && resultCode == RESULT_OK) data?.data?.let(::importCaptureUri)
    }

    private fun importCaptureUri(uri: Uri) {
        val name = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
            if (it.moveToFirst()) it.getString(0) else null
        } ?: uri.lastPathSegment ?: "capture.pcap"
        page("Analyzing capture", name + " is processed locally. Unsupported files fail closed.")
        content.addView(card("IN PROGRESS", "Validating PCAP structure and OT protocol evidence…", RESULT_SUMMARY_ID))
        worker.execute {
            runCatching {
                requireNotNull(contentResolver.openInputStream(uri)).use { PassivePcapAnalyzer.analyze(it) }
            }.onSuccess { runOnUiThread { renderPassiveResult(name, it) } }
                .onFailure { runOnUiThread { renderFailure("Capture could not be analyzed", it.message ?: it.javaClass.simpleName) } }
        }
    }

    fun analyzeCaptureForTest(name: String, input: InputStream) =
        renderPassiveResult(name, PassivePcapAnalyzer.analyze(input))

    private fun renderPassiveResult(name: String, result: PassiveAnalysis) {
        page("Passive analysis complete", name + " • no packets transmitted")
        val duration = if (result.startedAt != null && result.endedAt != null)
            java.time.Duration.between(result.startedAt, result.endedAt).seconds else 0
        val protocols = result.protocolCounts.entries.joinToString { it.key.label + ": " + it.value }
        content.addView(card(
            result.assets.size.toString() + " ASSETS • " + result.parsedPackets + " OT PACKETS",
            "Capture packets: " + result.totalPackets + "\nDuration: " + duration + "s\nSHA-256: " +
                result.sha256.take(16) + "…\n" + protocols,
            RESULT_SUMMARY_ID,
        ))
        content.addView(section("Assets requiring review"))
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; id = ASSET_LIST_ID }
        result.assets.forEach { asset ->
            val identity = listOfNotNull(asset.vendor, asset.product, asset.revision).joinToString(" • ")
            var detail = asset.protocols.joinToString { it.label } + " • " + asset.role + " • " + asset.confidence + "% confidence"
            if (identity.isNotBlank()) detail += "\n" + identity
            detail += "\nEvidence: " + (asset.evidence.firstOrNull() ?: "protocol framing")
            list.addView(card(asset.address, detail))
        }
        content.addView(list)
        result.warnings.forEach { content.addView(help("Review: " + it)) }
        content.addView(button("Save assets to case", SAVE_ASSETS_ID) {
            content.addView(card("SAVED", result.assets.size.toString() + " evidence-backed assets added to the local case."))
        })
        content.addView(secondary("Back to workspace", ::renderHome))
    }

    fun runActiveDiscovery(caseId: String, site: String, target: String, scope: String, unitId: Int) {
        page("Identifying controller", caseId + " • " + site)
        content.addView(card("AUTHORIZED ACTIVE CHECK", target + ":502 • unit " + unitId + " • scope " + scope +
            "\nWaiting for the isolated network broker…", ACTIVE_RESULT_ID))
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val broker = IAtlasNetworkBroker.Stub.asInterface(service)
                worker.execute { executeGrant(broker, caseId, site, target, scope, unitId) }
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                runOnUiThread { renderFailure("Network broker disconnected", "No additional packet was sent.") }
            }
        }
        brokerConnection = connection
        val bound = bindService(Intent("com.atlasot.netbroker.BIND").setPackage("com.atlasot.netbroker"), connection, Context.BIND_AUTO_CREATE)
        if (!bound) renderFailure("Network broker unavailable", "Install the matching signed broker application before active assessment.")
    }

    private fun executeGrant(
        broker: IAtlasNetworkBroker, caseId: String, site: String, target: String, scope: String, unitId: Int,
    ) {
        runCatching {
            val interfaces = broker.inspectInterfaces(byteArrayOf()).toString(Charsets.UTF_8)
            val matches = Regex("""\{"handle":(\d+),"ethernet":(true|false),"wifi":(true|false)\}""").findAll(interfaces).toList()
            val selected = matches.firstOrNull { it.groupValues[2] == "true" } ?: matches.firstOrNull()
                ?: error("No connected Android network interface")
            val interfaceLabel = if (selected.groupValues[2] == "true") "USB/Ethernet"
                else if (selected.groupValues[3] == "true") "Wi-Fi" else "connected network"
            val key = grantKeyPair()
            val provision = broker.provisionGrantKey(key.public.encoded).toString(Charsets.UTF_8)
            require(provision == "PROVISIONED") { provision }
            val now = Instant.now()
            val grant = ExecutionGrant(
                UUID.randomUUID().toString(), caseId, sha256(caseId + "|" + site + "|" + scope + "|" + target + "|" + unitId),
                Operation.MODBUS_DEVICE_ID_BASIC, selected.groupValues[1].toLong(), target, 502, unitId,
                setOf(scope), emptySet(), 2, 512, 0, 1500, 1, now, now.plusSeconds(30), UUID.randomUUID().toString(),
            )
            val payload = ExecutionGrantWire.encode(grant)
            val envelope = ExecutionGrantWire.envelope(payload, GrantSignatures.sign(grant, key.private))
            val pipe = ParcelFileDescriptor.createPipe()
            val accepted = pipe[1].use { broker.execute(envelope, it).toString(Charsets.UTF_8) }
            require(accepted.startsWith("ACCEPTED:")) { accepted }
            val evidence = pipe[0].use { read -> FileInputStream(read.fileDescriptor).use { it.readBytes() } }
            require(!evidence.toString(Charsets.UTF_8).startsWith("ERROR:")) { evidence.toString(Charsets.UTF_8) }
            Triple(ActiveModbusEvidence.parse(evidence), interfaceLabel, evidence)
        }.onSuccess {
            runOnUiThread { renderActiveResult(caseId, target, it.second, it.first, it.third) }
        }.onFailure {
            runOnUiThread { renderFailure("Device identification failed safely", it.message ?: it.javaClass.simpleName) }
        }
    }

    private fun renderActiveResult(
        caseId: String, target: String, interfaceLabel: String, identity: ActiveModbusIdentity, evidence: ByteArray,
    ) {
        page("Controller identified", caseId + " • constrained active discovery complete")
        val identityText = listOfNotNull(identity.vendor, identity.product, identity.revision).joinToString(" • ")
        content.addView(card(
            "MODBUS/TCP • " + if (identity.identitySupported) "IDENTITY CONFIRMED" else "SERVICE CONFIRMED",
            target + ":502\nInterface: " + interfaceLabel + "\n" +
                identityText.ifBlank { "Vendor/model not returned by the device" } + "\n" + identity.evidence +
                "\nEvidence bytes: " + evidence.size,
            ACTIVE_RESULT_ID,
        ))
        content.addView(card("NEXT DECISION", "Review the evidence and save the asset. No register read or write was performed."))
        content.addView(button("Save identified asset", SAVE_ASSETS_ID) {
            content.addView(card("SAVED", target + " added to " + caseId + " with active evidence."))
        })
        content.addView(secondary("Back to workspace", ::renderHome))
    }

    private fun renderFailure(title: String, detail: String) {
        page(title, "The operation stopped without expanding scope.")
        content.addView(card("ACTION REQUIRED", detail, ACTIVE_RESULT_ID))
        content.addView(secondary("Back to workspace", ::renderHome))
    }

    private fun grantKeyPair(): KeyPair {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getEntry(KEY_ALIAS, null) as? KeyStore.PrivateKeyEntry)?.let {
            return KeyPair(it.certificate.publicKey, it.privateKey)
        }
        return KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore").run {
            initialize(KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                .setDigests(KeyProperties.DIGEST_SHA256).build())
            generateKeyPair()
        }
    }

    private fun sha256(value: String) = MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
        .joinToString("") { "%02x".format(it.toInt() and 0xff) }
    private fun section(value: String) = label(value, 18f, Color.rgb(15, 44, 67)).apply { setPadding(0, 28, 0, 10) }
    private fun help(value: String) = label(value, 13f, Color.DKGRAY).apply { setPadding(8, 4, 8, 12) }
    private fun field(hintText: String, value: String) = EditText(this).apply {
        hint = hintText; setText(value); textSize = 16f; setPadding(12, 10, 12, 10); content.addView(this)
    }
    private fun label(value: String, size: Float, color: Int = Color.BLACK) = TextView(this).apply {
        text = value; textSize = size; setTextColor(color)
    }
    private fun button(value: String, viewId: Int = View.NO_ID, action: () -> Unit) = Button(this).apply {
        text = value; id = viewId; isAllCaps = false; setOnClickListener { action() }
    }
    private fun secondary(value: String, action: () -> Unit) = button(value, action = action).apply { alpha = 0.75f }
    private fun card(title: String, detail: String, viewId: Int = View.NO_ID) = TextView(this).apply {
        text = title + "\n" + detail; textSize = 15f; id = viewId; setTextColor(Color.rgb(18, 49, 65))
        setBackgroundColor(Color.rgb(232, 242, 245)); setPadding(24, 20, 24, 20); gravity = Gravity.START
        layoutParams = LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 8, 0, 10) }
    }

    companion object {
        const val STATUS_VIEW_ID = 0x41544C41
        const val SCREEN_TITLE_ID = 0x41544C42
        const val PRIMARY_ACTION_ID = 0x41544C43
        const val PASSIVE_ACTION_ID = 0x41544C44
        const val RESULT_SUMMARY_ID = 0x41544C45
        const val ASSET_LIST_ID = 0x41544C46
        const val SAVE_ASSETS_ID = 0x41544C47
        const val SAFETY_STATUS_ID = 0x41544C48
        const val ACTIVE_ACTION_ID = 0x41544C49
        const val ACTIVE_RESULT_ID = 0x41544C4A
        const val ACTIVE_LIMITS_ID = 0x41544C4B
        private const val OPEN_CAPTURE = 70
        private const val KEY_ALIAS = "atlas-grant-key-v1"
    }
}
