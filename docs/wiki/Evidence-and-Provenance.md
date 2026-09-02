# Evidence and Provenance

Atlas is designed so a professional conclusion can be traced back to the source that supports it. The core discipline is to keep different semantic layers separate rather than collapsing them into one mutable asset row.

## The evidence chain

A useful mental model is:

**Customer declaration → raw evidence → observation → identity claim → reconciliation decision → finding → finalized assessment**

These are related, but they are not interchangeable.

### Customer declaration

What the customer currently believes or records—for example an asset inventory, drawing or handover list.

### Raw evidence

The original source retained for assessment use, such as a PCAP/PCAPNG file or other approved artifact.

### Observation

A bounded fact extracted from evidence, for example that an endpoint at a certain address participated in a Modbus exchange during the sample.

### Identity claim

An interpreted identity assertion supported by one or more observations—for example a vendor/product identifier returned by an approved protocol method.

### Reconciliation decision

A professional decision about how observed evidence relates to an expected record: confirmed, probable, conflicting, unexpected, not observed or unresolved.

### Finding

A reviewed assessment conclusion that depends on sufficient evidence and preserves confidence, limitations and professional context.

## Why provenance matters

Without provenance, a future reviewer cannot tell whether a model name came from:

- the customer's spreadsheet;
- a packet response;
- a physical label;
- a parser inference;
- an assessor judgment.

Atlas should preserve enough source information to answer that question.

## Artifact identity

Professional artifacts should retain cryptographic identity and collection/import context. The hash proves which exact bytes were referenced; it does not by itself prove that the capture point, collection process or interpretation was correct. Those contextual facts remain part of the evidence record.

## Review does not erase history

When an assessor accepts a reconciliation decision, the original customer declaration and observations should remain available. Professional review adds a decision; it should not rewrite the evidence history to make the case look cleaner.

## Limitations are part of provenance

Provenance includes how the evidence was obtained and what it could observe. A 20-minute SPAN capture and a customer-supplied PCAP with unknown capture point are not equivalent evidence sources even if they contain similar packets.

For the normative record model, use the repository's [Evidence Data Model](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/EVIDENCE-DATA-MODEL.md) and [Professional Case Model](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/PROFESSIONAL-CASE-MODEL.md).
