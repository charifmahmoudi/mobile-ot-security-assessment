# Device, vendor and protocol catalog

## How to read this page

“Vendor family → protocol” mappings are supported by vendor documentation. They do **not** prove that a Moroccan target account uses that family. Installed-base status remains unknown until supported by a plant-specific source or authorized observation.

| Device/vendor family | Likely identity surface | Protocols/transports | Safe prototype action | Evidence |
|---|---|---|---|---|
| Siemens SIMATIC S7-1200/1500 | PROFINET DCP identity, TLS/cert, OPC UA endpoints | PROFINET, S7 communications, OPC UA | passive fingerprint; DCP identify only in approved window | Siemens documentation |
| Schneider Modicon M340/M580 | MAC OUI, Modbus device identity where supported, web/TLS metadata | Modbus/TCP; EtherNet/IP modules; OPC UA options | read-only function 43/14 only on allowlist | Schneider product documentation |
| Rockwell ControlLogix/CompactLogix | CIP Identity Object | EtherNet/IP/CIP | ListIdentity/identity attributes only | Rockwell EtherNet/IP manual |
| ABB/Siemens/Schneider drives | discovery metadata, protocol object identity | PROFINET, EtherNet/IP, Modbus/TCP vary by option | passive first; vendor-specific profile required | per-SKU manual required |
| OPC UA servers/gateways | endpoint and application description, certificate | OPC UA TCP/HTTPS | GetEndpoints/FindServers; no browse until approved | OPC Foundation/vendor docs |
| BACnet controllers | I-Am fields: vendor ID, device instance | BACnet/IP | Who-Is with strict rate and scope | ASHRAE/BACnet assignment tables |
| Building/energy meters | Modbus identity/register map where implemented | Modbus RTU/TCP, BACnet | identity only; never generic register sweeps | per-model manual required |
| Utility RTU/IED | passive application identifiers | IEC 60870-5-104, IEC 61850, DNP3 depending region/vendor | passive-only in MVP | per-site engineering data required |
| Printers/cameras/IoT | DNS-SD, SSDP, SNMP system data, TLS | mDNS, SSDP, SNMP, ONVIF | multicast observation; SNMP only with authorization | standards/vendor docs |
| BLE sensors/gateways | GAP advertisement, GATT service UUIDs | BLE GAP/GATT | advertisement-only by default | Bluetooth SIG assignments |
| Wi-Fi infrastructure | beacon/probe metadata | 802.11, WPA2/3 | passive beacon inventory; no deauth | IEEE/Android APIs |

## Identification confidence

```
confidence = source reliability × fingerprint specificity × observation freshness
```

Store evidence independently. Example: OUI alone is low confidence; CIP vendor/product code plus a matching vendor EDS is high confidence. Conflicting fingerprints must lower confidence and remain visible.

## Risk classes

- **P0 passive:** receive-only observation and imported evidence.
- **A1 identity:** bounded standards-based identity request; no operational data.
- **A2 read-only:** explicit approved read of non-control metadata.
- **A3 sensitive:** authentication, broad enumeration, legacy or fragile protocol; disabled by default.
- **A4 prohibited in production:** writes, mode changes, firmware, fuzzing, exploit and denial-of-service behavior.

The prototype supports P0 and selected A1 only.
