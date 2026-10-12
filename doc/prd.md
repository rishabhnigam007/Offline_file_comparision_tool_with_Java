# Offline Java Compare Tool - PRD

## 1. Executive Summary
**Offline Java Compare Tool** is a lightweight, pure **Java 21** desktop application that enables developers and users to compare two text or Java source files side-by-side with rich visual diffing, synchronized scrolling, intra-line word difference detection, and high-contrast **Pure White** and **Pure Black** themes. It guarantees 100% offline privacy with zero external telemetry.

---

## 2. Product Goals
- Deliver an instant-launch, side-by-side file comparison tool with zero setup complexity.
- Support real-time parallel comparison for typed, pasted, or drag-and-dropped files.
- Provide granular visual diffs (insertions, deletions, modifications, and character/word badges).
- High-contrast pure White and pure Black modes with clean icons and tooltips.
- 100% offline operation with zero network dependencies.

---

## 3. Key Functional Features

### 3.1. Dual-Pane Side-by-Side Editor
- **Left Pane:** Original baseline text file.
- **Right Pane:** Modified target text file.
- **Line Numbers in Both Modes:**
  - **Normal Text Mode:** Clean IDE-style line number gutter with automatic soft-wrap awareness and click-to-jump caret positioning.
  - **Visual Diff Mode:** Fixed/dynamic Git gutter with 1px border separator and `+` / `-` change indicators.
- **Direct Input & Drag-and-Drop:** Paste text directly into either pane or drag-and-drop files from Windows Explorer.

### 3.2. Git-Style Split Diff Calculation & Highlighting
- **Matching Lines ("if match leave it"):** Clean, untouched editor background with no distracting colors; matching lines sit on the exact same horizontal row.
- **Additions (Right):** Soft green background (`#e6ffec`) with bold green gutter marker (`+`); opposing left pane renders an explicit diagonal-striped **GAP**.
- **Deletions (Left):** Soft red background (`#ffebe9`) with bold red gutter marker (`-`); opposing right pane renders an explicit diagonal-striped **GAP**.
- **Modifications:** Left shows deleted old version in soft red with darker red intra-line badges; right shows added new version in soft green with darker green intra-line badges.
- **Proper Spacers / Gaps:** Explicit diagonal-hatch spacer rows on the opposing side ensure matching lines below remain perfectly horizontally aligned.
- **Overview Mini-Map Bar:** Right-edge overview bar with colored tick marks and click-to-jump viewport box.

### 3.3. Synchronized Scrolling
- **Lock-step Dual Scrolling:** Scrolling either pane simultaneously translates the sibling pane.
- **Virtual Spacer Alignment:** Inserts visual padding rows so matching lines remain aligned horizontally.

### 3.4. Filter Toggles & Actions
- `Word wrap` (Toggle soft line wrapping, **checked by default**).
- `Hide Unchanged` (Fold/collapse identical blocks).
- `Trim Lines` (Ignore leading and trailing whitespace, **checked by default**).
- `Ignore Case` (Case-insensitive matching).
- `◀ PREVIOUS` and `NEXT ▶` step navigation buttons.
- Header actions: `🔄 Reset`, `💾 Export Report`, `🌓 Theme`.

---

## 4. Non-Functional Requirements

| Metric | Target |
| :--- | :--- |
| **Java Platform** | Java 21 LTS |
| **Build & Run** | Standard Maven (`mvn compile exec:java` / `mvn package`) |
| **Packaging** | Executable standalone Fat-JAR (`target/java-diff-compare-1.0.0.jar`) |
| **Startup Latency** | < 1 second |
| **Diff Calculation** | < 100ms for 5,000 lines |
| **Privacy & Security** | 100% offline, zero network requests |
