# Product and business risk register

Likelihood and impact: 1 low to 5 critical. Score = likelihood × impact.

| ID | Risk | L | I | Score | Owner | Leading indicator | Mitigation |
|---|---|---:|---:|---:|---|---|---|
| R1 | Android cannot capture promised traffic | 4 | 5 | 20 | platform | unsupported NIC/TAP or missing packets | hardware spike and certified matrix |
| R2 | Active query affects fragile OT | 3 | 5 | 15 | safety | timeout, unexpected response/state | passive default, packet budgets, independent review |
| R3 | Device identity false positives | 4 | 4 | 16 | knowledge | corrections/conflicts | evidence scoring, corpus, conservative UI |
| R4 | Knowledge-pack maintenance overwhelms team | 4 | 4 | 16 | product | stale mappings and support tickets | prioritize families, partner packs, citations |
| R5 | Buyers demand permanent monitoring | 3 | 4 | 12 | commercial | baseline proposals rejected | position as feeder/field workflow |
| R6 | Incumbent duplicates mobile workflow | 3 | 4 | 12 | strategy | new portable/mobile launch | local channel, offline evidence, kit |
| R7 | No willingness to pay | 4 | 5 | 20 | founder | praise but no scoped PO | paid service before full build |
| R8 | Sales cycle too long | 4 | 4 | 16 | commercial | >120 days without pilot | integrator channel and SME supplier wedge |
| R9 | Personal/capture data violation | 3 | 5 | 15 | privacy | excessive payload/retention | minimization, encryption, DPIA, access logs |
| R10 | OSS license conflict | 3 | 4 | 12 | engineering/legal | GPL linkage or missing notices | exact-component review and isolation |
| R11 | Parser vulnerability | 4 | 5 | 20 | security | crashes/malformed corpus failures | memory-safe code where possible, fuzzing |
| R12 | Field kit supply variance | 3 | 3 | 9 | operations | adapter revision changes | multiple approved SKUs and device IDs |
| R13 | Partner produces unsafe service | 3 | 5 | 15 | channel | scope deviations | training, signed policy, audit and certification |
| R14 | Support economics fail | 4 | 4 | 16 | operations | >6 h/account/year | narrow support matrix and paid tiers |
| R15 | “AI pentest” messaging creates distrust | 3 | 4 | 12 | marketing | safety objections | evidence-led StoryBrand; AI never packet authority |

Review monthly during design and before each gate.
