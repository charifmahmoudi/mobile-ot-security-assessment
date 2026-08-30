# Morocco sector dossiers

## Prioritization model

Score each sector 1–5 on site concentration, OT intensity, assessment trigger, channel accessibility and ability to pay. The scores are hypotheses derived from public footprint, not customer research.

| Sector | Concentration | OT intensity | Trigger | Channel access | Ability to pay | Total /25 | Priority |
|---|---:|---:|---:|---:|---:|---:|---|
| Automotive/suppliers | 5 | 5 | 4 | 5 | 5 | 24 | A |
| Phosphate/chemicals/mining | 4 | 5 | 5 | 3 | 5 | 22 | A |
| Ports/logistics | 4 | 5 | 5 | 4 | 5 | 23 | A |
| Energy/water | 3 | 5 | 5 | 3 | 5 | 21 | A |
| Food/beverage | 4 | 4 | 3 | 4 | 3 | 18 | B |
| Cement/materials | 4 | 5 | 4 | 3 | 4 | 20 | B |
| Aerospace | 5 | 4 | 5 | 4 | 5 | 23 | A/B |
| Pharmaceuticals | 3 | 4 | 5 | 3 | 4 | 19 | B |
| Textile | 5 | 3 | 2 | 4 | 2 | 16 | C |

## Automotive and suppliers

**Verified landscape.** Renault operates Tangier and SOMACA Casablanca. Stellantis operates Kenitra and announced expansion toward 400,000 vehicles/year plus electric mobility objects. Tanger Med cites more than 150 foreign automotive operators in its ecosystem.

**Likely asset classes (sector hypothesis):** PLCs, robot controllers, safety controllers, vision, torque systems, conveyors, paint-shop controls, drives, energy meters, industrial switches, AGVs, building management and quality systems.

**Protocol coverage priority:** PROFINET/DCP, S7 passive signatures, EtherNet/IP/CIP identity, Modbus/TCP, OPC UA, SNMP, LLDP, mDNS and vendor web/TLS fingerprints. Automotive facilities can be multi-vendor; no single protocol should be assumed.

**Entry offer:** contractor/handover inventory validation or a controlled baseline of a supplier facility, not a flagship assembly line first.

**Disqualifier:** no mirror/TAP access, no maintenance owner, or expectation of uncontrolled scanning during production.

## Phosphate, chemicals and mining

**Verified landscape.** OCP documents mines and integrated chemical/fertilizer platforms at Jorf Lasfar and Safi. Managem lists Moroccan mines including Draa Sfar, Imiter, Akka, Bou-Azzer, Bleida and Tizert.

**Likely assets:** DCS/PLC, process analyzers, drives, conveyors, weigh systems, remote telemetry, power distribution, water/desalination, historians and safety systems.

**Protocol hypotheses:** Modbus RTU/TCP, OPC UA/DA gateways, PROFINET, EtherNet/IP, IEC 60870-5-104/IEC 61850 in electrical domains, HART behind gateways and vendor-specific DCS protocols. The mobile product should not directly interrogate SIS or fieldbus loops in MVP.

**Entry offer:** passive inventory reconciliation on an isolated area or maintenance network; imported PCAP analysis; contractor handover.

## Ports and logistics

**Verified landscape.** Tanger Med Group operates port, logistics and industrial platforms; its public LinkedIn description reports 187m tonnes and 11.44m TEU across the group and Marsa Maroc terminals. Treat social figures as current profile claims, not audited statistics.

**Likely assets:** crane PLCs, terminal operating interfaces, gates, OCR/cameras, access control, reefer monitoring, shore power, substations, conveyors and BMS.

**Protocol priorities:** SNMP/LLDP, Modbus/TCP, PROFINET, EtherNet/IP, OPC UA, ONVIF, BACnet/IP and standard IP services. Physical segmentation means multi-interface case mapping is valuable.

**Entry offer:** terminal-area evidence baseline tied to an expansion/handover.

## Energy and water

**Verified landscape.** ONEE is the national electricity/water operator; Masen publishes the Noor renewable project portfolio including 160 MW Noor Ouarzazate I.

**Likely assets:** SCADA, RTU, IED, protection relays, plant PLC/DCS, meters, pumps, telemetry and BMS.

**Protocol hypotheses:** IEC 60870-5-104, IEC 61850, Modbus, OPC UA and vendor-specific engineering protocols. DNP3 is lower priority than IEC protocols for Morocco until evidence changes the ranking.

**Safety position:** passive-only for substation/protection and safety-relevant assets in early releases.

## Food and beverage

The Ministry of Industry names Cosumar, Centrale Danone, COPAG, Nestlé, Mondelez, Lesieur Cristal and other significant operators. Cosumar reports sugar operations in five regions plus a Casablanca refinery.

Likely assets include batch control, temperature/cold chain, packaging, CIP cleaning, boilers, refrigeration, utilities and warehouse systems. Prioritize Modbus, PROFINET, EtherNet/IP, OPC UA, BACnet and common packaging/drive fingerprints.

The channel opportunity is stronger than large direct contracts: a standardized baseline service for many medium plants.

## Cement and materials

Holcim Maroc describes itself as Morocco's national leader in construction materials. Ciments du Maroc and CIMAT operate additional cement/grinding sites.

Likely assets: kiln and mill DCS/PLC, conveyors, drives, weigh feeders, bagging, quarry systems, analyzers and power distribution. Remote and harsh environments strengthen offline value.

## Aerospace

Safran reports more than 4,800 staff across ten Moroccan sites. US Commercial Service reported nearly 150 aerospace companies and $2.6bn of exports at end-2024.

High-value manufacturing and MRO create strong assurance triggers. However, access and supplier security requirements can lengthen sales. Start through a certified integrator or supplier, with strict data handling.

## Pharmaceuticals

Sothema documents specialized manufacturing units; Cooper Pharma identifies as a producer/distributor/exporter. Asset inventory touches validated systems, so changes and evidence handling are sensitive.

Entry offer: passive network evidence and inventory reconciliation around utilities/environmental monitoring, with quality and validation approval. Do not actively query validated production without a separately approved protocol.

## Textile

Large employment and many SMEs make this a possible later-volume segment, but lower budgets and less standardized OT may increase support cost. Serve through partners after the product has a repeatable low-touch workflow.
