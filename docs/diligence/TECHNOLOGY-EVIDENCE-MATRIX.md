# Technology and protocol evidence matrix

## Evidence layers

- **L1 plant-specific:** named facility and installed device/vendor/protocol from an operator, tender, OEM case study or authorized observation.
- **L2 Morocco ecosystem:** Moroccan jobs, training, distributor or integrator evidence for a technology, without proving a named plant.
- **L3 vendor-product:** official manufacturer documentation maps a family to a protocol.
- **L4 sector hypothesis:** process knowledge suggests relevance; must not be marketed as installed base.

## Current evidence

| Technology | Morocco signal | Product-family evidence | Plant-specific status | Product decision |
|---|---|---|---|---|
| Siemens S7/TIA | Moroccan public skills profiles and training show S7-300/400/1200/1500, TIA, WinCC, PROFINET/PROFIBUS | Siemens documents S7-1500 PROFINET and OPC UA | isolated CV/project references are weak; most target plants unknown | priority passive + DCP lab work |
| Schneider Modicon | Schneider has Morocco operations; regional integrators advertise Schneider capability | M580 module officially supports EtherNet/IP and Modbus TCP | unknown by target facility | priority Modbus identity; evaluate EIP |
| Rockwell/Allen-Bradley | Moroccan/region automation profiles mention Logix/PanelView; weaker local signal | Rockwell documents CIP/EtherNet/IP identity/communications | unknown | implement standard CIP identity after lab proof |
| OPC UA | common Industry 4.0 integration and supported by multiple PLC families | IEC 62541; Siemens and open62541 evidence | unknown | high-value safe discovery candidate |
| Modbus | Moroccan skills evidence and broad device support | open Schneider-origin specification and many OEMs | unknown | first active identity candidate, with strict limitations |
| PROFINET | Moroccan training profiles mention it; strong automotive/manufacturing relevance | Siemens/PI ecosystem | unknown | passive/DCP identity; no cyclic-control interaction |
| PROFIBUS | skills signal and legacy likelihood | PI standard/device catalogs | no mobile Ethernet visibility without gateway/accessory | catalog/import only initially |
| EtherNet/IP/CIP | ecosystem signal weaker than Siemens but credible | Rockwell official documentation | unknown | CIP ListIdentity/Identity Object only |
| BACnet/IP | building controls likely across large campuses | ASHRAE standard and vendor IDs | unknown | passive then bounded Who-Is |
| IEC 60870-5-104 | relevant to regional electric SCADA architecture | IEC standard/vendor products | unknown | passive-only priority for utility pack |
| IEC 61850 | relevant to substations | IEC standard and IED products | unknown | passive-only; MMS/GOOSE safety sensitivity |
| DNP3 | common in some global utilities | DNP Users Group/specification | no Morocco-specific evidence found | lower priority |
| BACnet MS/TP, Modbus RTU | building/field serial installed base plausible | standards and devices | phone needs USB serial and physical access | future accessory profile |
| BLE GATT | Android supports scan/connect; industrial sensors/gateways increasing | Bluetooth SIG assignments | target installed base unknown | advertisement inventory first |
| Wi-Fi 802.11 | universal enterprise/industrial infrastructure | Android Wi-Fi APIs | raw monitor mode not assured | connected-network metadata, imported captures |
| SNMP/LLDP | common managed infrastructure | IETF/IEEE standards | highly plausible, not plant-proven | passive + authorized read-only system metadata |
| ONVIF | camera systems in ports/plants | ONVIF profiles | target installed base unknown | passive/WS-Discovery metadata |

## Safe identity operations under consideration

| Protocol | Candidate request | Identity returned | Primary risk | MVP status |
|---|---|---|---|---|
| EtherNet/IP | UDP ListIdentity | vendor/device/product/revision/serial where implemented | broadcast load or fragile stack | lab candidate |
| Modbus/TCP | Encapsulated Interface Transport, Read Device Identification (43/14) | vendor/product/revision objects where implemented | unsupported function/fragile gateway | lab candidate, unicast only |
| OPC UA | FindServers/GetEndpoints | application URI/name, endpoints, certificate | session/certificate edge cases | lab candidate |
| PROFINET DCP | Identify | station name, vendor/device IDs, IP parameters | Layer-2 multicast and duplicate traffic | lab candidate |
| BACnet/IP | Who-Is/I-Am | device instance, vendor ID, capabilities | broadcast amplification | later candidate |
| mDNS/DNS-SD | passive browse or scoped query | service types and instance metadata | multicast traffic | passive preferred |
| SSDP/WS-Discovery | passive or bounded probe | service/device URIs | multicast response burst | passive preferred |
| SNMP | sysObjectID/sysDescr with provided credential | system/vendor identity | credential/privacy, agent load | later, explicit authorization |
| IEC 104/61850 | none active | passive traffic metadata | critical control/protection sensitivity | passive only |

## Catalog schema

Each device-family record needs:

- canonical vendor and aliases;
- manufacturer IDs/OUI ranges with source and dates;
- product family/models;
- device roles;
- supported protocols by SKU/firmware;
- passive fingerprints and ambiguity;
- permitted identity profiles;
- known fragile versions/advisories;
- confidence rules;
- licensing/copyright status of imported identifiers;
- source URLs and last review;
- test fixtures and expected observations.

## Coverage metrics

Do not count a protocol as “supported” because a port is recognized. Report:

1. transport detection;
2. protocol confirmation;
3. vendor identity;
4. product family;
5. model;
6. firmware/revision;
7. asset role;
8. vulnerability applicability confidence.

Publish numerator/denominator against a versioned lab corpus.
