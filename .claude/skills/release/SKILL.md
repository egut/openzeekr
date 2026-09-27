---
name: release
description: Prepare an OpenZeekr release (version bump for phone + watch, changelogs, README, verify, release commit). Use only when the user explicitly asks to cut a release.
argument-hint: "[version, e.g. 0.1.8]"
disable-model-invocation: true
---

# Release procedure

Prepare release **$ARGUMENTS**. This procedure is plain markdown, so any agent can
follow it, not only Claude Code. Do not push, tag or publish unless the user
explicitly asks.

## 1. Preconditions

- The working tree is clean, and you're on an up-to-date `master` or a release
  branch the user named.
- `CHANGELOG.md` has a `## [Unreleased]` section with the changes. If it's
  empty, stop and ask what the release contains.
- The version is new and semver-shaped `X.Y.Z`. Check it with
  `git tag --list 'v*'`.

## 2. Version bump (phone + watch)

The phone and watch APKs share one `applicationId` and ship in one Play
release, so they need distinct, increasing version codes. The rule since 0.1.4
is **phone = previous watch + 1, watch = phone + 1**. For example, 0.1.7 is
phone 9 and watch 10, so 0.1.8 is phone 11 and watch 12.

- `app/build.gradle.kts`: set `versionName = "X.Y.Z"` and
  `versionCode = <previous wear versionCode + 1>`.
- `wear/build.gradle.kts`: set `versionName = "X.Y.Z"` and
  `versionCode = <new app versionCode + 1>`.

## 3. Changelogs

- `CHANGELOG.md`: rename `## [Unreleased]` to `## [X.Y.Z] - YYYY-MM-DD`
  (today's date). Keep the Added / Changed / Fixed / Digital key subsections.
  Add a fresh, empty `## [Unreleased]` above it.
- `README.md`:
  - Update the header line `**Version X.Y.Z**`.
  - Add a `### X.Y.Z` section at the top of `## Changelog`, a short
    user-facing summary in the style of the existing entries.
  - Put any critical warning first, the way 0.1.7 did.

## 4. Verify

```bash
./gradlew --continue verify   # Windows: gradlew.bat --continue verify
trunk check
```

Both must pass. The signed release build (`./gradlew assembleRelease`) needs
the maintainer's untracked `keystore.properties`. Leave it to the maintainer
unless asked, and never read that file.

## 5. Commit

Make one commit, named `X.Y.Z: <comma-separated highlights>`, matching earlier
release commits, e.g. `0.1.7: multi-car switcher, accept shared cars in-app, …`.

Then report:

- the new version codes
- the changelog entry
- the exact commands the maintainer runs to tag and publish:
  `git tag vX.Y.Z`, `git push origin vX.Y.Z`, and the GitHub release with the
  signed APK `openzeekr-phone-X.Y.Z.apk`.
