# Component contracts

_Status: component responsibility and coupling rules. Current executable behavior is reported in [IMPLEMENTATION.md](../../IMPLEMENTATION.md)._

This document owns **which code boundary is responsible for what and which coupling is forbidden**. Packet semantics belong in [NETWORK-EXECUTION.md](NETWORK-EXECUTION.md); deployment/privilege topology belongs in [SYSTEM-AND-DEPLOYMENT.md](SYSTEM-AND-DEPLOYMENT.md); evidence entities belong in [EVIDENCE-DATA-MODEL.md](EVIDENCE-DATA-MODEL.md).

## Repository component map

| Path/component | Responsibility | Must not become |
|---|---|---|
| `core-domain/` | Pure grant/scope/domain policy and deterministic business rules | Android/network/storage adapter layer |
| `case-app/` | Site/workflow UI, evidence import/review, working inventory and orchestration | Generic raw-network client or root shell |
| `network-broker/` | Verify active grant and execute compiled active operation through selected Android `Network` | Generic scanner, arbitrary payload/socket service, customer case store |
| `capture-broker/` | Expose bounded passive interface/start/stop Binder boundary and FD stream | Active network client, shell command surface, arbitrary file service |
| `appliance/capture-daemon/` | Native raw Ethernet receive backend for dedicated appliance integration | General network utility or packet transmitter |
| isolated parser service | Decode untrusted evidence into bounded observations | Network/database/key owner |
| documentation/test tools | Verify architecture, docs, daemon and workflows | Runtime product authority |

Dependencies should point toward narrow domain/contracts. UI and adapters may depend on domain policy; domain policy must not depend on Android UI, broker service classes or capture implementation.

## Case App → Network Broker contract

The current AIDL surface is intentionally small:

```aidl
interface IAtlasNetworkBroker {
    byte[] inspectInterfaces(in byte[] signedRequest);
    byte[] provisionGrantKey(in byte[] x509GrantPublicKey);
    byte[] execute(in byte[] grantEnvelope, in ParcelFileDescriptor evidenceSink);
    byte[] emergencyStop(in byte[] signedStop);
}
```

Contract rules:

- no method accepts arbitrary packet bytes, command strings, port ranges, URLs or generic sockets;
- `execute` receives one signed grant envelope and a caller-provided evidence pipe;
- the broker owns grant verification and policy enforcement before socket creation;
- the broker returns evidence/result bytes, not direct database mutations;
- active operation semantics are defined only in [NETWORK-EXECUTION.md](NETWORK-EXECUTION.md).

## Case App → Capture Broker contract

```aidl
interface IAtlasCaptureBroker {
    byte[] inspectInterfaces();
    byte[] startPassiveCapture(String interfaceId, long maxBytes, long durationMs,
                               in ParcelFileDescriptor sink);
    void stopCapture();
}
```

Contract rules:

- `interfaceId` must resolve to a compiled/allowlisted passive interface;
- byte/time limits are validated by the broker/backend;
- output is returned by file descriptor;
- no arbitrary output path, BPF string, shell command or packet-send operation is exposed;
- production integration of the native daemon must preserve the same application-facing contract.

## Domain grant policy

`core-domain` owns deterministic validation of the signed active grant data: lifetime, replay nonce, operation enum, exact target/port/unit, CIDR scope/exclusion and resource ceilings. Cryptographic/network execution mechanics are outside the domain layer and are specified by [NETWORK-EXECUTION.md](NETWORK-EXECUTION.md).

Domain tests must be runnable without Android networking.

## Parser boundary

Untrusted capture bytes cross into an isolated parser through file-descriptor/typed-result boundaries. The parser must not:

- receive Android network authority;
- open the customer case database directly;
- change accepted inventory/findings;
- convert malformed evidence into partial accepted state.

Parser output is an observation input to the evidence/review model; it is not an accepted asset or finding.

## Evidence mutation boundary

The Case App/application layer is responsible for explicit review commands that change the working semantic model. Key rule:

```text
raw artifact -> parsed observation -> proposed claim/match -> analyst decision -> accepted model
```

No broker or parser directly creates an accepted asset/finding. Exact evidence lineage is defined in [EVIDENCE-DATA-MODEL.md](EVIDENCE-DATA-MODEL.md).

## Background/restart rule

Network activity never auto-resumes solely because a process restarts. A resumed workflow re-establishes the case/authorization/interface conditions required by the product contract.

Offline parsing/rendering jobs may be retryable when they operate from sealed immutable inputs and deterministic state.

## Error/logging rule

Cross-boundary failures must return bounded typed/status information suitable for safe user action. Logs and broker errors must avoid unnecessary customer payloads, credentials or full sensitive paths.

## Determinism rule

Any final assessment result must be reproducible from the finalized evidence/model inputs and identified product/content versions. Report determinism and finalization belong in [EVIDENCE-DATA-MODEL.md](EVIDENCE-DATA-MODEL.md), not duplicated here.
