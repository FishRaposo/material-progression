# Asset provenance inventory

`docs/asset-provenance.json` is the machine-readable inventory for the current
art release. It separates Material Progression's shipped asset groups from
local-only study inputs, pins every local Git study checkout, and records the
status that applies to its assets. The JSON is checked by
`tests/test_asset_provenance.py`.

## Shipped Material Progression art

The four inventory groups in the manifest are original, local Material
Progression work: Rocks and cobbles, materials and workstations, tools, and
full blocks. Their deterministic source is `tools/generate_item_art.py`; the
shipped PNGs and models are under `src/main/resources/assets/material_progression/`.
Ground Stick and Loose Rock world assets are separately preserved local
resources, not external study assets and not input to the generator.

## Local-only study inputs

The vanilla 26.2 client archive and the No Tree Punching, TerraFirmaCraft, and
Divergent Underground checkouts are readability references only. They are not
dependencies, generated-resource inputs, documentation images, or release
artifacts. `research/reference-assets/` is ignored; its contents must never be
staged. The manifest's `license_status` is a review record, not a grant of
redistribution rights.

To refresh a Git study checkout locally, clone the manifest's `source_url` to
its `local_path`, then check out its pinned `revision`. Do not use a moving
branch name. To refresh the vanilla study input, resolve the Minecraft 26.2
client archive through the normal local Gradle/NeoForge runtime and inspect it
in place as described in [Item Art Direction and Reference Baseline](ITEM_ART.md).
Neither workflow copies images or source into this repository.

Before a release, run:

```powershell
python -m unittest tests.test_asset_provenance -v
python -m unittest tests.test_item_art -v
```

If a study source or its license status changes, update the pinned manifest
entry and this record before comparison resumes. A new external source stays
local-only unless a separate redistribution review explicitly authorizes a
different boundary.
