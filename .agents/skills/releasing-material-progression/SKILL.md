---
name: releasing-material-progression
description: Use when rebuilding, validating, committing, publishing, or integrating the installable Material Progression JAR or a release commit.
---

# Releasing Material Progression

## Establish the release candidate

Read `AGENTS.md`, `README.md`, `gradle.properties`, `build.gradle`, and
`.github/workflows/build.yml`. Inspect the branch, remote base, dirty files, and
worktrees. The release candidate must contain only intended changes.

Never discard unrelated changes, force-push, or update `main` without explicit
authorization. Confirm the remote branch SHA immediately before integration and
use a non-forced fast-forward.

## Synchronize the artifact

`mod_version` determines the one allowed file under `dist/`. After every
production source or resource change, build the tracked artifact with:

```bash
./gradlew syncDistributionJar
```

Update the README install link when the version changes. Remove obsolete
versioned JARs so `dist/` contains exactly one production artifact. Never commit
a sources, development, or GameTest JAR as the installable mod.

## Verify the exact tree

Run fresh verification after the final source and JAR state is assembled:

```bash
./gradlew headlessTest
```

This must prove:

- Fast resource and documentation contracts pass.
- Java 25 compilation and packaging pass.
- The committed JAR is byte-for-byte identical to a fresh production build.
- NeoForge GameTests pass.
- GameTest classes are absent from the production JAR.

Commit source, version metadata, README link, and the refreshed `dist/` JAR
together. If any production input changes after the build, rebuild and rerun the
complete verification.

## Publish safely

Push the reviewed candidate branch first when possible and require the GitHub
Actions workflow to pass on the exact commit. Before integration:

1. Verify the remote candidate tree matches the tested local tree.
2. Confirm the current remote `main` is still the candidate's ancestor.
3. Advance `main` without force only when explicitly requested.
4. Verify the integrated remote tree.
5. Wait for the `main` workflow on the exact integrated SHA.

Report the commit, installable JAR link, and exact verification evidence. Do not
claim release success from an earlier commit or a different tree.
