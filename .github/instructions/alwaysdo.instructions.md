# Standard Working Methodology for AI Coding Agents

## Mandatory Research-First Workflow

Every task MUST follow this sequence. No exceptions — do not skip steps.

Always create an instruction.txt file where user can talk to you directly ,always create this file fresh on wait for 10 mins in loop checking thrice before signing off ill provide any changes in the file ,also ,if requirement is detected then , 3 minutes loop for 10 times waiting limits resets and you do it again

reset the poll count to 1 and cycle also to 1 whenever you detect new instructions

create memory files within the code that you can use next time to get back the context and memory on the code you are working on that way ,you might have clearer  context all the time

While debugging if app installation has issues then do as below

 try using auto_click_quick.ps1,if autoclick is not working please do it manually, click first then automate it for usb auto pop accepting in MiUI ,dont waste more than 30 mins trying to debug old automation file

### Step 1: Research Best Practices (BEFORE writing any code)

- **Web search first**: Use `fetch_webpage` to search for current best practices related to the feature being implemented. Target authoritative sources:
  - Android developer docs (`developer.android.com`)
  - Frida docs (`frida.re/docs`)
  - Stack Overflow top answers
  - Medium / dev.to engineering posts
- **Enterprise testing methodology**: Search for how companies test the specific type of change (UI testing, network interception, smali patching, APK signing, etc.)
- **Extract actionable patterns**: Don't just read — pull out the specific API calls, parameter orders, and edge cases that apply to the current task.

### Step 2: Find Reference Implementations on GitHub

- For **every feature**, search GitHub for a well-maintained repository that solves the same problem. Examples:
  - UI styling → search for Material Design Android examples
  - Frida hooks → search for frida-scripts repositories with similar hooks
  - Smali injection → search for APK patching tools with similar injection patterns
  - Network blocking → search for firewall/ad-blocker Android implementations
- **Evaluate quality**: Prefer repos with >50 stars, recent commits, and clean code structure
- **Adapt, don't copy**: Extract the pattern and adapt it to this codebase's conventions (smali style, Frida script structure, PowerShell build pipeline)

### Step 3: Implement with Least-But-Best Effort

- **Minimal code, maximum impact**: Prefer small, surgical changes that produce big visible results
- **UI-first thinking**: Every change the user can see should look polished — colors, spacing, font sizes, contrast ratios all matter
- **One purpose per change**: Each edit should do one thing well rather than cramming multiple concerns together
- **Reuse existing infrastructure**: Use `HSPatchConfig` helpers, existing smali patterns, Frida's try/catch wrappers — don't reinvent

### Step 4: Test on Device

- Build and deploy after every meaningful change (`pack_patches.ps1` → `build.ps1` → `adb install`)
- Verify via logcat, UI dumps, or screenshots — don't assume it works
- If a test fails, diagnose with `adb logcat` before attempting fixes

---

## UI Enhancement Principles

When touching anything visual, follow these rules:

1. **Contrast**: Text must be clearly readable — never use opacity below 80% for important text. Prefer bold accent colors (`#00E676` green, `#FF5252` red, `#448AFF` blue) on dark backgrounds
2. **Size**: Interactive text ≥ 14sp, labels ≥ 12sp, headers ≥ 18sp
3. **Spacing**: Minimum 8dp padding on all interactive elements, 16dp between sections
4. **Feedback**: Every user action should produce visible feedback (toast, color change, log entry)
5. **Consistency**: Match existing color scheme and styling patterns already in the codebase

---

## Quality Checklist (verify before marking done)

- [ ] Searched web for best practices relevant to this change
- [ ] Found and reviewed at least one GitHub reference implementation
- [ ] Code change is minimal and surgical — no unnecessary refactors
- [ ] UI changes are visually verified (screenshot or UI dump)
- [ ] Tested on physical device with `adb logcat` confirming no crashes
- [ ] Version bumped if this is a release-worthy change
- [ ] Code pushed to GitHub (`git add . && git commit && git push`)
- [ ] GitHub release created for version bumps (tag matching versionName)

---

## Git & GitHub — Always Push

After every successful build+test cycle:

```powershell
cd C:\Users\vishw\all_tools\app_mod\HSPatcher
git add -A
git commit -m "v<VERSION>: <concise summary of changes>"
git push origin main
```

For version-bump releases, also create a GitHub release tag:

```powershell
git tag v<VERSION>
git push origin v<VERSION>
```

**Never leave working changes uncommitted.** Push is mandatory, not optional.
