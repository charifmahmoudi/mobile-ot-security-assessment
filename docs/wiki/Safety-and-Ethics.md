# Safety, authorization and privacy

## Operating doctrine

Only assess networks and devices with documented authorization. Operational safety outranks coverage.

Before active discovery, the case must record owner, scope, time window, approved interfaces/subnets/devices, excluded systems, emergency contact, stop conditions and evidence-retention policy.

## Hard controls

- passive mode is the default;
- active mode requires a case authorization and visible confirmation;
- no writes or control functions in the prototype;
- concurrency, rate, retries, payload size and timeout are bounded per protocol;
- an offline kill switch cancels outstanding work;
- every packet-producing action is logged with profile hash;
- safety profiles are signed and cannot be edited during an assessment;
- fragile assets and safety systems can be denylisted.

## Privacy

Packet captures may contain identifiers, credentials, communications or personal data. Apply Morocco's Law 09-08 and contractual requirements through minimization, purpose limitation, access control, short retention, export approval and deletion. Default reports should exclude payloads and secrets.

## Responsible development

Use emulators, digital twins and owned lab hardware for parser and active-query testing. Production validation needs written authorization and a rollback/communications plan. Publish a security contact and coordinated disclosure policy before binary release.

## Prohibited product behavior

Credential attacks, exploit execution, persistence, evasion, deauthentication, jamming, fuzzing, firmware modification, control writes, safety-state interaction and autonomous expansion beyond scope.
