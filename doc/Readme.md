# Offline Java Compare Tool (Neo-Brutalist Edition)

> A modern, lightning-fast, offline desktop application for parallel file and text comparison built with **Java 21**, **Maven**, and a high-contrast **Neo-Brutalist** design system.

---

## 📸 Preview & Layout

**Offline Java Compare Tool** provides an intuitive, high-performance side-by-side desktop interface with bold `2px` solid black outlines, hard offset drop shadows (zero blur), saturated color blocking (Electric Yellow, Lime Green, Coral Pink, Cyan), and tactile mechanical buttons:

```
+--------------------------------------------------------------------------------------------------+
|  ⚡ OFFLINE JAVA COMPARE TOOL                       [🔄 RESET]  [💾 EXPORT REPORT]  [🌓 THEME]    |
+--------------------------------------------------------------------------------------------------+
| 📄 ORIGINAL (LEFT)                        || 📄 MODIFIED (RIGHT)                        | | Mini |
| 1- package com.example;                   || 1+ package com.example.v2;                 | | [■]  |
| 2  public class Calculator {              || 2  public class Calculator {              | |      |
| 3-     public int add(int a, int b) {     || 3+     public long add(long a, long b) {   | | [■]  |
| 4          return a + b;                  || 4          return a + b;                  | |      |
| 5      }                                  || 5      }                                  | |      |
+--------------------------------------------------------------------------------------------------+
| [🔘 Word Wrap] [🔘 Hide Unchanged] [🔘 Trim Lines] [🔘 Ignore Case]     ◀ PREV  NEXT ▶  (2 DIFFS) |
+--------------------------------------------------------------------------------------------------+
```

---

## ✨ Features

- **Neo-Brutalist Design:** Bold 2px solid black outlines, hard offset drop shadows (zero blur), saturated color blocking, and mechanical button shift upon click.
- **Dual Themes (Retro Light & Cyber Dark):** High-contrast retro cream (`#fdfaf3`) and deep obsidian (`#141416`) palettes with instant live switching.
- **Parallel Dual-Pane View:** Side-by-side comparison of original vs. modified text or source files.
- **Real-Time Visual Diffs:** Saturated additions (`#e6fce8` mint), deletions (`#ffebee` rose), and intra-line word/character badges with 1px black borders.
- **Synchronized Scrolling:** Lock-step synchronized scrolling with virtual spacer alignment for matching lines.
- **Interactive Overview Mini-Map:** Mini-map on the right margin with hard drop shadow, punchy diff markers, and draggable viewport indicator.
- **Chunky Neo-Brutalist Toggles:**
  - Soft word wrap toggle.
  - Hide unchanged lines (focus exclusively on diff blocks).
  - Trim whitespace & ignore case.
- **Step-by-Step Navigation:** `◀ PREV` and `NEXT ▶` tactile buttons with physical click displacement.
- **Drag-and-Drop & Instant Paste:** Drop files from Windows Explorer or paste raw text straight from the clipboard.
- **Standalone Neo-Brutalist HTML Export:** Generates clean offline HTML reports styled with matching Neo-Brutalist CSS (solid black borders, hard offset shadows, vibrant badges).
- **100% Private & Offline:** Operates entirely locally on your machine with zero cloud connectivity or data transmission.
- **Robust SLF4J / Logback Logging:** Structured console logging for file I/O and diff calculations.

---

## 🚀 Quick Start (Java 21 & Maven)

### Prerequisites
- **Java Development Kit (JDK):** Version 21 LTS installed (`JAVA_HOME` configured).
- **Apache Maven:** Version 3.8.0 or newer.

### Build and Run

1. **Compile and Run directly:**
   ```bash
   mvn clean compile exec:java
   ```

2. **Package Standalone Executable JAR:**
   ```bash
   mvn clean package
   java -jar target/java-diff-compare-1.0.0.jar
   ```

3. **Run Test Suites:**
   ```bash
   mvn test
   ```

---

## 📚 Documentation Index

All technical documentation is organized in the [doc/](file:///e:/java_compare_classes_project/doc/) directory:

| Document | Description |
| :--- | :--- |
| [prd.md](file:///e:/java_compare_classes_project/doc/prd.md) | Product Requirements Document, user stories, and acceptance criteria. |
| [Architecture.md](file:///e:/java_compare_classes_project/doc/Architecture.md) | System architecture, component models, sequence diagrams, and package layouts. |
| [design_system.md](file:///e:/java_compare_classes_project/doc/design_system.md) | Neo-Brutalist design system, border strokes, hard shadows, tokens, and components. |
| [memory.md](file:///e:/java_compare_classes_project/doc/memory.md) | Living project memory and architectural handbook. |
| [code_style.md](file:///e:/java_compare_classes_project/doc/code_style.md) | Java 21 coding standards, SLF4J rules, and Clean Code guidelines. |
| [implementation_plan.md](file:///e:/java_compare_classes_project/doc/implementation_plan.md) | Step-by-step development roadmap and phase breakdown. |
| [testing.md](file:///e:/java_compare_classes_project/doc/testing.md) | Test cases, scenarios, and QA strategy. |
| [security.md](file:///e:/java_compare_classes_project/doc/security.md) | Offline privacy, zero telemetry, read-only file access. |
| [Agents.md](file:///e:/java_compare_classes_project/doc/Agents.md) | AI coding agent instructions and architectural guardrails. |
