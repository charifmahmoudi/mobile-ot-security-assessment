package com.atlasot.scout

import android.app.Activity
import android.content.*
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.*
import android.provider.OpenableColumns
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import com.atlasot.domain.*
import com.atlasot.capturebroker.IAtlasCaptureBroker
import com.atlasot.netbroker.IAtlasNetworkBroker
import java.io.FileInputStream
import java.io.InputStream
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.time.Instant
import java.util.UUID
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private enum class WorkspaceSection(val label: String) {
        OVERVIEW("Overview"), COLLECT("Collect"), ASSETS("Assets"), FINDINGS("Findings"), REPORT("Report")
    }

    private val worker = Executors.newSingleThreadExecutor()
    private lateinit var repository: SiteRepository
    private lateinit var professionalCases: ProfessionalCaseApplication
    private lateinit var content: LinearLayout
    private var site: SiteProfile? = null
    private var brokerConnection: ServiceConnection? = null
    private var captureConnection: ServiceConnection? = null
    private var backAction: (() -> Unit)? = null
    private var siteDraft = SiteDraft()

    private data class SiteDraft(
        var name: String = "",
        var location: String = "",
        var industry: String = "Water & wastewater",
        var vendors: List<String> = emptyList(),
    )

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        repository = SiteRepository(this)
        professionalCases = ProfessionalCaseApplication(SqlCipherCaseRepository(this))
        window.statusBarColor = SURFACE
        window.navigationBarColor = SURFACE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        intent?.data?.let {
            site = repository.sites().first()
            importCaptureUri(it)
        } ?: renderSiteSelection()
    }

    override fun onDestroy() {
        brokerConnection?.let { runCatching { unbindService(it) } }
        captureConnection?.let { runCatching { unbindService(it) } }
        worker.shutdownNow()
        super.onDestroy()
    }

    @Deprecated("Screen-specific back behavior")
    override fun onBackPressed() = backAction?.invoke() ?: super.onBackPressed()

    private fun page(
        kicker: String,
        title: String,
        subtitle: String,
        back: (() -> Unit)? = null,
        section: WorkspaceSection? = null,
    ) {
        backAction = back
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(SURFACE)
            setOnApplyWindowInsetsListener { view, insets ->
                view.setPadding(0, insets.systemWindowInsetTop, 0, insets.systemWindowInsetBottom)
                insets
            }
        }
        val scroll = ScrollView(this).apply { isFillViewport = true }
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(20), dp(24), dp(40))
        }
        val header = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        if (back != null) header.addView(TextView(this).apply {
            text = "‹"; textSize = 36f; setTextColor(NAVY); gravity = Gravity.CENTER
            contentDescription = "Back"; setOnClickListener { back() }
        }, LinearLayout.LayoutParams(dp(40), dp(48)))
        val titles = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        titles.addView(txt(kicker.uppercase(), 11f, BLUE, Typeface.BOLD).apply { letterSpacing = .10f })
        titles.addView(txt(title, 25f, NAVY, Typeface.BOLD).apply { id = SCREEN_TITLE_ID })
        titles.addView(txt(subtitle, 14f, MUTED).apply { setPadding(0, dp(4), 0, 0) })
        header.addView(titles, LinearLayout.LayoutParams(0, -2, 1f))
        content.addView(header)
        content.addView(space(14))
        if (section != null) {
            content.addView(stageRail(section))
            content.addView(space(16))
        }
        scroll.addView(content)
        if (section != null) root.addView(fieldSafetyBar())
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        if (section != null) root.addView(workspaceNavigation(section))
        setContentView(root)
    }

    private fun fieldSafetyBar(): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp(18), dp(9), dp(18), dp(9))
        setBackgroundColor(NAVY)
        addView(txt("●  OFFLINE WORKSPACE", 11f, AQUA, Typeface.BOLD).apply { letterSpacing = .04f })
        addView(txt("PASSIVE BY DEFAULT", 11f, WHITE, Typeface.BOLD).apply {
            gravity = Gravity.END
        }, LinearLayout.LayoutParams(0, -2, 1f))
        contentDescription = "Offline field mode. Passive collection is the safe default."
    }

    private fun workspaceNavigation(selected: WorkspaceSection): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        setPadding(dp(6), dp(6), dp(6), dp(8))
        setBackgroundColor(WHITE)
        WorkspaceSection.entries.forEach { destination ->
            addView(TextView(this@MainActivity).apply {
                id = when (destination) {
                    WorkspaceSection.OVERVIEW -> OVERVIEW_NAV_ID
                    WorkspaceSection.COLLECT -> COLLECT_NAV_ID
                    WorkspaceSection.ASSETS -> ASSETS_NAV_ID
                    WorkspaceSection.FINDINGS -> FINDINGS_NAV_ID
                    WorkspaceSection.REPORT -> REPORT_NAV_ID
                }
                text = destination.label
                textSize = 12f
                gravity = Gravity.CENTER
                setTypeface(Typeface.DEFAULT, if (destination == selected) Typeface.BOLD else Typeface.NORMAL)
                setTextColor(if (destination == selected) BLUE else MUTED)
                background = if (destination == selected) rounded(PALE_BLUE, 12) else null
                minHeight = dp(48)
                setOnClickListener {
                    when (destination) {
                        WorkspaceSection.OVERVIEW -> renderWorkspace()
                        WorkspaceSection.COLLECT -> renderScanMenu()
                        WorkspaceSection.ASSETS -> renderInventory()
                        WorkspaceSection.FINDINGS -> renderFindings()
                        WorkspaceSection.REPORT -> renderReportReadiness()
                    }
                }
            }, LinearLayout.LayoutParams(0, dp(50), 1f))
        }
    }

    private fun stageRail(selected: WorkspaceSection): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        contentDescription = "Assessment progress: " + selected.label + " stage"
        WorkspaceSection.entries.forEachIndexed { index, destination ->
            val reached = destination.ordinal <= selected.ordinal
            addView(LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                addView(txt((index + 1).toString(), 12f, if (reached) WHITE else MUTED, Typeface.BOLD).apply {
                    gravity = Gravity.CENTER
                    background = rounded(if (reached) BLUE else WHITE, 40, if (reached) BLUE else BORDER)
                }, LinearLayout.LayoutParams(dp(28), dp(28)))
                addView(txt(destination.label, 11f, if (destination == selected) NAVY else MUTED,
                    if (destination == selected) Typeface.BOLD else Typeface.NORMAL).apply {
                    gravity = Gravity.CENTER; setPadding(0, dp(4), 0, 0)
                })
            }, LinearLayout.LayoutParams(0, -2, 1f))
        }
    }

    fun renderHome() = renderSiteSelection()

    private fun renderSiteSelection() {
        site = null
        page("Atlas OT Scout", "Choose a site", "Resume an evidence-led assessment or establish a new operating context")
        content.addView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; background = rounded(NAVY, 14); setPadding(dp(18), dp(17), dp(18), dp(17))
            addView(txt("FIELD ASSESSMENT WORKSPACE", 10f, AQUA, Typeface.BOLD).apply { letterSpacing = .09f })
            addView(txt("Turn bounded network evidence into a reviewable asset inventory.", 18f, WHITE, Typeface.BOLD).apply { setPadding(0, dp(7), 0, dp(7)) })
            addView(txt("Site context, observations, decisions and report readiness stay together. No packet is sent from this screen.", 13f, Color.rgb(207, 220, 234)))
        }.apply { id = SAFETY_STATUS_ID; layoutParams = margins(0, 0, 0, 14) })
        content.addView(section("Available sites", "Choose the operating context before collecting evidence."))
        repository.sites().forEachIndexed { index, item ->
            content.addView(siteCard(item).apply { if (index == 0) id = SITE_CARD_ID })
        }
        content.addView(button("Create a new site", NEW_SITE_ACTION_ID, false, ::renderNewSite))
        content.addView(section("Professional cases", "Scope, authority and decisions are stored in the encrypted case record."))
        professionalCases.list().forEach { summary ->
            content.addView(card(
                "${summary.caseNumber} · revision ${summary.revision}",
                "${summary.state.name.replace('_', ' ')}\nResume the durable professional workspace",
                accent = if (summary.state == CaseState.AUTHORIZED || summary.state == CaseState.COLLECTING) TEAL else BLUE,
            ).apply {
                setOnClickListener { renderProfessionalCase(summary.id) }
                if (summary.id.value == GoldenCustomerAssessment.CASE_ID) id = PROFESSIONAL_CASE_CARD_ID
            })
        }
        if (professionalCases.load(CaseId(GoldenCustomerAssessment.CASE_ID)) == null) {
            content.addView(button("Prepare Golden Customer Assessment", PROFESSIONAL_CASE_ACTION_ID, false, ::renderGoldenCasePreparation))
        }
        content.addView(section("What the assessment produces", "The product is valuable when it supports a decision—not when it merely counts devices."))
        content.addView(card("CONTROLLED HANDOFF",
            "• Evidence-backed inventory changes\n• Unknown and conflicting identities to resolve\n• Communication observations with stated limitations\n• Explicit readiness blockers before report issue", accent = BLUE))
        content.addView(txt("P0-WATER  •  Case App offline  •  Broker isolated", 12f, MUTED).apply {
            id = STATUS_VIEW_ID; gravity = Gravity.CENTER; setPadding(0, dp(20), 0, 0)
        })
    }

    private fun renderGoldenCasePreparation() {
        val now = Instant.now()
        val fixture = GoldenCustomerAssessment.input(now)
        page("Professional case · prepare", fixture.caseNumber, "Review the bounded assessment before requesting approval", ::renderSiteSelection)
        content.addView(banner("GOLDEN CUSTOMER ASSESSMENT", "Deterministic water-treatment pilot fixture · no packet is sent during preparation"))
        content.addView(section("Operational context", "Evidence remains bound to this legal entity, site and process area."))
        content.addView(keyValue("Customer", fixture.legalEntity))
        content.addView(keyValue("Site", fixture.site))
        content.addView(keyValue("Process area", fixture.processArea))
        content.addView(section("Decision", "Collection exists to answer a named operational question."))
        content.addView(card("ASSESSMENT QUESTION", fixture.question + "\n\nRequested outcome\n" + fixture.requestedDecision, accent = BLUE))
        content.addView(section("Exact authority requested", "The active boundary is one /32 target; Atlas cannot broaden it."))
        content.addView(keyValue("Scope", fixture.scopeCidrs.joinToString()))
        content.addView(keyValue("Exclusion", fixture.excludedAddresses.joinToString()))
        content.addView(keyValue("Methods", "H3 offline import · H1 Modbus 43/14 basic identity"))
        content.addView(keyValue("Window", "Four hours from approval"))
        content.addView(keyValue("Stop", "Process alarm · instability · approver request · manual stop"))
        content.addView(section("Data policy", "Policy changes require a new matching authorization fingerprint."))
        content.addView(keyValue("Classification", fixture.classification))
        content.addView(keyValue("Raw capture export", "Not permitted"))
        content.addView(keyValue("Retention", "30 days"))
        content.addView(keyValue("Destination", fixture.exportDestination.orEmpty()))
        content.addView(section("Named professional roles", "Each action retains the role used, even if customer policy permits one person to hold more than one role."))
        content.addView(keyValue("Assessor", fixture.participants.assessor.displayName))
        content.addView(keyValue("Operational approver", fixture.participants.operationalApprover.displayName))
        content.addView(keyValue("Security approver", fixture.participants.securityApprover.displayName))
        content.addView(keyValue("Independent reviewer", fixture.participants.independentReviewer.displayName))
        val error = txt("", 13f, DANGER).apply { id = PROFESSIONAL_CASE_ERROR_ID }
        content.addView(error)
        content.addView(button("Create case and request approval", CREATE_PROFESSIONAL_CASE_ID) {
            runCatching { professionalCases.createPrepared(fixture, now) }
                .onSuccess { renderProfessionalCase(it.id) }
                .onFailure { error.text = it.message ?: "Unable to create the professional case." }
        })
    }

    private fun renderProfessionalCase(caseId: CaseId) {
        val professionalCase = requireNotNull(professionalCases.load(caseId))
        val participants = requireNotNull(professionalCases.participants(caseId))
        page(
            "Professional case · revision ${professionalCase.revision}",
            professionalCase.caseNumber,
            professionalCase.state.name.replace('_', ' '),
            ::renderSiteSelection,
        )
        content.addView(banner(
            "CURRENT SCOPE AND AUTHORITY",
            "${professionalCase.context.legalEntity} · ${professionalCase.context.site}\n${professionalCase.context.processArea}",
            PROFESSIONAL_CASE_STATUS_ID,
        ))
        content.addView(keyValue("Assessment pack", professionalCase.context.assessmentPack))
        content.addView(section("Assessment purpose", professionalCase.objective.question))
        content.addView(card("REQUESTED DECISION", professionalCase.objective.requestedDecision, accent = BLUE))
        content.addView(section("Boundaries", "These values are part of the authorization fingerprint."))
        content.addView(keyValue("Active scope", professionalCase.scope.cidrs.joinToString(transform = ::cidrToText)))
        content.addView(keyValue("Excluded", professionalCase.scope.excludedAddresses.joinToString { addressToText(it) }))
        content.addView(keyValue("Methods", professionalCase.scope.evidenceMethods.sortedBy { it.name }.joinToString { it.name }))
        content.addView(keyValue("Stop conditions", professionalCase.stopConditions.sortedBy { it.name }.joinToString { it.name }))
        content.addView(keyValue("Data policy", professionalCase.dataPolicy.classification))
        content.addView(keyValue("Raw export", if (professionalCase.dataPolicy.includeRawCapturesInExport) "Permitted" else "Not permitted"))
        content.addView(keyValue("Export destination", professionalCase.dataPolicy.exportDestination ?: "No export destination"))
        content.addView(keyValue("Delete after", professionalCase.dataPolicy.deleteAfter?.toString() ?: "No automatic deletion date"))
        content.addView(keyValue("Authorization window", professionalCase.authorization?.let { "${it.validFrom} — ${it.validUntil}" } ?: "Pending approval"))
        content.addView(section("Professional roles", "Approval identities are recorded in the professional aggregate; the planned reviewer stays in encrypted case metadata."))
        content.addView(keyValue("Assessor", participants.assessor.displayName))
        content.addView(keyValue("Operational approver", participants.operationalApprover.displayName))
        content.addView(keyValue("Security approver", participants.securityApprover.displayName))
        content.addView(keyValue("Independent reviewer", participants.independentReviewer.displayName))

        when (professionalCase.state) {
            CaseState.AWAITING_AUTHORIZATION -> renderCaseAuthorizationControls(professionalCase)
            CaseState.AUTHORIZED -> {
                content.addView(banner("AUTHORIZED", "Both required approvals match scope ${professionalCase.scopeHash.value.take(12)}… and data policy ${professionalCase.dataPolicyHash.value.take(12)}…"))
                content.addView(button("Start protected collection", START_PROFESSIONAL_COLLECTION_ID) {
                    runCatching { professionalCases.startCollection(caseId, Instant.now()) }
                        .onSuccess { renderProfessionalCase(caseId) }
                        .onFailure { renderProfessionalCaseFailure(caseId, it) }
                })
            }
            CaseState.COLLECTING -> content.addView(banner("PROTECTED COLLECTION AVAILABLE", "The Case App will still ask the domain operation guard before issuing any signed broker grant."))
            else -> content.addView(banner("COLLECTION UNAVAILABLE", "The professional lifecycle is ${professionalCase.state.name.replace('_', ' ')}."))
        }
        content.addView(section("Audit trail", "Integrity-verified lifecycle actions restored from encrypted persistence."))
        professionalCase.auditTrail.events.takeLast(8).forEach { event ->
            content.addView(keyValue("#${event.sequence} ${event.type.name}", "${event.actor.displayName} · ${event.actor.role.name}"))
        }
    }

    private fun renderCaseAuthorizationControls(professionalCase: AssessmentCase) {
        content.addView(section("Required approvals", "Protected collection remains unavailable until both actions are recorded."))
        val operations = CheckBox(this).apply {
            id = OPERATIONAL_APPROVAL_ID
            text = "Operational approval · process window and stop authority"
            setTextColor(NAVY)
        }
        val security = CheckBox(this).apply {
            id = SECURITY_APPROVAL_ID
            text = "Security approval · target, method, retention and export"
            setTextColor(NAVY)
        }
        content.addView(operations)
        content.addView(security)
        val error = txt("", 13f, DANGER).apply { id = PROFESSIONAL_CASE_ERROR_ID }
        content.addView(error)
        content.addView(button("Record approvals", RECORD_APPROVALS_ID) {
            val now = Instant.now()
            runCatching {
                val proposal = professionalCases.proposeAuthorization(
                    professionalCase.id,
                    operations.isChecked,
                    security.isChecked,
                    now.minusSeconds(1),
                    now.plusSeconds(4 * 3600),
                    "${professionalCase.caseNumber}|${professionalCase.scopeHash}|${professionalCase.dataPolicyHash}|${now.epochSecond}",
                    now,
                )
                professionalCases.authorize(professionalCase.id, proposal, now.plusMillis(1))
            }.onSuccess { renderProfessionalCase(professionalCase.id) }
                .onFailure { error.text = it.message ?: "Both approvals are required." }
        })
    }

    private fun renderProfessionalCaseFailure(caseId: CaseId, error: Throwable) {
        page("Professional case", "Protected action blocked", "The domain guard failed closed", { renderProfessionalCase(caseId) })
        content.addView(card("ACTION REQUIRED", error.message ?: error.javaClass.simpleName, accent = DANGER))
    }

    private fun addressToText(address: Int): String = listOf(24, 16, 8, 0)
        .joinToString(".") { ((address ushr it) and 0xff).toString() }

    private fun cidrToText(cidr: IPv4Cidr): String = "${addressToText(cidr.network)}/${cidr.prefix}"

    private fun renderNewSite() {
        siteDraft = SiteDraft()
        renderNewSiteBasics()
    }

    private fun renderNewSiteBasics() {
        page("New site · 1 of 3", "Where are you assessing?", "Create the operating context before collecting evidence", ::renderSiteSelection)
        content.addView(setupProgress(1))
        content.addView(section("Site identity", "Use the exact location and process boundary from the authorization."))
        val name = field("Site name", "e.g. East pumping station", SITE_NAME_FIELD_ID, siteDraft.name)
        val location = field("Location / process area", "e.g. Rabat · Booster station 3", SITE_LOCATION_FIELD_ID, siteDraft.location)
        content.addView(section("Industry", "This selects the assessment pack and terminology; it does not infer assets."))
        val industry = Spinner(this).apply {
            id = INDUSTRY_SPINNER_ID
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, INDUSTRIES)
            background = rounded(WHITE, 14, BORDER); setPadding(dp(14), dp(8), dp(14), dp(8))
            setSelection(INDUSTRIES.indexOf(siteDraft.industry).coerceAtLeast(0))
        }
        content.addView(industry, margins(0, 4, 0, 12))
        val error = txt("", 13f, DANGER).apply { id = VALIDATION_MESSAGE_ID }
        content.addView(error)
        content.addView(button("Continue to technology context", CONTINUE_SETUP_ID) {
            if (name.text.isBlank() || location.text.isBlank()) {
                error.text = "Enter the authorized site name and process area to continue."
                name.requestFocus()
            } else {
                siteDraft.name = name.text.toString().trim()
                siteDraft.location = location.text.toString().trim()
                siteDraft.industry = industry.selectedItem.toString()
                renderNewSiteVendors()
            }
        })
        content.addView(txt("Nothing is scanned or transmitted during site setup.", 12f, MUTED).apply {
            gravity = Gravity.CENTER; setPadding(0, dp(10), 0, 0)
        })
    }

    private fun renderNewSiteVendors() {
        page("New site · 2 of 3", "What technology is expected?", siteDraft.name, ::renderNewSiteBasics)
        content.addView(setupProgress(2))
        content.addView(section("Known vendor context", "Optional. Select vendors from drawings, contracts or the existing inventory."))
        content.addView(banner("CONTEXT, NOT DISCOVERY", "Selections improve filters and interview prompts. They never become identified assets without evidence."))
        val grid = GridLayout(this).apply { columnCount = 2; useDefaultMargins = false }
        val checks = VENDORS.map { vendor ->
            CheckBox(this).apply {
                text = vendor; textSize = 14f; setTextColor(NAVY); isChecked = vendor in siteDraft.vendors
                buttonTintList = android.content.res.ColorStateList.valueOf(BLUE)
                setPadding(0, dp(6), dp(6), dp(6))
                grid.addView(this, GridLayout.LayoutParams().apply {
                    width = 0; height = -2; columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                })
            }
        }
        content.addView(grid)
        content.addView(button("Continue to workspace settings", VENDORS_CONTINUE_ID) {
            siteDraft.vendors = checks.filter { it.isChecked }.map { it.text.toString() }
            renderNewSiteSettings()
        })
        content.addView(button("Skip vendor context", primary = false) {
            siteDraft.vendors = emptyList(); renderNewSiteSettings()
        })
    }

    private fun renderNewSiteSettings() {
        page("New site · 3 of 3", "Review and create", siteDraft.name, ::renderNewSiteVendors)
        content.addView(setupProgress(3))
        content.addView(card("OPERATING CONTEXT", siteDraft.industry + "\n" + siteDraft.location + "\n" +
            if (siteDraft.vendors.isEmpty()) "Vendor context not recorded" else siteDraft.vendors.joinToString(" · "), accent = BLUE))
        content.addView(section("Field and report settings", "Choose the working language and local evidence-retention window."))
        content.addView(txt("Report language", 13f, MUTED, Typeface.BOLD).apply { setPadding(dp(2), dp(6), 0, dp(5)) })
        val language = Spinner(this).apply {
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item,
                listOf("French", "Arabic", "English"))
            background = rounded(WHITE, 14, BORDER); setPadding(dp(14), dp(8), dp(14), dp(8))
        }
        content.addView(language, margins(0, 0, 0, 10))
        content.addView(txt("Local retention", 13f, MUTED, Typeface.BOLD).apply { setPadding(dp(2), dp(6), 0, dp(5)) })
        val retention = Spinner(this).apply {
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item,
                listOf("7 days", "30 days", "90 days"))
            setSelection(1)
            background = rounded(WHITE, 14, BORDER); setPadding(dp(14), dp(8), dp(14), dp(8))
        }
        content.addView(retention, margins(0, 0, 0, 10))
        content.addView(button("Create site workspace", CREATE_SITE_ACTION_ID) {
            site = repository.addSite(siteDraft.name, siteDraft.location, siteDraft.industry, siteDraft.vendors,
                language.selectedItem.toString(), retention.selectedItem.toString().substringBefore(' ').toInt())
            renderWorkspace()
        })
        content.addView(button("Back to vendor context", primary = false, action = ::renderNewSiteVendors))
    }

    private fun renderWorkspace() {
        val current = requireNotNull(site)
        val assets = repository.assets(current.id)
        page(current.industry, current.name, current.location, ::renderSiteSelection, WorkspaceSection.OVERVIEW)
        content.addView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; background = rounded(NAVY, 18); setPadding(dp(18), dp(15), dp(18), dp(15))
            addView(txt(if (current.sample) "DEMONSTRATION CONTEXT" else "AUTHORIZED SITE CONTEXT", 10f, AQUA, Typeface.BOLD).apply { letterSpacing = .09f })
            addView(txt(current.vendors.ifEmpty { listOf("Vendors not recorded") }.joinToString("  ·  "), 14f, WHITE).apply { setPadding(0, dp(6), 0, 0) })
        })
        val reviewCount = assets.count { it.reviewState == "Needs review" }
        val nextTitle = when {
            assets.isEmpty() -> "Begin passive collection"
            reviewCount > 0 -> "Review $reviewCount unresolved assets"
            else -> "Review assessment findings"
        }
        val nextBody = when {
            assets.isEmpty() -> "Start with a SPAN/TAP capture or import an approved PCAP file."
            reviewCount > 0 -> "Resolve identity uncertainty before making risk conclusions."
            else -> "Turn the reconciled evidence model into defensible observations."
        }
        content.addView(section("Do this next", "One recommended action keeps the assessment moving without expanding scope."))
        content.addView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(PALE_BLUE, 18, BLUE)
            setPadding(dp(18), dp(16), dp(18), dp(16))
            addView(txt(nextTitle, 18f, NAVY, Typeface.BOLD))
            addView(txt(nextBody, 14f, MUTED).apply { setPadding(0, dp(5), 0, dp(8)) })
            addView(button(when {
                assets.isEmpty() -> "Choose an evidence method"
                reviewCount > 0 -> "Open the review queue"
                else -> "Review draft findings"
            }, CONTINUE_ACTION_ID) {
                when {
                    assets.isEmpty() -> renderScanMenu()
                    reviewCount > 0 -> renderInventory("Needs review")
                    else -> renderFindings()
                }
            })
        })
        content.addView(section("Assessment status", "A concise view of evidence already in the workspace."))
        val metrics = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        metrics.addView(metric(assets.size.toString(), "Known assets"), weight(8))
        metrics.addView(metric(assets.map { it.protocol }.distinct().size.toString(), "Protocols"), weight(8))
        metrics.addView(metric(assets.count { it.reviewState == "Needs review" }.toString(), "To review"), weight(0))
        content.addView(metrics)
        content.addView(LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(button("Collect evidence", PRIMARY_ACTION_ID, false, ::renderScanMenu),
                LinearLayout.LayoutParams(0, dp(54), 1f).apply { marginEnd = dp(8) })
            addView(button("Open inventory", INVENTORY_ACTION_ID, false, ::renderInventory),
                LinearLayout.LayoutParams(0, dp(54), 1f))
        })
        content.addView(section("Deliverable in progress", "The customer handoff is assembled from reviewed evidence, not raw scanner output."))
        content.addView(card("ASSESSMENT PACKAGE",
            "Inventory delta  ·  Identity review queue\nCommunication picture  ·  Findings with limitations\nAuthorization ledger  ·  Report-readiness status", accent = SLATE))
        content.addView(section("Evidence coverage", "Use the model to decide which uncertainty is worth closing next."))
        val roles = assets.groupingBy { it.role.substringBefore('/') }.eachCount().entries
            .sortedByDescending { it.value }.joinToString("  ·  ") { it.value.toString() + " " + it.key.lowercase() }
        val review = assets.count { it.reviewState == "Needs review" }
        val insight = if (assets.isEmpty()) "No assets yet. Begin with a passive capture or one approved identity check."
            else roles + "\nPriority: " + review + " observation" + if (review == 1) " requires" else "s require" + " identity review."
        content.addView(card("CURRENT EVIDENCE MODEL", insight, NETWORK_INSIGHT_ID, BLUE))
        if (assets.isNotEmpty()) {
            content.addView(section("Recent assets", "Open the inventory for filters and evidence detail."))
            assets.take(3).forEach { asset -> content.addView(assetCard(asset) { renderAssetDetail(asset) }) }
        }
    }

    private fun renderScanMenu() {
        val current = requireNotNull(site)
        page("Collect evidence", "Choose a method", current.name, ::renderWorkspace, WorkspaceSection.COLLECT)
        content.addView(card("STEP 2 OF 5 · COLLECT",
            "Start with the evidence gap. Then choose the least intrusive method that can close it.", accent = BLUE))
        content.addView(banner("SAFETY POSITION", "Passive collection never transmits. Active identity remains locked behind exact scope and written authorization."))
        content.addView(section("What do you need to establish?", "Every method names the decision it supports, its network effect and its prerequisite."))
        content.addView(method("CURRENT COMMUNICATION PICTURE", "Observe a SPAN / TAP interface", "Build time-bounded evidence of endpoints, roles and protocols from approved mirrored traffic.", "Passive · qualified appliance required", LIVE_CAPTURE_OPTION_ID, ::renderLiveCaptureSetup))
        content.addView(method("CUSTOMER-SUPPLIED EVIDENCE", "Analyze PCAP / PCAPNG", "Turn an approved capture into a reviewable inventory delta without touching the OT network.", "Passive · no packets sent", PASSIVE_ACTION_ID, ::openCapturePicker))
        content.addView(method("ONE UNRESOLVED IDENTITY", "Identify one known controller", "Ask one exact allowlisted Modbus target for its basic device identity.", "Active · written authorization required", ACTIVE_SCAN_OPTION_ID, ::renderAssessmentSetup))
        content.addView(section("Planned collection packs", "Roadmap items are visually separated from working capability."))
        content.addView(card("PLANNED · WI-FI OBSERVATION", "Inventory approved SSIDs and access-point evidence.", accent = MUTED).apply { alpha = .65f })
        content.addView(card("PLANNED · BLUETOOTH OBSERVATION", "Record nearby industrial BLE identity signals.", accent = MUTED).apply { alpha = .65f })
    }

    private fun renderLiveCaptureSetup() {
        val current = requireNotNull(site)
        page("Live passive capture", "Connect SPAN / TAP", current.name, ::renderScanMenu)
        content.addView(banner("PASSIVE INTERFACE", "The capture interface must have no IPv4/IPv6 address and kernel egress must remain blocked."))
        content.addView(card("REQUIRED CONNECTION", "Approved SPAN port or network TAP  ·  qualified USB Ethernet  ·  signed appliance image", LIVE_CAPTURE_STATUS_ID, TEAL))
        content.addView(section("Appliance check", "The Case App cannot open raw sockets. It asks the separately signed capture boundary to inspect the dedicated interface."))
        val state = card("CHECKING CAPABILITY", "Looking for the Passive Capture Broker…", CAPTURE_CAPABILITY_ID, BLUE)
        content.addView(state)
        val start = button("Start 30-second passive sample", LIVE_CAPTURE_ACTION_ID) { }.apply { isEnabled = false; alpha = .45f }
        content.addView(start)

        captureConnection?.let { runCatching { unbindService(it) } }
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val broker = IAtlasCaptureBroker.Stub.asInterface(service)
                runCatching { broker.inspectInterfaces().toString(Charsets.UTF_8) }
                    .onSuccess { capability ->
                        val available = capability.contains("\"available\":true")
                        val emulated = capability.contains("EMULATED_APPLIANCE")
                        state.removeAllViews()
                        state.addView(txt(if (available) "INTERFACE READY" else "APPLIANCE NOT READY", 11f,
                            if (available) TEAL else DANGER, Typeface.BOLD).apply { letterSpacing = .07f })
                        state.addView(txt(if (available)
                            "USB Ethernet · SPAN/TAP\nNo address assigned · receive-only policy\nBackend: " + if (emulated) "CI emulation" else "native capture daemon"
                        else "Install and attest the signed appliance capture service before field use.", 14f, NAVY).apply { setPadding(0, dp(6), 0, 0) })
                        start.isEnabled = available; start.alpha = if (available) 1f else .45f
                        start.setOnClickListener { runLiveCapture(broker, emulated) }
                    }
                    .onFailure { state.removeAllViews(); state.addView(txt("CAPABILITY CHECK FAILED", 12f, DANGER, Typeface.BOLD)) }
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                start.isEnabled = false; start.alpha = .45f
            }
        }
        captureConnection = connection
        if (!bindService(Intent("com.atlasot.capturebroker.BIND").setPackage("com.atlasot.capturebroker"), connection, Context.BIND_AUTO_CREATE)) {
            state.removeAllViews()
            state.addView(txt("PASSIVE CAPTURE BROKER UNAVAILABLE", 12f, DANGER, Typeface.BOLD))
            state.addView(txt("Use imported PCAP/PCAPNG until the dedicated appliance image is installed.", 14f, NAVY).apply { setPadding(0, dp(6), 0, 0) })
        }
    }

    private fun runLiveCapture(broker: IAtlasCaptureBroker, emulated: Boolean) {
        page("Live passive capture", "Observing SPAN / TAP", "30-second bounded sample · zero intended transmission", ::renderLiveCaptureSetup)
        content.addView(card("CAPTURE ACTIVE", "Interface span0  ·  16 MiB limit  ·  analyzer isolated after collection" +
            if (emulated) "\nCI uses a labeled replay stream; this is not rooted-hardware qualification." else "", LIVE_CAPTURE_STATUS_ID, BLUE))
        val pipe = ParcelFileDescriptor.createPipe()
        val accepted = runCatching { pipe[1].use { broker.startPassiveCapture("span0", 16L * 1024 * 1024, 30_000, it) }.toString(Charsets.UTF_8) }
            .getOrElse {
                pipe[0].close(); renderFailure("Live capture stopped safely", it.message ?: it.javaClass.simpleName); return
            }
        if (!accepted.startsWith("ACCEPTED:")) {
            pipe[0].close(); renderFailure("Live capture unavailable", accepted); return
        }
        worker.execute {
            runCatching { pipe[0].use { descriptor -> FileInputStream(descriptor.fileDescriptor).use { PassivePcapAnalyzer.analyze(it) } } }
                .onSuccess { result -> runOnUiThread { renderPassiveResult(if (emulated) "CI SPAN replay" else "Live SPAN sample", result) } }
                .onFailure { error -> runOnUiThread { renderFailure("Live capture could not be analyzed", error.message ?: error.javaClass.simpleName) } }
        }
    }

    private fun renderAssessmentSetup() {
        val current = requireNotNull(site)
        page("Authorized active check", "Identify one Modbus device", current.name, ::renderScanMenu)
        val collectingSummary = professionalCases.list().firstOrNull { it.state == CaseState.COLLECTING }
        if (collectingSummary == null) {
            content.addView(card(
                "PROFESSIONAL AUTHORITY REQUIRED",
                "A confirmation checkbox cannot authorize network activity. Create the professional case, record both approvals, and start its protected collection window first. No register reads or writes are available.",
                ACTIVE_LIMITS_ID,
                AMBER,
            ))
            content.addView(button("Open professional cases", PROFESSIONAL_CASE_ACTION_ID, false, ::renderSiteSelection))
            return
        }
        val collectingCase = requireNotNull(professionalCases.load(collectingSummary.id))
        content.addView(step("1", "Work order"))
        val caseId = field("Case reference", "Professional case", CASE_FIELD_ID, collectingCase.id.value).apply { isEnabled = false }
        field("Process area", "Bound professional context", SITE_FIELD_ID, collectingCase.context.processArea).apply { isEnabled = false }
        content.addView(step("2", "Exact target and scope"))
        val allowedCidrs = collectingCase.scope.cidrs
            .sortedWith(compareBy<IPv4Cidr> { it.network }.thenBy { it.prefix })
            .map(::cidrToText)
        val defaultTarget = collectingCase.scope.cidrs
            .firstOrNull { it.prefix == 32 }
            ?.let { addressToText(it.network) }
            .orEmpty()
        val target = field("Target controller IPv4", "Canonical IPv4 address", TARGET_FIELD_ID, defaultTarget)
        field("Authorized IPv4 scope (CIDR)", "Professional case allowlist", SCOPE_FIELD_ID, allowedCidrs.joinToString()).apply { isEnabled = false }
        val unit = field("Modbus unit ID", "0–247", UNIT_FIELD_ID, "1")
        content.addView(banner("ACTIVE LIMITS", "1 identity request  ·  TCP/502  ·  1.5 s timeout  ·  no register reads or writes", ACTIVE_LIMITS_ID))
        content.addView(step("3", "Domain authorization"))
        content.addView(banner("AUTHORIZED CASE", "${collectingCase.caseNumber} · ${collectingCase.authorization?.id}\nScope and data-policy fingerprints verified after encrypted restore."))
        val error = txt("", 13f, DANGER).apply { id = VALIDATION_MESSAGE_ID }
        content.addView(error)
        val start = button("Identify within case authority", ACTIVE_ACTION_ID) {
            val unitId = unit.text.toString().toIntOrNull()
            val valid = runCatching {
                require(unitId != null && unitId in 0..247) { "Use a Modbus unit ID from 0 to 247." }
                professionalCases.assertOperationAllowed(collectingCase.id, target.text.toString(), Instant.now())
            }
            if (valid.isFailure) error.text = valid.exceptionOrNull()?.message ?: "Review the fields."
            else runActiveDiscovery(caseId.text.toString(), target.text.toString(), unitId!!)
        }
        content.addView(start)
    }

    private fun openCapturePicker() {
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE); type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/vnd.tcpdump.pcap", "application/x-pcapng", "application/octet-stream"))
        }, OPEN_CAPTURE)
    }

    @Deprecated("Retained for API 29")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_CAPTURE && resultCode == RESULT_OK) data?.data?.let(::importCaptureUri)
    }

    private fun importCaptureUri(uri: Uri) {
        if (site == null) site = repository.sites().first()
        val name = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
            if (it.moveToFirst()) it.getString(0) else null
        } ?: uri.lastPathSegment ?: "capture.pcap"
        page("Passive analysis", "Analyzing capture", name + " · processed locally", ::renderScanMenu)
        content.addView(card("VALIDATING CAPTURE", "Checking packet bounds and supported OT protocol evidence…", RESULT_SUMMARY_ID, BLUE))
        worker.execute {
            runCatching { requireNotNull(contentResolver.openInputStream(uri)).use { PassivePcapAnalyzer.analyze(it) } }
                .onSuccess { runOnUiThread { renderPassiveResult(name, it) } }
                .onFailure { runOnUiThread { renderFailure("Capture could not be analyzed", it.message ?: it.javaClass.simpleName) } }
        }
    }

    fun analyzeCaptureForTest(name: String, input: InputStream) = renderPassiveResult(name, PassivePcapAnalyzer.analyze(input))

    private fun renderPassiveResult(name: String, result: PassiveAnalysis) {
        val current = requireNotNull(site)
        page("Passive evidence", result.assets.size.toString() + " assets observed · review required", name + " · no packets transmitted", ::renderScanMenu, WorkspaceSection.ASSETS)
        val duration = if (result.startedAt != null && result.endedAt != null)
            java.time.Duration.between(result.startedAt, result.endedAt).seconds else 0
        content.addView(summary(result.assets.size.toString(), "Assets", result.parsedPackets.toString(), "OT packets", duration.toString() + "s", "Window", RESULT_SUMMARY_ID))
        content.addView(txt(result.protocolCounts.entries.joinToString("  ·  ") { it.key.label + " " + it.value }, 14f, NAVY, Typeface.BOLD).apply { setPadding(0, dp(12), 0, dp(4)) })
        content.addView(txt("SHA-256  " + result.sha256.take(20) + "…", 12f, MUTED))
        content.addView(banner("ANALYST DECISION", if (result.assets.isEmpty())
            "No supported OT evidence was found. This does not prove the segment is empty."
        else "This capture is a visibility sample. Review role, confidence and evidence before adding observations."))
        content.addView(card("STEP 3 OF 5 · REVIEW", "Accept only observations supported by this evidence window. Unselected records remain outside inventory.", accent = BLUE))
        content.addView(section("Observations to review", "Nothing enters the inventory until you explicitly accept it."))
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; id = ASSET_LIST_ID }
        val accepted = mutableSetOf<Int>()
        var save: Button? = null
        result.assets.forEachIndexed { index, asset ->
            val identity = listOfNotNull(asset.vendor, asset.product, asset.revision).joinToString(" · ")
            val detail = asset.protocols.joinToString { it.label } + "  ·  " + asset.role + "\n" +
                asset.confidence + "% confidence" + if (identity.isBlank()) "" else "  ·  " + identity +
                "\n" + (asset.evidence.firstOrNull() ?: "Protocol framing")
            list.addView(LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = rounded(WHITE, 16, BORDER)
                elevation = dp(1).toFloat()
                setPadding(dp(16), dp(14), dp(16), dp(12))
                layoutParams = margins(0, 0, 0, 10)
                addView(txt(asset.address, 14f, if (asset.confidence >= 90) TEAL else AMBER, Typeface.BOLD))
                addView(txt(detail, 14f, NAVY).apply { setPadding(0, dp(6), 0, dp(8)); setLineSpacing(0f, 1.12f) })
                addView(CheckBox(this@MainActivity).apply {
                    text = "Accept into site inventory"
                    textSize = 13f
                    setTextColor(NAVY)
                    buttonTintList = android.content.res.ColorStateList.valueOf(TEAL)
                    setOnCheckedChangeListener { _, checked ->
                        if (checked) accepted += index else accepted -= index
                        save?.let {
                            it.text = "Add ${accepted.size} selected observations"
                            it.isEnabled = accepted.isNotEmpty()
                            it.alpha = if (accepted.isNotEmpty()) 1f else .45f
                        }
                    }
                })
            })
        }
        content.addView(list)
        save = button("Add 0 selected observations", SAVE_ASSETS_ID) {
            repository.addAssets(current.id, result.assets.filterIndexed { index, _ -> index in accepted }.map { asset ->
                InventoryAsset(UUID.randomUUID().toString(), current.id, asset.address,
                    asset.product ?: asset.vendor ?: asset.role.replaceFirstChar { it.uppercase() },
                    asset.protocols.firstOrNull()?.label ?: "OT protocol", asset.role, asset.vendor, asset.product,
                    asset.confidence, "Passive capture · " + name, asset.evidence.firstOrNull() ?: "Validated protocol framing",
                    if (asset.confidence >= 90 && asset.vendor != null) "Confirmed" else "Needs review")
            })
            renderInventory()
        }.apply { isEnabled = false; alpha = .45f }
        content.addView(checkNotNull(save))
    }

    private fun renderInventory() = renderInventory("All assets")

    private fun renderInventory(initialFilter: String) {
        val current = requireNotNull(site)
        val all = repository.assets(current.id)
        page("Asset inventory", current.name, "Navigate, filter and reason about the current network model", ::renderWorkspace, WorkspaceSection.ASSETS)
        val pending = all.count { it.reviewState == "Needs review" }
        content.addView(card(if (pending > 0) "$pending DECISION" + if (pending == 1) " PENDING" else "S PENDING" else "REVIEW QUEUE CLEAR",
            if (pending > 0) "Open each review item and decide whether to corroborate, merge or leave it unresolved."
            else "No asset identity currently requires review. Continue validating coverage and expected changes.", accent = if (pending > 0) AMBER else SUCCESS))
        content.addView(LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(button("List", primary = true, action = { }), LinearLayout.LayoutParams(0, dp(54), 1f).apply { marginEnd = dp(8) })
            addView(button("Zone map", primary = false, action = ::renderNetworkMap), LinearLayout.LayoutParams(0, dp(54), 1f))
        })
        val search = field("Search inventory", "Address, name, vendor, protocol or role", INVENTORY_SEARCH_ID, "")
        val filter = Spinner(this).apply {
            id = INVENTORY_FILTER_ID
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item,
                listOf("All assets", "Needs review", "Controllers", "HMIs / clients", "Gateways", "Passive evidence", "Active evidence"))
            background = rounded(WHITE, 14, BORDER); setPadding(dp(14), dp(8), dp(14), dp(8))
        }
        val initialIndex = listOf("All assets", "Needs review", "Controllers", "HMIs / clients", "Gateways", "Passive evidence", "Active evidence")
            .indexOf(initialFilter)
        if (initialIndex >= 0) filter.setSelection(initialIndex)
        content.addView(filter, margins(0, 8, 0, 12))
        val count = txt("", 12f, MUTED, Typeface.BOLD)
        content.addView(count)
        content.addView(card("EVIDENCE COVERAGE", networkInsight(all), NETWORK_INSIGHT_ID, SLATE))
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; id = INVENTORY_LIST_ID }
        content.addView(list)
        fun refresh() {
            val q = search.text.toString().trim().lowercase()
            val f = filter.selectedItem?.toString() ?: "All assets"
            val visible = all.filter { asset ->
                val queryMatch = q.isBlank() || listOfNotNull(asset.address, asset.displayName, asset.vendor, asset.product, asset.protocol, asset.role)
                    .any { it.lowercase().contains(q) }
                val filterMatch = when (f) {
                    "Needs review" -> asset.reviewState == "Needs review"
                    "Controllers" -> asset.role.contains("controller", true)
                    "HMIs / clients" -> asset.role.contains("hmi", true) || asset.role.contains("client", true)
                    "Gateways" -> asset.role.contains("gateway", true)
                    "Passive evidence" -> asset.source.contains("passive", true)
                    "Active evidence" -> asset.source.contains("active", true)
                    else -> true
                }
                queryMatch && filterMatch
            }
            count.text = visible.size.toString() + " of " + all.size + " assets"
            list.removeAllViews()
            if (visible.isEmpty()) list.addView(card("NO MATCHING ASSETS", "Change the search or filter, or collect additional evidence.", accent = MUTED))
            visible.forEach { asset -> list.addView(assetCard(asset) { renderAssetDetail(asset) }) }
        }
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = refresh()
            override fun afterTextChanged(s: Editable?) = Unit
        })
        filter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = refresh()
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        refresh()
    }

    private fun renderNetworkMap() {
        val current = requireNotNull(site)
        val assets = repository.assets(current.id)
        page("Network model", current.name, "Process-zone view · derived from reviewed inventory", ::renderInventory, WorkspaceSection.ASSETS)
        content.addView(card("ZONE VIEW · NOT A COMPLETE TOPOLOGY",
            "Relationships are evidence-scoped. Missing traffic does not prove that a connection does not exist.", accent = AMBER))
        if (assets.isEmpty()) {
            content.addView(card("NO NETWORK MODEL YET", "Collect passive evidence or import an approved inventory baseline.", accent = MUTED))
            content.addView(button("Collect evidence", action = ::renderScanMenu))
            return
        }
        val zones = linkedMapOf(
            "Supervisory" to assets.filter { it.role.contains("HMI", true) || it.role.contains("client", true) },
            "Control" to assets.filter { it.role.contains("controller", true) || it.role.contains("server", true) },
            "Cell integration" to assets.filter { it.role.contains("gateway", true) },
            "Field" to assets.filter { it.role.contains("field", true) || it.role.contains("instrument", true) },
        ).filterValues { it.isNotEmpty() }
        content.addView(section("Observed process zones", "Start with functional boundaries, then drill into individual assets."))
        zones.forEach { (zone, members) ->
            content.addView(card(zone.uppercase(), members.joinToString("\n") {
                it.displayName + "  ·  " + it.address + "  ·  " + it.protocol
            }, accent = when (zone) {
                "Supervisory" -> BLUE
                "Control" -> TEAL
                "Cell integration" -> AMBER
                else -> MUTED
            }))
        }
        val protocols = assets.groupBy { it.protocol }.entries.sortedByDescending { it.value.size }
        content.addView(section("Communication picture", "Protocol groupings that deserve validation against drawings and expected conduits."))
        protocols.forEach { (protocol, members) ->
            content.addView(keyValue(protocol, members.size.toString() + " observed assets"))
        }
        content.addView(button("Return to inventory list", primary = false, action = ::renderInventory))
    }

    private fun renderFindings() {
        val current = requireNotNull(site)
        val assets = repository.assets(current.id)
        val unresolved = assets.filter { it.reviewState == "Needs review" }
        val cleartextProtocols = assets.map { it.protocol }.filter {
            it.contains("Modbus", true) || it.contains("IEC 60870", true) || it.contains("DNP3", true)
        }.distinct()
        page("Assessment findings", current.name, "Evidence-linked observations · severity and confidence kept separate", ::renderWorkspace, WorkspaceSection.FINDINGS)
        content.addView(card("STEP 4 OF 5 · REASON",
            "A finding explains the condition, evidence, consequence and required validation. Device counts alone are not findings.", accent = BLUE))
        if (assets.isEmpty()) {
            content.addView(card("NOT READY FOR FINDINGS", "No reviewed asset evidence is available for this site.", accent = AMBER))
            content.addView(button("Collect evidence", action = ::renderScanMenu))
            return
        }
        content.addView(summary(
            (unresolved.size + if (cleartextProtocols.isEmpty()) 0 else 1).toString(), "Drafts",
            unresolved.size.toString(), "Need review",
            "0", "Final", FINDINGS_SUMMARY_ID,
        ))
        if (unresolved.isNotEmpty()) {
            content.addView(section("Identity and inventory", "Resolve uncertainty before assigning ownership or consequence."))
            content.addView(findingCard(
                "F-001 · Unresolved asset identities",
                "REVIEW REQUIRED", "Confidence: High",
                unresolved.joinToString("\n") { it.displayName + "  ·  " + it.address + "  ·  " + it.confidence + "% identity confidence" },
                "Corroborate with a nameplate, approved baseline or authorized identity request.",
            ))
        }
        if (cleartextProtocols.isNotEmpty()) {
            content.addView(section("Communication characteristics", "Document protocol design in its operational context."))
            content.addView(findingCard(
                "F-002 · Legacy cleartext OT communication observed",
                "CONTEXT REQUIRED", "Confidence: High",
                "Observed protocol evidence: " + cleartextProtocols.joinToString() + ". This observation alone does not establish exploitability or business consequence.",
                "Validate segmentation, authorized peers, remote paths and compensating controls before assigning severity.",
            ))
        }
        if (unresolved.isEmpty() && cleartextProtocols.isEmpty()) {
            content.addView(card("NO DETERMINISTIC DRAFTS", "The current evidence does not trigger a supported finding rule. Continue review; this is not a clean-bill-of-health statement.", accent = TEAL))
        }
        content.addView(button("Review report readiness", action = ::renderReportReadiness))
    }

    private fun findingCard(title: String, status: String, confidence: String, evidence: String, next: String): View =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(WHITE, 18, BORDER)
            elevation = dp(1).toFloat()
            setPadding(dp(17), dp(16), dp(17), dp(16))
            layoutParams = margins(0, 0, 0, 12)
            addView(txt(status, 11f, AMBER, Typeface.BOLD).apply { letterSpacing = .07f })
            addView(txt(title, 17f, NAVY, Typeface.BOLD).apply { setPadding(0, dp(5), 0, dp(6)) })
            addView(txt(confidence, 12f, TEAL, Typeface.BOLD))
            addView(txt(evidence, 14f, NAVY).apply { setPadding(0, dp(10), 0, dp(10)); setLineSpacing(0f, 1.12f) })
            addView(txt("NEXT DECISION", 11f, BLUE, Typeface.BOLD))
            addView(txt(next, 13f, MUTED).apply { setPadding(0, dp(4), 0, 0) })
        }

    private fun renderReportReadiness() {
        val current = requireNotNull(site)
        val assets = repository.assets(current.id)
        val unresolved = assets.count { it.reviewState == "Needs review" }
        page("Report readiness", current.name, "Professional handoff checklist · " + current.reportLanguage, ::renderWorkspace, WorkspaceSection.REPORT)
        content.addView(card("STEP 5 OF 5 · REPORT",
            "The report remains blocked until required context, review and approval records are complete.", accent = BLUE))
        content.addView(section("Readiness checks", "Green checks are supported by current local case data; amber items require action."))
        content.addView(readinessRow(true, "Site context", current.industry + "  ·  " + current.location))
        content.addView(readinessRow(assets.isNotEmpty(), "Evidence-backed inventory",
            if (assets.isEmpty()) "No assets recorded" else assets.size.toString() + " assets with provenance"))
        content.addView(readinessRow(unresolved == 0 && assets.isNotEmpty(), "Observation review",
            if (unresolved == 0 && assets.isNotEmpty()) "No unresolved identities" else "$unresolved assets require review"))
        content.addView(readinessRow(false, "Signed authorization record", "Persist operational and security approvals in the case record"))
        content.addView(readinessRow(false, "Independent reviewer", "Assign a reviewer before finalization"))
        content.addView(section("Deliverable", "The final package will contain a signed PDF, machine-readable JSON, inventory CSV and evidence manifest."))
        content.addView(card("REPORT SETTINGS", current.reportLanguage + "  ·  Local retention " + current.retentionDays + " days\n" +
            "Timestamps: Africa/Casablanca  ·  Evidence hashes: SHA-256", accent = TEAL))
        val ready = assets.isNotEmpty() && unresolved == 0
        content.addView(button(if (ready) "Preview draft report" else "Resolve readiness blockers", REPORT_ACTION_ID, ready) {
            if (ready) renderReportPreview() else renderInventory(if (unresolved > 0) "Needs review" else "All assets")
        })
        content.addView(txt("Final signing and export remain disabled in this PoC until encrypted case storage and reviewer approval are implemented.", 12f, MUTED).apply {
            setPadding(0, dp(10), 0, 0)
        })
    }

    private fun readinessRow(complete: Boolean, title: String, detail: String): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.TOP
        background = rounded(WHITE, 14, BORDER)
        setPadding(dp(14), dp(12), dp(14), dp(12))
        layoutParams = margins(0, 0, 0, 8)
        addView(txt(if (complete) "✓" else "!", 18f, if (complete) TEAL else AMBER, Typeface.BOLD).apply { gravity = Gravity.CENTER },
            LinearLayout.LayoutParams(dp(32), dp(32)))
        addView(LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            addView(txt(title, 14f, NAVY, Typeface.BOLD))
            addView(txt(detail, 12f, MUTED).apply { setPadding(0, dp(3), 0, 0) })
        }, LinearLayout.LayoutParams(0, -2, 1f))
    }

    private fun renderReportPreview() {
        val current = requireNotNull(site)
        val assets = repository.assets(current.id)
        page("Draft report", current.name, "Preview only · not signed", ::renderReportReadiness, WorkspaceSection.REPORT)
        content.addView(banner("DRAFT · NOT FOR ISSUE", "This preview demonstrates report structure. It is not a certification or finalized audit deliverable."))
        content.addView(section("Executive summary", "Assessment scope and decision-relevant results."))
        content.addView(card("SITE AND SCOPE", current.industry + "\n" + current.location + "\n" + assets.size + " evidence-backed assets", accent = NAVY))
        content.addView(section("Technical inventory", "Every record retains its evidence source and confidence."))
        assets.take(8).forEach { content.addView(assetCard(it) { renderAssetDetail(it) }) }
        content.addView(section("Evidence manifest", "Generated package contents and cryptographic hashes."))
        content.addView(card("EXPORT BLOCKED", "Encrypted case persistence, reviewer signature and deterministic PDF generation are required before issue.", accent = AMBER))
    }

    private fun renderAssetDetail(asset: InventoryAsset) {
        page("Asset evidence", asset.displayName, asset.address, ::renderInventory)
        content.addView(pill(asset.reviewState))
        content.addView(section("Identity", "What the current evidence supports."))
        content.addView(keyValue("Protocol", asset.protocol))
        content.addView(keyValue("Role", asset.role))
        content.addView(keyValue("Vendor", asset.vendor ?: "Not established"))
        content.addView(keyValue("Product", asset.product ?: "Not established"))
        content.addView(keyValue("Confidence", asset.confidence.toString() + "%"))
        content.addView(section("Provenance", "Keep observation and interpretation separate."))
        content.addView(card(asset.source.uppercase(), asset.evidence, ASSET_DETAIL_ID, if (asset.source.contains("active", true)) BLUE else TEAL))
        content.addView(banner("NEXT DECISION", if (asset.reviewState == "Needs review")
            "Corroborate this observation with a nameplate, approved baseline or authorized identity check."
        else "Compare this record during the next walkdown and investigate unexpected changes."))
        content.addView(button("Collect more evidence", primary = false, action = ::renderScanMenu))
    }

    fun runActiveDiscovery(caseId: String, target: String, unitId: Int) {
        page("Authorized active check", "Contacting " + target, "One constrained request · no reads or writes", ::renderAssessmentSetup)
        content.addView(card("SIGNED GRANT ACCEPTED", "Waiting for the isolated Network Broker on TCP/502…", ACTIVE_RESULT_ID, BLUE))
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                worker.execute { executeGrant(IAtlasNetworkBroker.Stub.asInterface(service), caseId, target, unitId) }
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                runOnUiThread { renderFailure("Network Broker disconnected", "No additional packet was sent.") }
            }
        }
        brokerConnection = connection
        if (!bindService(Intent("com.atlasot.netbroker.BIND").setPackage("com.atlasot.netbroker"), connection, Context.BIND_AUTO_CREATE))
            renderFailure("Network Broker unavailable", "Install the matching signed broker before active assessment.")
    }

    private fun executeGrant(broker: IAtlasNetworkBroker, caseId: String, target: String, unitId: Int) {
        runCatching {
            val professionalCase = professionalCases.assertOperationAllowed(CaseId(caseId), target, Instant.now())
            val interfaces = broker.inspectInterfaces(byteArrayOf()).toString(Charsets.UTF_8)
            val matches = Regex("""\{"handle":(\d+),"ethernet":(true|false),"wifi":(true|false)\}""").findAll(interfaces).toList()
            val selected = matches.firstOrNull { it.groupValues[2] == "true" } ?: matches.firstOrNull()
                ?: error("No connected Android network interface")
            val interfaceLabel = if (selected.groupValues[2] == "true") "USB/Ethernet"
                else if (selected.groupValues[3] == "true") "Wi-Fi" else "connected network"
            val key = grantKeyPair()
            require(broker.provisionGrantKey(key.public.encoded).toString(Charsets.UTF_8) == "PROVISIONED")
            val now = Instant.now()
            professionalCase.assertOperationAllowed(Operation.MODBUS_DEVICE_ID_BASIC, IPv4Cidr.parseAddress(target), now)
            val authorizationHash = requireNotNull(professionalCase.authorizationHash) { "professional case authorization is missing" }
            val exclusions = professionalCase.scope.excludedAddresses.mapTo(linkedSetOf(), ::addressToText)
            val allowedCidrs = professionalCase.scope.cidrs.mapTo(linkedSetOf(), ::cidrToText)
            val grant = ExecutionGrant(UUID.randomUUID().toString(), caseId, authorizationHash,
                Operation.MODBUS_DEVICE_ID_BASIC, selected.groupValues[1].toLong(), target, 502, unitId, allowedCidrs, exclusions,
                2, 512, 0, 1500, 1, now, now.plusSeconds(30), UUID.randomUUID().toString())
            val payload = ExecutionGrantWire.encode(grant)
            val pipe = ParcelFileDescriptor.createPipe()
            val accepted = pipe[1].use { broker.execute(ExecutionGrantWire.envelope(payload, GrantSignatures.sign(grant, key.private)), it).toString(Charsets.UTF_8) }
            require(accepted.startsWith("ACCEPTED:")) { accepted }
            val evidence = pipe[0].use { read -> FileInputStream(read.fileDescriptor).use { it.readBytes() } }
            require(!evidence.toString(Charsets.UTF_8).startsWith("ERROR:")) { evidence.toString(Charsets.UTF_8) }
            Triple(ActiveModbusEvidence.parse(evidence), interfaceLabel, evidence)
        }.onSuccess { runOnUiThread { renderActiveResult(caseId, target, it.second, it.first, it.third) } }
            .onFailure { runOnUiThread { renderFailure("Identification stopped safely", it.message ?: it.javaClass.simpleName) } }
    }

    private fun renderActiveResult(caseId: String, target: String, iface: String, identity: ActiveModbusIdentity, evidence: ByteArray) {
        val current = requireNotNull(site)
        page("Active evidence", if (identity.identitySupported) "Controller identified" else "Modbus service confirmed",
            caseId + " · constrained check complete", ::renderScanMenu)
        content.addView(pill(if (identity.identitySupported) "Identity confirmed" else "Service confirmed"))
        val identityText = listOfNotNull(identity.vendor, identity.product, identity.revision).joinToString(" · ")
        content.addView(card("MODBUS/TCP · " + target + ":502", "Interface  " + iface + "\n" +
            identityText.ifBlank { "Vendor and model not returned" } + "\n" + identity.evidence + "\nEvidence bytes  " + evidence.size,
            ACTIVE_RESULT_ID, BLUE))
        content.addView(banner("NEXT DECISION", "Review the evidence before adding this asset. No register read or write was performed."))
        content.addView(button("Add to asset inventory", SAVE_ASSETS_ID) {
            repository.addAssets(current.id, listOf(InventoryAsset(UUID.randomUUID().toString(), current.id, target,
                identity.product ?: identity.vendor ?: "Modbus device", "Modbus/TCP", "Controller/server",
                identity.vendor, identity.product, if (identity.identitySupported) 98 else 82, "Active identity · " + caseId,
                identity.evidence, if (identity.identitySupported) "Confirmed" else "Needs review")))
            renderInventory()
        })
    }

    private fun renderFailure(title: String, detail: String) {
        page("Safe stop", title, "The application did not expand the authorized scope", ::renderScanMenu)
        content.addView(card("ACTION REQUIRED", detail, ACTIVE_RESULT_ID, DANGER))
        content.addView(button("Return to collection methods", primary = false, action = ::renderScanMenu))
    }

    private fun siteCard(item: SiteProfile): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL; background = rounded(WHITE, 18, BORDER); elevation = dp(2).toFloat()
        setPadding(dp(18), dp(16), dp(18), dp(16)); setOnClickListener { site = item; renderWorkspace() }
        addView(LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            addView(txt(item.name, 18f, NAVY, Typeface.BOLD), LinearLayout.LayoutParams(0, -2, 1f))
            addView(txt("›", 30f, BLUE))
        })
        addView(txt(item.location, 13f, MUTED).apply { setPadding(0, dp(5), 0, dp(10)) })
        val footer = (if (item.sample) "SAMPLE  ·  " else "") + item.industry + "  ·  " + repository.assets(item.id).size + " assets"
        addView(txt(footer.uppercase(), 11f, if (item.sample) AMBER else SUCCESS, Typeface.BOLD).apply { letterSpacing = .04f })
        layoutParams = margins(0, 0, 0, 12)
    }

    private fun method(kicker: String, title: String, body: String, footer: String, viewId: Int, action: () -> Unit): View =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; id = viewId; background = rounded(WHITE, 18, BORDER); elevation = dp(2).toFloat()
            setPadding(dp(18), dp(16), dp(18), dp(16)); setOnClickListener { action() }; layoutParams = margins(0, 0, 0, 12)
            addView(txt(kicker, 11f, SLATE, Typeface.BOLD).apply { letterSpacing = .06f })
            addView(txt(title, 19f, NAVY, Typeface.BOLD).apply { setPadding(0, dp(5), 0, dp(6)) })
            addView(txt(body, 14f, MUTED)); addView(txt(footer + "   →", 12f, BLUE, Typeface.BOLD).apply { setPadding(0, dp(12), 0, 0) })
        }

    private fun assetCard(asset: InventoryAsset, action: () -> Unit): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL; background = rounded(WHITE, 16, BORDER); elevation = dp(1).toFloat()
        setPadding(dp(16), dp(14), dp(16), dp(14)); setOnClickListener { action() }; layoutParams = margins(0, 0, 0, 10)
        addView(LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            addView(txt(asset.displayName, 16f, NAVY, Typeface.BOLD), LinearLayout.LayoutParams(0, -2, 1f))
            addView(txt(if (asset.reviewState == "Needs review") "REVIEW" else "CONFIRMED", 10f,
                if (asset.reviewState == "Needs review") AMBER else SUCCESS, Typeface.BOLD))
        })
        addView(txt(asset.address + "  ·  " + asset.protocol, 13f, BLUE, Typeface.BOLD).apply { setPadding(0, dp(5), 0, dp(4)) })
        addView(txt(listOfNotNull(asset.vendor, asset.product, asset.role).joinToString(" · "), 13f, MUTED))
    }

    private fun networkInsight(assets: List<InventoryAsset>): String {
        if (assets.isEmpty()) return "Inventory is empty. Import a capture or run one authorized identity check."
        val protocols = assets.groupingBy { it.protocol }.eachCount().entries.sortedByDescending { it.value }.take(3)
            .joinToString("  ·  ") { it.key + " " + it.value }
        return protocols + "\n" + assets.mapNotNull { it.vendor }.distinct().size + " identified vendors  ·  " +
            assets.count { it.reviewState == "Needs review" } + " assets require review"
    }

    private fun metric(value: String, label: String): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; background = rounded(WHITE, 16, BORDER)
        setPadding(dp(6), dp(14), dp(6), dp(14)); addView(txt(value, 24f, NAVY, Typeface.BOLD).apply { gravity = Gravity.CENTER })
        addView(txt(label, 11f, MUTED, Typeface.BOLD).apply { gravity = Gravity.CENTER })
    }
    private fun summary(a: String, al: String, b: String, bl: String, c: String, cl: String, idValue: Int): View =
        LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; id = idValue
            addView(metric(a, al), weight(8)); addView(metric(b, bl), weight(8)); addView(metric(c, cl), weight(0))
        }
    private fun weight(end: Int) = LinearLayout.LayoutParams(0, -2, 1f).apply { marginEnd = dp(end) }

    private fun card(title: String, body: String, idValue: Int = View.NO_ID, accent: Int = SLATE): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; id = idValue; background = rounded(WHITE, 16, BORDER); elevation = dp(1).toFloat()
            setPadding(dp(16), dp(14), dp(16), dp(14)); layoutParams = margins(0, 0, 0, 10)
            addView(txt(title, 12f, accent, Typeface.BOLD).apply { letterSpacing = .05f })
            addView(txt(body, 14f, NAVY).apply { setPadding(0, dp(6), 0, 0); setLineSpacing(0f, 1.12f) })
        }

    private fun banner(title: String, body: String, idValue: Int = View.NO_ID): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL; id = idValue; background = rounded(PALE_BLUE, 12, BORDER)
        setPadding(dp(16), dp(14), dp(16), dp(14)); layoutParams = margins(0, 0, 0, 12)
        addView(txt(title, 11f, BLUE, Typeface.BOLD).apply { letterSpacing = .06f })
        addView(txt(body, 14f, NAVY).apply { setPadding(0, dp(5), 0, 0) })
    }
    private fun pill(value: String): View = txt(value.uppercase(), 11f, if (value.contains("confirmed", true)) SUCCESS else AMBER, Typeface.BOLD).apply {
        background = rounded(if (value.contains("confirmed", true)) PALE_TEAL else PALE_AMBER, 40)
        setPadding(dp(14), dp(8), dp(14), dp(8)); layoutParams = LinearLayout.LayoutParams(-2, -2)
    }
    private fun keyValue(key: String, value: String): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL; setPadding(0, dp(9), 0, dp(9))
        addView(txt(key, 13f, MUTED), LinearLayout.LayoutParams(0, -2, .42f))
        addView(txt(value, 14f, NAVY, Typeface.BOLD), LinearLayout.LayoutParams(0, -2, .58f))
    }
    private fun step(number: String, title: String): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(0, dp(12), 0, dp(8))
        addView(txt(number, 12f, WHITE, Typeface.BOLD).apply { gravity = Gravity.CENTER; background = rounded(BLUE, 40) },
            LinearLayout.LayoutParams(dp(28), dp(28)))
        addView(txt(title, 17f, NAVY, Typeface.BOLD).apply { setPadding(dp(10), 0, 0, 0) })
    }
    private fun setupProgress(active: Int): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        listOf("Site", "Technology", "Review").forEachIndexed { index, label ->
            val number = index + 1
            val reached = number <= active
            addView(LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
                addView(txt(number.toString(), 13f, if (reached) WHITE else MUTED, Typeface.BOLD).apply {
                    gravity = Gravity.CENTER
                    background = rounded(if (reached) BLUE else WHITE, 40, if (reached) BLUE else BORDER)
                }, LinearLayout.LayoutParams(dp(32), dp(32)))
                addView(txt(label, 12f, if (number == active) NAVY else MUTED,
                    if (number == active) Typeface.BOLD else Typeface.NORMAL).apply {
                    gravity = Gravity.CENTER; setPadding(0, dp(5), 0, 0)
                })
            }, LinearLayout.LayoutParams(0, -2, 1f))
        }
    }
    private fun section(title: String, subtitle: String): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL; setPadding(0, dp(20), 0, dp(10))
        addView(txt(title, 19f, NAVY, Typeface.BOLD)); addView(txt(subtitle, 13f, MUTED).apply { setPadding(0, dp(3), 0, 0) })
    }
    private fun field(label: String, hint: String, idValue: Int, value: String): EditText {
        content.addView(txt(label, 12f, MUTED, Typeface.BOLD).apply { setPadding(dp(2), dp(6), 0, dp(5)) })
        return EditText(this).apply {
            id = idValue; setText(value); this.hint = hint; textSize = 15f; setTextColor(NAVY)
            setHintTextColor(Color.rgb(148, 157, 166)); background = rounded(WHITE, 14, BORDER)
            setPadding(dp(14), dp(11), dp(14), dp(11)); setSingleLine(true); content.addView(this, margins(0, 0, 0, 8))
        }
    }
    private fun button(label: String, idValue: Int = View.NO_ID, primary: Boolean = true, action: () -> Unit): Button =
        Button(this).apply {
            text = label; id = idValue; isAllCaps = false; textSize = 15f; typeface = Typeface.DEFAULT_BOLD
            setTextColor(if (primary) WHITE else NAVY); background = rounded(if (primary) BLUE else WHITE, 11, if (primary) BLUE else BORDER)
            minHeight = dp(50); setPadding(dp(14), dp(10), dp(14), dp(10)); setOnClickListener { action() }
            layoutParams = margins(0, 12, 0, 0)
        }
    private fun txt(value: String, size: Float, color: Int, style: Int = Typeface.NORMAL) = TextView(this).apply {
        text = value; textSize = size; setTextColor(color); setTypeface(Typeface.DEFAULT, style)
    }
    private fun rounded(color: Int, radius: Int, stroke: Int? = null) = GradientDrawable().apply {
        setColor(color); cornerRadius = dp(radius).toFloat(); stroke?.let { setStroke(dp(1), it) }
    }
    private fun margins(left: Int, top: Int, right: Int, bottom: Int) = LinearLayout.LayoutParams(-1, -2).apply {
        setMargins(dp(left), dp(top), dp(right), dp(bottom))
    }
    private fun space(height: Int) = Space(this).apply { layoutParams = LinearLayout.LayoutParams(1, dp(height)) }
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    private fun grantKeyPair(): KeyPair {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getEntry(KEY_ALIAS, null) as? KeyStore.PrivateKeyEntry)?.let { return KeyPair(it.certificate.publicKey, it.privateKey) }
        return KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore").run {
            initialize(KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1")).setDigests(KeyProperties.DIGEST_SHA256).build())
            generateKeyPair()
        }
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
        const val CASE_FIELD_ID = 0x41544C4C
        const val SITE_FIELD_ID = 0x41544C4D
        const val TARGET_FIELD_ID = 0x41544C4E
        const val SCOPE_FIELD_ID = 0x41544C4F
        const val UNIT_FIELD_ID = 0x41544C50
        const val AUTHORIZATION_CHECK_ID = 0x41544C51
        const val VALIDATION_MESSAGE_ID = 0x41544C52
        const val SITE_CARD_ID = 0x41544C53
        const val NEW_SITE_ACTION_ID = 0x41544C54
        const val CREATE_SITE_ACTION_ID = 0x41544C55
        const val SITE_NAME_FIELD_ID = 0x41544C56
        const val SITE_LOCATION_FIELD_ID = 0x41544C57
        const val INDUSTRY_SPINNER_ID = 0x41544C58
        const val ACTIVE_SCAN_OPTION_ID = 0x41544C59
        const val INVENTORY_ACTION_ID = 0x41544C5A
        const val INVENTORY_SEARCH_ID = 0x41544C5B
        const val INVENTORY_FILTER_ID = 0x41544C5C
        const val INVENTORY_LIST_ID = 0x41544C5D
        const val NETWORK_INSIGHT_ID = 0x41544C5E
        const val ASSET_DETAIL_ID = 0x41544C5F
        const val LIVE_CAPTURE_OPTION_ID = 0x41544C60
        const val LIVE_CAPTURE_STATUS_ID = 0x41544C61
        const val CAPTURE_CAPABILITY_ID = 0x41544C62
        const val LIVE_CAPTURE_ACTION_ID = 0x41544C63
        const val FINDINGS_SUMMARY_ID = 0x41544C64
        const val REPORT_ACTION_ID = 0x41544C65
        const val CONTINUE_ACTION_ID = 0x41544C66
        const val OVERVIEW_NAV_ID = 0x41544C67
        const val COLLECT_NAV_ID = 0x41544C68
        const val ASSETS_NAV_ID = 0x41544C69
        const val FINDINGS_NAV_ID = 0x41544C6A
        const val REPORT_NAV_ID = 0x41544C6B
        const val CONTINUE_SETUP_ID = 0x41544C6C
        const val VENDORS_CONTINUE_ID = 0x41544C6D
        const val PROFESSIONAL_CASE_ACTION_ID = 0x41544C6E
        const val CREATE_PROFESSIONAL_CASE_ID = 0x41544C6F
        const val PROFESSIONAL_CASE_CARD_ID = 0x41544C70
        const val PROFESSIONAL_CASE_STATUS_ID = 0x41544C71
        const val OPERATIONAL_APPROVAL_ID = 0x41544C72
        const val SECURITY_APPROVAL_ID = 0x41544C73
        const val RECORD_APPROVALS_ID = 0x41544C74
        const val START_PROFESSIONAL_COLLECTION_ID = 0x41544C75
        const val PROFESSIONAL_CASE_ERROR_ID = 0x41544C76
        private const val OPEN_CAPTURE = 70
        private const val KEY_ALIAS = "atlas-grant-key-v1"
        private val INDUSTRIES = listOf("Water & wastewater", "Manufacturing", "Energy & utilities", "Mining & minerals", "Food & beverage", "Ports & logistics", "Oil & gas", "Pharmaceutical")
        private val VENDORS = listOf("Siemens", "Schneider Electric", "Rockwell Automation", "ABB", "Emerson", "Honeywell", "Yokogawa", "Endress+Hauser", "Phoenix Contact", "WAGO")
        private val NAVY = Color.rgb(11, 31, 51)
        private val MUTED = Color.rgb(71, 85, 105)
        private val SLATE = Color.rgb(51, 65, 85)
        private val TEAL = Color.rgb(22, 122, 90)
        private val SUCCESS = Color.rgb(22, 122, 90)
        private val AQUA = Color.rgb(167, 202, 255)
        private val BLUE = Color.rgb(36, 87, 214)
        private val AMBER = Color.rgb(170, 82, 16)
        private val DANGER = Color.rgb(176, 35, 45)
        private val SURFACE = Color.rgb(244, 247, 251)
        private val WHITE = Color.WHITE
        private val BORDER = Color.rgb(205, 216, 228)
        private val PALE_TEAL = Color.rgb(234, 247, 241)
        private val PALE_AMBER = Color.rgb(255, 241, 219)
        private val PALE_BLUE = Color.rgb(237, 243, 255)
    }
}
