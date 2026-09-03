# Atlas OT Scout website

The GitHub Pages site is the public product surface for Atlas OT Scout.

## Audience and language

French is the default language. English is available under `/en/`.

The primary reader is an industrial-assessment, audit-methodology or OT-security practitioner evaluating whether Atlas's evidence, reconciliation and review model can fit an existing professional method. Supporting journeys cover safety/control and a bounded methodology evaluation.

## Calls to action

Primary commercial calls to action open the private Atlas methodology-review calendar:

- `https://calendar.app.google/jYG6MXGrGHncnjAUA`

GitHub remains the route for technical inspection, independent builds and open-source participation. The public issue templates remain available for people who intentionally want to propose an open evaluation through GitHub, but the website does not ask prospects to publish organization, site or authorization context in an issue.

## Claim boundary

The site distinguishes illustrative methodology examples from customer evidence and links technical claims to the repository's current executable baseline. `IMPLEMENTATION.md` remains authoritative for implemented capability and limitations.

The methodology-evaluation page describes the optional 30-day device evaluation documented in `docs/business-development/OFFER.md`. It identifies signed multi-format final export, complete on-phone passive-daemon integration and physical qualification as current release work rather than executable production capability.

## Visual identity

The Android application owns the Atlas palette. `tools/brand_palette.py` reads the application RGB tokens and the website workflow fails if `src/styles/global.css` drifts from them.

Product proof uses CI-recorded Atlas emulator screens. The water-treatment image in `public/media/water-treatment-context.jpg` is illustrative context, not customer evidence.

## Deployment

`.github/workflows/website.yml` validates the palette, extracts the current application demo frames and builds the static Astro site on pull requests and changes to `main`. Pull requests build without deployment. Changes merged to `main` deploy to GitHub Pages.
