# Open-source integration and guided UX implementation

## Product rule

Atlas owns the assessment workflow, safety policy, evidence model, inventory, findings and report semantics. Open-source components may implement bounded technical functions; they do not define the product or the assessor's conclusions.

## Integration decisions

| Capability | Project | Runtime decision | Boundary |
|---|---|---|---|
| Rooted Ethernet capture | libpcap | Integrate into the system capture daemon | Daemon is SELinux-confined; Case App never receives root |
| Android capture reference | PCAPdroid / pcapd | Study capture lifecycle and qualify reuse only after license review | Do not fork the complete app or expose generic controls |
| Flow classification | nDPI | Evaluate as an optional, separately linked native worker | LGPL and dual-licensed dissectors require explicit review |
| OT parser oracle | Zeek + CISA ICSNPP | CI only | Compare normalized observations; do not deploy Zeek on the phone |
| Encrypted records | SQLCipher for Android | Planned replacement for SharedPreferences case storage | Key is wrapped by Android Keystore; capture services cannot read the database |
| Modbus RTU transport | usb-serial-for-android | Planned restricted serial broker | Adapter, unit IDs, function codes, timing and byte limits are allowlisted |
| Report rendering | Typst | Planned isolated offline renderer | Renderer receives a finalized read-only report model |
| Offline vulnerability context | CISA KEV plus vendor advisories | Planned signed data pack | Matching creates a review candidate, never an automatic finding |

## Runtime structure

1. The unprivileged Case App creates and reviews the assessment.
2. Signature-protected brokers accept narrow, typed requests.
3. The rooted capture daemon opens only a qualified passive interface.
4. Isolated parser workers convert bounded frames into observations.
5. Observations remain separate from confirmed assets until assessor acceptance.
6. Findings reference assets, observations and immutable evidence artifacts.
7. Final reports are blocked until authorization, review and evidence-quality gates pass.

## Guided workflow

The UI implements five persistent destinations inside a selected site:

1. **Overview** — context, progress, coverage and one recommended next action.
2. **Collect** — live passive, capture import, constrained active identity and future serial collection.
3. **Assets** — explicit observation review, inventory search/filtering and a process-zone network model.
4. **Findings** — evidence-linked draft conditions with confidence separate from consequence.
5. **Report** — readiness gates and a clearly marked draft preview.

The first-run path remains outside this shell: choose an existing site or create one with industry, expected vendors, report language and local retention.

## Implemented interaction rules

- Offline/passive state is persistent in the assessment shell.
- Passive is labeled as the recommended default.
- Active collection declares exactly what will be transmitted.
- Parser observations cannot be promoted without explicit selection.
- The network model starts with functional zones instead of an IP-node hairball.
- Findings do not assign exploitability or business impact from protocol presence alone.
- Report export is blocked while authorization, reviewer and encrypted-storage controls are incomplete.
- Planned capabilities remain visually distinct from working functions.

## Visual system

| Token | Value | Use |
|---|---|---|
| Navy | `#102A43` | Primary text and trust boundary |
| Action blue | `#155EEF` | Primary actions and selected navigation |
| Trusted teal | `#087E8B` | Passive/verified status |
| Background | `#F5F7FA` | Field-friendly light background |
| Surface | `#FFFFFF` | Cards and controls |
| Border | `#D8E1E8` | Structure without heavy shadows |
| Review amber | `#B54708` | Analyst decision required |
| Blocked red | `#B42318` | Invalid authorization or safe stop only |

Color never carries status by itself. Status text, icons and evidence descriptions remain mandatory. Dynamic wallpaper colors are not used on the dedicated appliance.

## Next engineering gates

- Migrate presentation from programmatic Android Views to Compose without changing broker IPC contracts.
- Replace SharedPreferences records with SQLCipher and a versioned migration.
- Persist accept, merge, reject and walkdown decisions as audit events.
- Add signed parser/advisory data packs and pack provenance.
- Generate deterministic PDF/JSON/CSV packages with reviewer signatures.
- Qualify the rooted Samsung/LineageOS/USB-NIC tuple on physical hardware.

