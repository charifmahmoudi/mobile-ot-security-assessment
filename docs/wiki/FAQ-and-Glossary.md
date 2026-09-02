# FAQ and Glossary

## Frequently asked questions

### Is Atlas a vulnerability scanner?

No. The P0 product is a bounded OT evidence and reconciliation assessment. It does not rely on broad scanning, credential testing or exploitation to produce its core value.

### Does `not observed` mean an asset is absent?

No. It means the expected record was not supported by the available evidence under the stated method and visibility. A bounded capture cannot usually prove physical absence.

### Can Atlas actively scan a subnet?

Not in the P0 contract. The initial active capability is one exact Modbus Device Identification operation to one explicitly authorized target. Exact behavior is defined in the repository's [Network Execution contract](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/NETWORK-EXECUTION.md).

### Does Atlas read or write Modbus process registers?

No for P0. The initial active operation is identity-only and does not include register reads/writes.

### Does passive capture see the whole network?

Only if the physical capture design delivers that visibility. Atlas cannot infer whole-segment visibility from an ordinary access port. A qualified SPAN/TAP arrangement is required for supported live passive use.

### Is a probable asset automatically accepted?

No. Probable means the evidence suggests a match but does not justify confirmation. The assessor/reviewer makes an explicit professional decision.

### Does protocol presence automatically create a vulnerability finding?

No. Service or protocol presence is evidence, not a vulnerability conclusion. Findings require the evidence conditions defined by the assessment method.

### Can the Case App access the Internet?

The architecture intentionally separates professional case work from privileged network behavior. Current permissions and executable behavior are authoritative only in [`IMPLEMENTATION.md`](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/IMPLEMENTATION.md) and the architecture contracts.

### Does emulator CI prove the field hardware is supported?

No. Emulator CI proves software and network-path behavior in the tested virtual environment. Phone, hub, USB NIC, driver, power and SPAN/TAP support require physical qualification.

### What is the first customer value proposition?

Use Atlas to compare an existing OT asset baseline with bounded evidence, explain discrepancies and uncertainty, safely investigate selected identity gaps, and return a reviewed assessment package.

## Glossary

**Artifact** — retained source evidence, such as an imported or collected capture, with identity/hash and context.

**Assessment case** — one authorized professional engagement bounded to a customer/site/process context, objective, scope, methods and review/finalization lifecycle.

**Claim** — an interpreted assertion supported by evidence, such as a vendor/model identity claim.

**Confirmed** — reconciliation status indicating that evidence and professional review support the accepted match strongly enough for the assessment.

**Conflict** — material disagreement between expected and observed identity/context that remains visible until resolved or accepted as unresolved.

**Evidence provenance** — information required to understand where evidence came from, how it was collected/interpreted and which professional decisions depend on it.

**Expected inventory / baseline** — customer-declared asset state used as a comparison source. It is not automatically treated as discovered fact.

**Finding** — reviewed professional assessment conclusion linked to sufficient evidence and limitations.

**Golden Customer Assessment** — deterministic CI fixture used to exercise the complete pilot workflow. Its values are regression expectations, not a customer benchmark.

**Identity gap** — missing or conflicting evidence that prevents a defensible asset identity conclusion.

**Not observed** — expected record without adequate supporting observation in the available evidence. It does not mean absent.

**Observation** — bounded fact derived from an evidence source before professional acceptance into the reconciled baseline.

**Probable** — candidate match supported by meaningful evidence but still missing enough certainty for confirmation.

**Reconciliation** — explicit process of comparing customer-declared expected state with observations/claims and recording professional decisions without erasing source history.

**SPAN/TAP** — network mechanism used to provide passive visibility. A correct physical configuration is necessary for meaningful capture evidence.

**Unexpected** — observed OT endpoint or asset candidate with no accepted match in the expected baseline.

**Unresolved** — evidence is insufficient or contradictory enough that the professional case intentionally preserves uncertainty.

For exact definitions and normative semantics, use the repository [Assessment Method](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/poc/ASSESSMENT-METHOD.md) and [Evidence Data Model](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/EVIDENCE-DATA-MODEL.md).
