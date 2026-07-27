# Automated Testing Harness Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use
> superpowers:subagent-driven-development (recommended) or
> superpowers:executing-plans to implement this plan task-by-task. Steps use
> checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add resource, documentation, build, and live NeoForge behavioral tests
for every deterministic feature in the current prototype, then enforce them in
GitHub Actions.

**Architecture:** Fast Python contract tests validate repository resources
without Minecraft. A development-only Java source set uses NeoForge's Test
Framework to exercise the loaded mod and ticking crusher in a GameTest server.
GitHub Actions runs contracts, build, and GameTests as separate required steps.

**Tech Stack:** Python 3 standard library, Java 25, Gradle 9.2.1,
NeoForge 26.2.0.35-beta, NeoForge Test Framework

## Global Constraints

- Tests cover only currently implemented behavior.
- GameTest code and dependencies must not ship in the production jar.
- Crusher recipes must produce exactly two dust per ore or raw metal.
- Tests assert real outputs, state changes, and loaded data rather than mocks.
- The outstanding workshop and ore-crushing documentation must remain intact.

---

### Task 1: Repository Contract Tests

**Files:**

- Create: `tests/test_resources.py`
- Create: `tests/test_docs.py`

**Interfaces:**

- Consumes: files under `src/main/resources` and Markdown files in the
  repository
- Produces: a dependency-free `python -m unittest discover -s tests -v` gate

- [ ] **Step 1: Write resource tests with literal expected contracts**

Test JSON parsing, crushing outputs, smelting outputs, alloy ratios, local
resource references, translations, models, tags, loot, and world-generation
wiring.

- [ ] **Step 2: Run tests and record any genuine resource defects**

Run: `python -m unittest discover -s tests -p 'test_*.py' -v`

Expected: Tests fail only for a real missing or inconsistent contract.

- [ ] **Step 3: Fix production resources if the tests expose a defect**

Make the smallest resource correction that restores the documented behavior.

- [ ] **Step 4: Run the complete contract suite**

Run: `python -m unittest discover -s tests -p 'test_*.py' -v`

Expected: All contract tests pass.

### Task 2: Development-Only GameTest Source Set

**Files:**

- Modify: `build.gradle`
- Create:
  `src/gameTest/java/dev/fishraposo/materialprogression/gametest/MaterialProgressionGameTests.java`

**Interfaces:**

- Consumes: the production mod output and
  `net.neoforged:testframework:${neo_version}`
- Produces: compiled GameTests discoverable by `runGameTestServer`

- [ ] **Step 1: Configure the isolated GameTest source set**

Add the source set, dependency inheritance, Test Framework dependency, and
development mod binding without adding GameTest output to the jar.

- [ ] **Step 2: Add recipe and tag tests**

Use the real server recipe manager and registries to verify crusher inputs,
two-dust outputs, repair tags, mining tags, and enchantability tags.

- [ ] **Step 3: Add live crusher tests**

Place a real crusher, fill its real inventory, tick the server, and assert fuel,
input, output, sided inventory, no-fuel behavior, and block drops.

- [ ] **Step 4: Compile the GameTest source set**

Run:
`./gradlew compileGameTestJava`

Expected: Compilation succeeds.

- [ ] **Step 5: Run the GameTest server**

Run: `./gradlew runGameTestServer`

Expected: The server exits successfully with every registered test passing.

### Task 3: CI and Developer Documentation

**Files:**

- Modify: `.github/workflows/build.yml`
- Modify: `README.md`
- Modify: `docs/ROADMAP.md`

**Interfaces:**

- Consumes: contract tests, Gradle build, and GameTest server task
- Produces: visible local commands and mandatory GitHub Actions gates

- [ ] **Step 1: Add explicit CI steps**

Run resource contracts, `build`, and `runGameTestServer` as named steps under
Java 25.

- [ ] **Step 2: Document local and CI test commands**

Explain the automated coverage and the human playtesting boundary.

- [ ] **Step 3: Run all locally available checks**

Run:

```bash
python -m unittest discover -s tests -p 'test_*.py' -v
./gradlew clean build
./gradlew runGameTestServer
git diff --check
```

Expected: Every command exits successfully.

- [ ] **Step 4: Publish and verify CI**

Push onto the existing PR branch and wait for GitHub Actions to pass at the
exact head commit.

- [ ] **Step 5: Merge and verify `main`**

Mark the PR ready, merge it, confirm the PR reports merged, confirm `main`
contains the merge commit, and confirm the post-merge workflow succeeds.

