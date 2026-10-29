# Neo-Brutalist Design System & UI/UX Guidelines

## 1. Design Philosophy: Neo-Brutalism
The **Offline Java Compare Tool** uses a modern, high-contrast **Neo-Brutalist** design system.
Unlike soft, low-contrast UI paradigms, Neo-Brutalism delivers raw visual energy, unapologetic clarity, and maximum readability through bold solid black outlines, hard offset drop shadows (zero blur), saturated color blocking, and punchy typography.

### Core Principles
1. **Solid Black Contours:** Every card, button, badge, and input container is framed with a bold `2px` solid black border outline (`#000000`).
2. **Hard Offset Drop Shadows (Zero Blur):** Elements cast crisp, un-blurred solid black shadows offset down-right (e.g. `3px` to `4px`).
3. **Mechanical Tactile Clicks:** When buttons are clicked/pressed, they shift down-right by `(3px, 3px)` to physically occupy their shadow offset, giving a tactile mechanical click sensation.
4. **Vibrant Color-Blocking:** High-energy saturated accent colors:
   - **Electric Yellow (`#FFE600`):** Hero brand accent, mode toggle, header banners.
   - **Vibrant Lime Green (`#00F076`):** Primary Compare action (`⚡ COMPARE`) & Identical diff badge.
   - **Electric Pink-Red (`#FF4365`):** Deletions, diff counter alerts & Reset.
   - **Electric Sky Blue (`#38B6FF`):** Visual diff mode toggle & informational states.
5. **Dual Modes:** Complete support for **Neo-Brutalist Light** (warm retro cream canvas `#FDFAF3` with stark white cards) and **Cyber Neo-Brutalist Dark** (obsidian canvas `#141416` with high-contrast borders and neon accents).

---

## 2. Color Palette & Neo-Brutalist Tokens

### 2.1. Base Canvas & Surface Tokens

| Token Name | Light Neo-Brutalism | Cyber Dark Neo-Brutalism | Visual Role |
| :--- | :--- | :--- | :--- |
| `canvas` | `#fdfaf3` (Warm Retro Cream) | `#141416` (Deep Obsidian) | Main window backdrop |
| `surface` | `#ffffff` (Stark White) | `#1e1e22` (Card Dark) | Editor panes & container cards |
| `surfaceAlt` | `#f5f1e9` (Muted Cream) | `#282830` | Header strips & line gutters |
| `border` | `#000000` (Solid Thick Black) | `#ffffff` / `#000000` | Heavy 2px solid outlines |
| `shadow` | `#000000` (Hard Drop Shadow) | `#000000` | Zero-blur offset shadow |

### 2.2. Text & Typography Tokens

| Token Name | Light Token | Dark Token | Purpose |
| :--- | :--- | :--- | :--- |
| `textPrimary` | `#000000` (Pure Black) | `#ffffff` (Pure White) | Code, primary headings, labels |
| `textSecondary` | `#3c3c3c` (Dark Charcoal) | `#d2d2d2` | Subtitles & option labels |
| `textMuted` | `#6e6e6e` | `#969696` | Gutter numbers & collapsed spacers |

### 2.3. Saturated Accent Palette

| Accent | Hex Code | Visual Application |
| :--- | :--- | :--- |
| **Electric Yellow** | `#ffe600` | Theme toggle button, selection highlights, table headers |
| **Vibrant Lime** | `#00f076` | `⚡ COMPARE` button, `+` addition badges, Identical status |
| **Electric Pink-Red** | `#ff4365` | `🔄 RESET` button, `-` deletion badges, Diff alert counts |
| **Electric Sky Blue** | `#38b6ff` | `🔍 Visual Diff View` toggle, informational badges |
| **Retro Purple** | `#a259ff` | Secondary badges & tags |
| **Punchy Orange** | `#ff914d` | Modification highlights |

### 2.4. Diff State Colors

