# Morocco technology evidence matrix

## Evidence rules

- **P1 plant/account evidence:** official procurement, employer job description or OEM case tied to a named Moroccan operator/site.
- **P2 Morocco employer evidence:** named Moroccan industrial employer and location, but not necessarily a specific production asset.
- **E1 ecosystem evidence:** Moroccan integrator, training or labor-market signal.
- **V1 vendor evidence:** official manual maps a product family to protocol.
- **Unknown:** no admissible source.

A job description proves required competence at its date. It does not enumerate the whole installed base.

## Account-linked evidence

| Organization/site | Evidence | Technologies explicitly named | Grade | Product implication |
|---|---|---|---|---|
| Renault Group Morocco | public Automation Engineer job | PLC, HMI, SCADA, Modbus, PROFIBUS, EtherNet/IP, PID | P2 | priority passive/identity coverage for these networks |
| Stellantis Kenitra | official/public automation-technician recruitment | industrial automation maintenance/projects; no protocol named in accessible text | P2 | confirms automation role, not vendor |
| ONEE Mohammedia 3×100 MW | procurement GN2107895 | maintenance of automata at fuel-loading station | P1 | confirms PLC maintenance target; protocol unknown |
| ANP Essaouira port | tender 46/AOO/DPSR/ANP/2024 | drinking-water network telemanagement | P1 | water/telemanagement pack; protocol unknown |
| ANP Jorf Lasfar port | public tender record | exterior-lighting telemanagement | P1 | building/utility OT evidence; protocol unknown |
| ANP Agadir port | official 2026–2028 procurement program | automatic pumping pretreatment, surveillance modernization, electrical systems, future lighting/water telemanagement | P1 | multiple OT/IoT asset classes; protocol unknown |
| ANP Nador | official 2026–2028 procurement program | VTS, surveillance, access control, electrical systems, network/technical rooms, water telemanagement | P1 | multi-system port assessment evidence |
| Suprajit Morocco | public Automation Engineer job | Siemens Step7/TIA/LOGO, Schneider, Omron, Rockwell RSLogix; PROFINET, PROFIBUS, Modbus, EtherNet/IP | P2 | strongest multi-vendor catalog evidence |
| Givaudan Morocco | public Automation Technician job | Siemens S7/TIA, Schneider EcoStruxure/Unity, Rockwell; PROFIBUS, PROFINET, Modbus, EtherNet/IP, SCADA | P2 | food/process multi-vendor coverage evidence |
| OPmobility Morocco | public Site Manufacturing Professional job | Apriso MES, Grafana, Node-RED, Schneider EMS, Siemens TIA Portal/Step7, IoT/API | P2 | OT/IT correlation and Siemens/Schneider evidence |
| Jibal Morocco | public automation-technician job | Modbus, PROFIBUS, EtherNet/IP, sensors, instruments, drives, CMMS documentation | P2 | food/process protocol and evidence-workflow relevance |
| Adept Technology Morocco | public automation/robotics job | TIA Portal, WinCC, InTouch, ABB/KUKA/FANUC robots, PROFIBUS, PROFINET, EtherNet/IP | P2 | robotics/vendor identification priority |
| APM Terminals Tangier | public Automation Expert listing | AS-i and PROFIBUS visible in accessible listing | P2 | port automation protocol evidence; verify full posting |
| Elum Energy Morocco-linked roles | public SCADA project/support jobs | Modbus, IEC 104, OPC UA, IEC 61850, OCPP; PV/BESS SCADA | P2/E1 | renewable pack evidence; assignments may be international |
| Riaya Industries Morocco | public automation-technician job | Siemens S7/TIA, Rockwell, Schneider; PROFINET, PROFIBUS, EtherNet/IP, Sercos | P2 | further multi-vendor ecosystem confirmation |

## What can now be stated

Public evidence shows that Moroccan industrial employers and operators work with:

- PLC and SCADA systems;
- Siemens, Schneider, Rockwell and Omron skills;
- Modbus, PROFIBUS, PROFINET and EtherNet/IP;
- automation in automotive, ports, utilities, food/process and renewable-energy contexts;
- telemanagement, surveillance, VTS, access-control, electrical and pumping systems.

## What cannot be stated

- market share of any vendor or protocol;
- exact PLC model at Renault, Stellantis, OCP or most named sites;
- that a job requirement maps to every line or site;
- that IEC 61850/104 is installed at a specific Moroccan power facility from the Elum role;
- that Bluetooth is widely deployed in Moroccan OT;
- that an identity query is safe for a specific device without model/firmware testing.

## Evidence-driven protocol order

1. Modbus passive identification and carefully bounded device-identification lab profile.
2. EtherNet/IP/CIP passive and ListIdentity lab profile.
3. PROFINET passive/DCP lab profile; PROFIBUS through imported/gateway evidence.
4. Siemens S7 passive family identification without control interaction.
5. Common SCADA/OPC UA discovery in lab.
6. SNMP/LLDP and standard network infrastructure evidence.
7. IEC 104/61850 passive parsing for utility/renewable pack.
8. Vendor robot/drive/HMI fingerprints only when sources and fixtures exist.
9. BACnet/ONVIF/BLE remain evidence gaps for Morocco until tenders/jobs/site evidence is added.

## Sources

See [behavioral evidence ledger](data/behavioral-evidence.csv). Every catalog record must cite the exact source and observation date.
