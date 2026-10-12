# Design System & UI/UX Guidelines

## 1. Design Principles
The **Offline Java Compare Tool** design system is crafted to provide a clean, distraction-free, high-contrast developer experience.
It adheres strictly to **Pure White** (Light) and **Pure Black** (Dark) themes with crisp borders and zero muddy gray panel backgrounds.

---

## 2. High-Contrast Color Palette & Tokens

### 2.1. Diff State Colors (Git-Style Split Diff)

| Semantic State | Pure White Theme Token | Pure Black Theme Token | Purpose / Application |
| :--- | :--- | :--- | :--- |
| **Match (Identical Line)** | `#ffffff` (Clean White) | `#0d1117` (Git Dark) | Unchanged lines ("if match leave it") - no distracting colors |
| **Addition Background (Line)** | `#e6ffec` (Soft Green) | `#112a1a` (Forest Green) | Full row background for added lines (Right pane) |
| **Addition Word Badge** | `#abf2bc` (Vibrant Mint) | `#236233` (Emerald Badge) | Intra-line added word/character badge |
| **Addition Gutter Indicator** | `#1a7f37` (Green 700) | `#3fb950` (Green 400) | Gutter `+` indicator and line number |
| **Deletion Background (Line)** | `#ffebe9` (Soft Red) | `#3a1014` (Deep Crimson) | Full row background for deleted lines (Left pane) |
| **Deletion Word Badge** | `#ffc1ba` (Coral Badge) | `#7e2226` (Ruby Badge) | Intra-line deleted word/character badge |
| **Deletion Gutter Indicator** | `#cf222e` (Red 700) | `#f85149` (Red 400) | Gutter `-` indicator and line number |
| **Gap / Spacer (Opposing Side)** | `#f6f8fa` + diagonal stripes | `#161b22` + diagonal stripes | Explicit gap pattern on opposing side when lines are added/deleted |

### 2.2. Base Application UI Colors

| Token Name | Pure White Theme | Pure Black Theme | Usage |
| :--- | :--- | :--- | :--- |
| `bg-primary` | `#ffffff` (Pure White) | `#000000` (Pure Black) | Main window and editor pane background |
| `bg-gutter` | `#ffffff` (Pure White) | `#000000` (Pure Black) | Gutter / line number background |
| `border-subtle` | `#d0d0d0` (Crisp Border) | `#333333` (Crisp Dark Border) | Pane dividers, splitters, editor outlines |
| `text-primary` | `#000000` (Jet Black) | `#ffffff` (Crisp White) | Standard code and primary UI labels |
| `text-muted` | `#646464` | `#a0a0a0` | Line numbers, secondary descriptions |
| `accent-primary` | `#0969da` (Blue) | `#2f81f7` (Blue) | Active toggle switches, primary buttons |
| `accent-success` | `#1a7f37` (Green) | `#238636` (Green) | Compare action button, identical badge |
| `accent-danger` | `#cf222e` (Red) | `#f85149` (Red) | Diff alert counter, reset confirmation |

---

## 3. Typography Hierarchy

### 3.1. Code & Editor Font
- **Font Family:** `JetBrains Mono`, `Fira Code`, `Consolas`, `Monaco`, `monospace`
- **Font Size:** `13px` (default)
- **Line Height:** `22px` row height for crisp tabular reading.

### 3.2. User Interface Font
- **Font Family:** `Segoe UI`, `-apple-system`, `Inter`, `Roboto`, `sans-serif`
- **Scale:**
  - **Window Title / Header:** `22px`, Bold (Weight 700)
  - **Section Subtitle / Description:** `12px`, Regular (Weight 400)
  - **Toolbar Buttons & Labels:** `12px`, Plain / Medium
  - **Status & Counter Badges:** `12px`, Semi-bold (Weight 600)

---

## 4. Layout Architecture & Component Specifications

```
+--------------------------------------------------------------------------------------------------+
| Top Bar: ⚡ Title & Subtitle + [🔄 Reset] [💾 Export Report] [🌓 Theme]                          |
+--------------------------------------------------------------------------------------------------+
| [Gutter] Original Editor Pane            | | [Gutter] Modified Editor Pane             | [Mini] |
| 1-       public void execute() {         | | 1+       public void run() {              | [Map ] |
| 2            log.info("start");          | | 2            log.info("start");           | [Diff] |
| 3-           doOldWork();                | | 3+           doNewWork();                 | [Bar ] |
| 4        }                               | | 4        }                                | [    ] |
+--------------------------------------------------------------------------------------------------+
| Bottom Toolbar: [Filters & Toggles] | [◀ PREVIOUS] [NEXT ▶] [Diff Counter] [⚡ COMPARE]          |
+--------------------------------------------------------------------------------------------------+
```

### 4.1. Split-Pane Layout
- **Split Ratio:** 50% / 50% split.
- **Center Divider:** 4px wide crisp drag handle.
- **Synchronized Scroll Alignment:** Left and Right scroll viewports maintain 1-to-1 vertical translation.

### 4.2. Gutter & Line Indicators (Both Modes)
- **Normal Text Mode:** Custom `TextLineNumberPanel` row header gutter attached to `JScrollPane`. Handles soft line wrapping (only first visual line of a wrapped line displays a number), click-to-jump caret positioning, and dynamic digit expansion.
- **Visual Diff Mode:** Gutter column with dynamic scaling (`Math.max(62, digits * charW + 32)`), right-aligned line numbers, 1px crisp vertical separator line, Git `-` / `+` change markers, and diagonal stripe gap patterns when aligned against opposite additions/deletions.
- **Theme Matching:** `#f6f8fa` (Light) / `#161b22` (Dark) with matching border lines across both modes.

### 4.3. Overview Mini-Map Bar
- Positioned on the rightmost margin (`16px` width).
- Colors: Pure White background in Light mode, Jet Black in Dark mode.
- Rendered ticks: Colored bars at exact line ratio locations.
- Viewport Indicator: Translucent box showing the visible document region with click-to-jump.

---

## 5. Controls, Icons & Interactive Elements

- **Header Buttons:** Clean iconography (`🔄 Reset`, `💾 Export Report`, `🌓 Theme`).
- **Interactive Tooltips:** Every button and toggle includes descriptive tooltips.
- **Navigation Controls:** `◀ PREVIOUS` and `NEXT ▶` with keyboard jump support.
