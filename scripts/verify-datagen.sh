#!/usr/bin/env bash
set -euo pipefail

generated_root="src/generated/resources"
gradle_wrapper="${GRADLEW:-./gradlew}"

if [[ ! -d "$generated_root" ]]; then
    echo "missing generated resource root: $generated_root" >&2
    exit 1
fi

generated_status() {
    git status --porcelain --untracked-files=all -- \
        "$generated_root" \
        ":(exclude,glob)$generated_root/**/.cache/**"
}

initial_status="$(generated_status)"
if [[ -n "$initial_status" ]]; then
    echo "generated resources were dirty before datagen:" >&2
    echo "$initial_status" >&2
    exit 1
fi

first_manifest="$(mktemp)"
second_manifest="$(mktemp)"
trap 'rm -f "$first_manifest" "$second_manifest"' EXIT

write_manifest() {
    local destination="$1"
    (
        cd "$generated_root"
        find . -type f ! -path '*/.cache/*' -print0 \
            | LC_ALL=C sort -z \
            | xargs -0 sha256sum
    ) > "$destination"
}

"$gradle_wrapper" runData --stacktrace
write_manifest "$first_manifest"
first_status="$(generated_status)"

"$gradle_wrapper" runData --stacktrace
write_manifest "$second_manifest"
second_status="$(generated_status)"

failed=0
if ! diff -u "$first_manifest" "$second_manifest"; then
    echo "second datagen run changed output" >&2
    failed=1
fi

if [[ -n "$first_status" ]]; then
    echo "checked-in generated resources are stale after first datagen run:" >&2
    echo "$first_status" >&2
    failed=1
fi

if [[ -n "$second_status" ]]; then
    echo "checked-in generated resources differ after second datagen run:" >&2
    echo "$second_status" >&2
    failed=1
fi

exit "$failed"
