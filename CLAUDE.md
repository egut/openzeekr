# CLAUDE.md

Project instructions are shared by all agents in AGENTS.md, imported here:

@AGENTS.md

## Claude Code specifics

- Shared permissions live in `.claude/settings.json`. It allows the Gradle
  verify and Trunk commands, and denies reading the secret files listed in
  AGENTS.md. Put personal overrides in `.claude/settings.local.json`
  (gitignored).
- `/release <version>` runs the release procedure in
  `.claude/skills/release/SKILL.md`. It only runs when invoked explicitly.
- Check `/context` to confirm this file and `AGENTS.md` are loaded.
