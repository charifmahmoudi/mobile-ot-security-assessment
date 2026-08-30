# Atlas OT Scout PoC demonstration script

Use this 7–9 minute story. Do not present planned features as implemented.

The story follows the persistent product stages: **Overview → Collect → Assets → Findings → Report**.

## Setup

- Install the matching Case App and Network Broker debug APKs from the successful CI artifacts.
- Keep the supplied Modbus PCAP on the emulator/device.
- For the live active segment, start the approved PyModbus emulator and use its exact address/CIDR.
- Reset to the **Choose a site** screen before the audience arrives.

## 1. Establish the problem — 45 seconds

Open **North Water Treatment Plant**.

Say: “OT discovery is only useful when evidence belongs to a known site and process area. We start with context, not a subnet sweep.”

Point out the sample label, industry, asset count and **Create a new site** option. Briefly show onboarding: industry dropdown and multi-vendor selection.

## 2. Resume and follow the recommendation — 60 seconds

On **Overview**, explain the 4 assets, 3 protocols and 1 review item. Point to **Assessment progress** and **Recommended next action**.

Say: “This is the working model. The app tells the assessor what is known and what still needs a decision.”

Open inventory, select **Needs review**, and open the Operator HMI. Show role, confidence, provenance and next decision. Return to the dashboard.

## 3. Choose evidence, not ‘scan everything’ — 45 seconds

Tap **Collect evidence**.

Say: “The assessor chooses a method based on visibility and authorization. Passive import cannot transmit. Active identity is deliberately locked to one known target.”

Point out that Wi-Fi and Bluetooth are marked planned.

## 4. Passive discovery — 90 seconds

Choose **Analyze PCAP / PCAPNG** and import the supplied Modbus capture.

Show the hash, 46 OT packets, four endpoints, controller/server versus client/HMI roles and confidence.

Say: “These are observations from a bounded visibility window, not claims about the complete network. Nothing enters inventory until the analyst accepts it.”

Add the reviewed observations and return to inventory to show how evidence changes the model.

## 5. Active identity — 90 seconds

Open **Identify one known controller**. Enter the approved PyModbus target and CIDR. Read the visible one-request limit aloud. Confirm authorization and run once.

On **Controller identified**, show interface, address, vendor, product, revision and evidence byte count.

Say: “The Case App cannot open arbitrary sockets. A signed, one-use grant asks the isolated broker for one FC 43 / MEI 14 identity request. No register read or write is available.”

Add the result to inventory.

## 6. Reason about the network — 60 seconds

Return to **Assets**, search for the new address or vendor, then open **Zone map**.

Say: “The output is not a celebratory device count. It is a navigable evidence model: what was observed, how, with what confidence, and where it sits in the operating process.”

## 7. Show the professional handoff — 60 seconds

Open **Findings**. Explain that confidence is separate from consequence and that protocol presence does not automatically become a vulnerability claim.

Open **Report**. Show the readiness checks and the explicit authorization/reviewer blockers.

Say: “The product guides the assessor all the way to a defensible handoff, but it refuses to issue a professional report until the required evidence and approvals exist.”

## Questions to answer precisely

| Question | Answer |
|---|---|
| Does the phone sniff the whole switched network? | No. Import PCAP/PCAPNG from an approved SPAN/TAP capture path. |
| Does active mode scan a subnet? | No. The PoC performs one identity request to one allowlisted Modbus target. |
| Is this a finished professional audit product? | No. It is an emulator-tested discovery and inventory PoC; encrypted cases, findings and signed reports remain. |
| Are inferred roles facts? | They are evidence-scoped candidates with confidence until corroborated. |
| Can the demo use real equipment? | Only under written scope and the same bounded safety rules; emulator results are the repeatable baseline. |
