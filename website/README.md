# Atlas OT Scout website

The GitHub Pages site is the public product surface for Atlas OT Scout.

## Language

French is the default language. English is available under `/en/`. The three maintained reader journeys are:

- home / product value;
- safety and control boundaries;
- customer evaluation.

## Evaluation request

Primary calls to action open a GitHub issue using the repository templates:

- `.github/ISSUE_TEMPLATE/atlas-evaluation-fr.md`;
- `.github/ISSUE_TEMPLATE/atlas-evaluation-en.md`.

The templates prefill the issue title and the qualification structure for site, question, scope, starting inventory, available evidence, authorization constraints and expected outcome.

## Visual identity

The Android application owns the Atlas palette. `tools/brand_palette.py` reads the application RGB tokens and the website workflow fails if `src/styles/global.css` drifts from them.

Product proof uses CI-recorded Atlas emulator screens. The water-treatment image in `public/media/water-treatment-context.jpg` is illustrative context, not customer evidence.

## Deployment

`.github/workflows/website.yml` validates the palette, extracts the current application demo frames, builds the static Astro site and deploys it to GitHub Pages from `main`.
