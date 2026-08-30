# Evidence-gap and hazard register

Subjective likelihood/impact scores have been removed. Hazards remain design inputs; evidence gaps require tests.

| ID | Hazard or gap | Current evidence | Required resolution |
|---|---|---|---|
| H1 | promised traffic may not be visible | Android USB API does not change switched-Ethernet visibility | two phone/NIC/TAP capture tests |
| H2 | active identity may affect fragile OT | industrial protocols/devices vary; no lab profiles approved | golden packets, owned devices, independent safety review |
| H3 | identity may be wrong | no labeled corpus benchmark | corpus accuracy/conflict results |
| H4 | catalog maintenance may be uneconomic | many vendors/protocols appear in Morocco-linked jobs | measured maintenance and support effort |
| H5 | product may duplicate existing tools | Claroty Edge/runZero/general/open tools exist | same-corpus competitive evaluation |
| H6 | willingness to pay unknown | zero accepted product prices/pilots | real offers and procurement outcome |
| H7 | qualified auditor may reject evidence | no provider review | written schema/method review |
| H8 | capture may contain personal/sensitive data | Law 09-08 and sensitive-system rules apply | DPIA/legal review and minimization tests |
| H9 | dependency license may block model | GPL/LGPL/MPL/NPSL mix | pinned legal/component review |
| H10 | parser may be exploitable | untrusted packets; no implementation tests | memory-safe design, fuzzing and sanitizers |
| H11 | field hardware may vary | no supported matrix | multiple SKU/device-ID tests |
| H12 | product vendor role may require qualification | legal boundary not formally reviewed | Moroccan counsel/DGSSI-qualified provider interpretation |
| H13 | phone use may be prohibited at site | no site mobile/MDM evidence | real evaluation policy review |
| H14 | Bluetooth may be irrelevant | no Moroccan procurement/job demand found | remove from MVP unless evidence appears |
| H15 | support cost may exceed price | neither variable observed | timed pilot and accepted price |

A hazard is closed only with evidence, not a lower subjective score.
