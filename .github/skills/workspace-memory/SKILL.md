---
name: workspace-memory
description: "**WORKFLOW SKILL** — Create, update, and maintain modular memory files after completing work in the workspace. USE FOR: saving context about code changes, architecture decisions, debugging discoveries, device-specific workarounds, version history, and session state so future sessions have full context. TRIGGER WHEN: finishing a coding task, committing changes, discovering a bug fix, finding a workaround, or when the user says 'save memory' or 'update context'. DO NOT USE FOR: reading existing memory (use memory tool directly); general coding tasks."
---

# Workspace Memory Management

## Purpose
Ensure that after every meaningful coding session, the agent creates or updates modular memory files so future sessions start with full context about the project, its quirks, and current state.

## Memory File Structure

### Repository Memory (`/memories/repo/`)
Persistent facts about this codebase. Updated when architecture, conventions, or tooling changes.

| File | Contents | Update When |
|------|----------|-------------|
| `hspatcher-architecture.md` | Project structure, directory layout, build pipeline, SDK paths, key commands | Source files added/removed, build process changes |
| `agent-js-guide.md` | Section map, line ranges, known gotchas, coding conventions | New sections added, bugs discovered in agent.js |
| `device-testing.md` | Device serials, install workarounds, logcat filters, testing checklist | New device added, new workaround discovered |
| `version-history.md` | Version log with features, commit hashes, uncommitted changes | After each commit/release |
| `debugging-guide.md` | Build errors, runtime errors, smali patching guide, performance notes | After fixing a new bug or finding a pattern |

### Session Memory (`/memories/session/`)
Temporary state for the current conversation. Tracks in-progress work.

| File | Contents | Update When |
|------|----------|-------------|
| `current-state.md` | Uncommitted changes, pending tasks, last build details | After each build, after each significant change |

### User Memory (`/memories/`)
Cross-workspace preferences (loaded automatically).

| File | Contents |
|------|----------|
| `user.md` | User preferences, working patterns, instruction file rules |

## Workflow

### After Completing a Feature
1. Update `/memories/repo/version-history.md` — add the feature to the "Uncommitted Changes" section
2. Update `/memories/repo/agent-js-guide.md` if agent.js sections changed (new section, line shifts)
3. Update `/memories/session/current-state.md` with remaining tasks

### After Committing/Releasing
1. Move "Uncommitted Changes" in version-history.md to a new version entry
2. Add commit hash and tag
3. Clear completed items from session current-state.md

### After Discovering a Bug Fix
1. Add the fix pattern to `/memories/repo/debugging-guide.md`
2. If it's an agent.js gotcha, add to `/memories/repo/agent-js-guide.md` under "Known Gotchas"

### After Testing on a New Device
1. Add device details to `/memories/repo/device-testing.md`
2. Document any device-specific workarounds

### After Architecture Changes
1. Update `/memories/repo/hspatcher-architecture.md` directory layout and build steps

## Guidelines
- **Keep files modular** — one topic per file, easy to read in isolation
- **Keep entries concise** — bullet points and tables, not paragraphs
- **Update, don't duplicate** — use `str_replace` on existing files rather than creating new ones
- **Always check existing files first** — `memory view /memories/repo/` before creating
- **Include specifics** — commit hashes, line numbers, exact error messages, exact command sequences
- **Remove stale info** — delete entries that are no longer accurate
