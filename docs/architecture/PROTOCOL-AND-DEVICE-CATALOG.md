# Device, vendor and protocol research catalog

This page records **candidate identity surfaces and protocol/device research context**. It does not authorize network actions and it does not report current implementation status.

- Current executable protocol coverage: [IMPLEMENTATION.md](../../IMPLEMENTATION.md)
- P0 permitted assessment behavior: [P0-WATER product contract](../poc/WATER-WASTEWATER-POC.md)
- Exact packet-producing contract: [NETWORK-EXECUTION.md](NETWORK-EXECUTION.md)

A vendor manual can establish that a product family supports a protocol or identity surface. It does **not** prove that a target account owns that family, that a specific firmware behaves safely, or that Atlas is allowed to query it.

## Research catalog

| Device/vendor family | Potential identity/evidence surface | Protocols/transports | Research disposition |
|---|---|---|---|
| Siemens SIMATIC S7-1200/1500 | PROFINET DCP fields, S7 metadata, certificate/OPC UA endpoint context where configured | PROFINET, S7 communications, OPC UA | Passive evidence may be cataloged; any active DCP/S7/OPC UA operation requires a separate approved network contract |
| Schneider Modicon M340/M580 | Modbus device identification where supported, MAC/vendor metadata, protocol/service context | Modbus/TCP, EtherNet/IP options, OPC UA options | Modbus basic device identification is the initial P0 active identity surface; all other actions remain separate research candidates |
| Rockwell ControlLogix/CompactLogix | CIP Identity Object and passive EtherNet/IP framing | EtherNet/IP/CIP | Passive/research catalog only until a separately governed active profile is admitted |
| ABB/Siemens/Schneider drives | Passive protocol/device metadata varies by option and SKU | PROFINET, EtherNet/IP, Modbus/TCP and vendor-specific options | Per-model evidence and firmware safety review required before any active profile |
| OPC UA servers/gateways | Endpoint/application descriptions and certificates | OPC UA TCP/HTTPS | Passive metadata/research candidate; active discovery requires separate approval and release evidence |
| BACnet controllers | I-Am/vendor/device-instance evidence | BACnet/IP | Passive evidence/research candidate; active Who-Is is outside initial P0 active scope |
| Building/energy meters | Device identity, protocol/service metadata, model-specific records | Modbus RTU/TCP, BACnet and vendor-specific options | Per-model evidence; no generic register enumeration is implied |
| Utility RTU/IED | Passive protocol/application identifiers | IEC 60870-5-104, IEC 61850, DNP3 | Passive/research candidate; active operations outside initial P0 scope |
| Cameras/printers/IoT | DNS-SD, SSDP, SNMP/TLS and vendor metadata | mDNS, SSDP, SNMP, ONVIF and IP services | Contextual evidence only unless a later product pack explicitly governs the action |
| BLE sensors/gateways | GAP advertisements and service/manufacturer identifiers | BLE GAP/GATT | Advertisement evidence is a candidate H4 source; connection/GATT interaction is outside P0 |
| Wi-Fi infrastructure | Android-visible SSID/BSSID/security/channel/RSSI context | 802.11/WPA2/WPA3 | High-level observation only under the P0 H4 boundary; no monitor-mode/deauthentication claim |

## Evidence interpretation

Identity confidence is governed by the [assessment method](../poc/ASSESSMENT-METHOD.md), not by a protocol name alone. In particular:

- OUI, hostname, open port or advertised name is weak/candidate evidence;
- protocol-specific identifiers can be stronger when the source and device context are reliable;
- material conflicts remain visible rather than being overwritten by the newest observation;
- a vendor/protocol mapping from documentation is ecosystem evidence, not site-installed-base evidence.

## Admission rule for active behavior

A candidate identity surface becomes a P0 packet-producing action only after all of the following are true:

1. the product contract explicitly admits it;
2. [NETWORK-EXECUTION.md](NETWORK-EXECUTION.md) defines the exact compiled operation and ceilings;
3. the threat model has been updated where necessary;
4. golden/negative/cancellation packet tests exist;
5. implementation and independent release evidence satisfy the applicable gate.

Until then, it remains research context even if the protocol standard defines a nominally read-only discovery service.
