# End-to-end acceptance contract

This file defines what a green pipeline proves. It deliberately separates software emulation from field and hardware acceptance.

## User journeys

| Journey | Entry point | Observable completion | Safety assertion |
|---|---|---|---|
| Passive import | Android `ACTION_OPEN_DOCUMENT` / `content://` URI | Capture summary and evidence-backed asset review | Case app has no Internet permission; no broker call |
| Live passive stream | Site → Collect evidence → SPAN/TAP capability → bounded sample | Capture summary and evidence-backed asset review | Capture Broker is signature-protected, has no Internet permission and exposes no send operation |
| Authorized identification | Home → work order → target/scope → authorization | `IDENTITY CONFIRMED` or `SERVICE CONFIRMED` result | One FC 43/MEI 14 request; no reads, writes, sweep or fallback |
| Invalid scope | Target outside CIDR | Inline correction before result screen | Broker is never contacted |
| Unsupported identity | Authorized request to a valid Modbus service | Service confirmed, vendor/model blank | Protocol evidence is not promoted to identity evidence |
| Malformed capture | Corrupt/truncated upload | Actionable failure screen | No partial assets are saved |

## CI evidence matrix

| Gate | Environment | Required result |
|---|---|---|
| Research PCAP corpus | JVM | Modbus, DNP3, IEC-104 and BACnet detected from hash-pinned upstream files |
| PCAPNG normalization | JVM | Same Modbus attribution; digest covers original PCAPNG; truncation rejected |
| Upload journey | Android API 29 and 35 | Real content URI reaches summary and asset review for all four captures |
| Live capture journey | Android API 29 and 35 | Labeled SPAN/TAP stream crosses Binder/FD boundary and reaches the same parser/inventory UI |
| Native capture backend | Linux virtual SPAN/veth | AF_PACKET daemon captures injected Ethernet; static linkage and runtime tracing show no packet-transmission syscall |
| Active identity | Android API 35 + PyModbus 3.11.3 | Vendor, product and revision returned through the signed broker path |
| Independent slave | Android API 35 + modbus-tk 1.1.5 | Modbus service confirmed without fabricated identity |
| ICS honeypot | Android API 35 + pinned Conpot commit | Modbus service confirmed without broadening the probe |
| Privilege boundary | Static + Android | Case app has no Internet permission; broker service is signature-protected |
| Guided assessor journey | Android API 29 and 35 | Site → Overview → Collect → Assets → Findings → blocked Report completes with stable navigation contracts |

The current executable reference is [run #32](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/runs/33337993093) at [`6b14a50`](https://github.com/charifmahmoudi/mobile-ot-security-assessment/commit/6b14a50ea3978b7ff69e5d60e03ec886e9602900).

## Not proven by this pipeline

- behavior of physical PLC/RTU models or vendor firmware variants;
- USB Ethernet selection on supported phones;
- receive-only SPAN/TAP capture hardware or custom Android system-image integration;
- completeness against a multi-gigabyte production capture;
- encrypted case persistence, findings, report signing, or a complete professional P0-WATER report.

Those remain release gates. Emulator compatibility is not a hardware compatibility claim.