| Semantic State | Light Token | Dark Token | Description |
| :--- | :--- | :--- | :--- |
| **Match (Identical Line)** | `#ffffff` (Clean Surface) | `#1e1e22` (Clean Surface) | Unchanged lines ("if match leave it") |
| **Addition Background** | `#e6fce8` (Clean Light Mint) | `#0c2c16` (Forest Green) | Full row addition background |
| **Addition Badge** | `#00f076` + 1px outline | `#00b455` | Intra-line added word token |
| **Addition Text** | `#007a33` (Deep Green) | `#82ffaa` (Bright Mint) | High-contrast green text |
| **Deletion Background** | `#ffebee` (Clean Light Rose) | `#370e16` (Deep Crimson) | Full row deletion background |
| **Deletion Badge** | `#ff4365` + 1px outline | `#be1e3c` | Intra-line deleted word token |
| **Deletion Text** | `#c40026` (Deep Red) | `#ffa0af` (Bright Rose) | High-contrast red text |
| **Spacer / Gap Pattern** | `#f2f2f2` + `#bebebe` stripes | `#18181c` + `#323238` stripes | Diagonal hatch pattern for aligned gaps |

---

## 3. Typography Hierarchy

### 3.1. Code & Editor Font
- **Primary:** `JetBrains Mono`
- **Fallback:** `Consolas`, `Monaco`, `monospace`
- **Default Size:** `13px`
- **Row Height:** `24px` for comfortable parallel scanning.

### 3.2. User Interface Font
- **Primary:** `Segoe UI`, `-apple-system`, `Inter`, `Roboto`, `sans-serif`
- **Scale:**
  - **Window Title:** `20px`, Bold 800/900, Uppercase
  - **Pane Headers:** `12px`, Bold 700, Uppercase
  - **Buttons & Badges:** `12px`, Bold 700, Uppercase
  - **Option Toggles:** `12px`, Bold 700
  - **Gutter Numbers:** `12px`, Plain Monospace

---

## 4. Component Hierarchy & Architecture

```
+--------------------------------------------------------------------------------------------------+
| Top Bar (NeoBrutalistPanel - 2px Border, 4px Shadow):                                            |
| ⚡ OFFLINE JAVA COMPARE TOOL                     [🔄 RESET] [💾 EXPORT REPORT] [🌓 THEME]       |
+--------------------------------------------------------------------------------------------------+
| [Left Editor Card - 2px Border, 4px Shadow]   | | [Right Editor Card - 2px Border, 4px Shadow]   |
| 📄 ORIGINAL (LEFT)                            | | 📄 MODIFIED (RIGHT)                          | |
| 1-   public void execute() {                  | | 1+   public void run() {                     | |
| 2        log.info("start");                   | | 2        log.info("start");                  | |
| 3-       doOldWork();                         | | 3+       doNewWork();                        | |
| 4    }                                        | | 4    }                                       | |
+--------------------------------------------------------------------------------------------------+
| Bottom Dock (NeoBrutalistPanel - 2px Border, 4px Shadow):                                        |
| [🔘 Word wrap] [🔘 Hide Unchanged] [🔘 Trim]   | [◀ PREV] [NEXT ▶] [⚠️ 2 DIFFS] [🔍 DIFF] [⚡]   |
+--------------------------------------------------------------------------------------------------+
```

### 4.1. NeoBrutalistPanel (`com.javacompare.ui.neobrutalism.NeoBrutalistPanel`)
- Standard container card with `borderWidth = 2.0f`, `shadowOffset = 4px`, `arc = 8px`.
- Renders hard drop shadow at `(x + 4, y + 4)` with zero blur.

### 4.2. NeoBrutalistButton (`com.javacompare.ui.neobrutalism.NeoBrutalistButton`)
- 2px solid black outline with 3px hard black shadow.
- Mechanical click feedback: Shifts `(3px, 3px)` down-right to occupy the shadow space upon mouse press.
- Styles: `DEFAULT`, `PRIMARY` (Blue), `SUCCESS` (Lime Green), `DANGER` (Red/Pink), `WARNING` (Yellow).

### 4.3. NeoBrutalistToggle (`com.javacompare.ui.neobrutalism.NeoBrutalistToggle`)
- Chunky switch toggle with 2px solid black border and 2px hard black shadow.
- Lights up with Electric Yellow when selected.

### 4.4. NeoBrutalistBadge (`com.javacompare.ui.neobrutalism.NeoBrutalistBadge`)
- Status pill / tag with 2px border, 2px hard shadow, and punchy uppercase text.

### 4.5. NeoBrutalistScrollBarUI (`com.javacompare.ui.neobrutalism.NeoBrutalistScrollBarUI`)
- Crisp scrollbar with 1.5px border, high-contrast thumb, and yellow highlight on drag.
