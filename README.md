# Offline Java Compare Tool

> A modern, lightning-fast, offline desktop application for parallel file and text comparison built with **Java 21** and **Maven**.

---

## 📸 Preview & Layout

**Offline Java Compare Tool** provides an intuitive, high-performance side-by-side desktop interface with high-contrast **Pure White** and **Pure Black** themes:

```
+--------------------------------------------------------------------------------------------------+
|  ⚡ Offline Java Compare Tool                       [🔄 Reset]  [💾 Export Report]  [🌓 Theme]     |
+--------------------------------------------------------------------------------------------------+
| 📄 Original (Left)                        || 📄 Modified (Right)                        | | Mini |
| 1- package com.example;                   || 1+ package com.example.v2;                 | | [■]  |
| 2  public class Calculator {              || 2  public class Calculator {              | |      |
| 3-     public int add(int a, int b) {     || 3+     public long add(long a, long b) {   | | [■]  |
| 4          return a + b;                  || 4          return a + b;                  | |      |
| 5      }                                  || 5      }                                  | |      |
+--------------------------------------------------------------------------------------------------+
| [● Word Wrap] [● Hide Unchanged] [● Trim Lines] [● Ignore Case]   ◀ PREVIOUS  NEXT ▶  (2 Diffs)  |
+--------------------------------------------------------------------------------------------------+
```

---

## ✨ Features

- **Parallel Dual-Pane View:** Side-by-side comparison of original vs. modified text or source files.
- **Pure White & Pure Black Themes:** High-contrast crisp styling with zero muddy gray elements.
- **Real-Time Visual Diffs:** Color-coded additions (soft green), deletions (soft red), and intra-line word/character modifications.
- **Synchronized Scrolling:** Lock-step synchronized scrolling with virtual spacer alignment for matching lines.
- **Interactive Overview Mini-Map:** Visual diff overview bar on the right margin showing change distribution across entire documents with click-to-jump.
- **Flexible Comparison Filters:**
  - Soft word wrap toggle.
  - Hide unchanged lines (focus exclusively on diff blocks).
  - Trim whitespace & ignore case.
- **Step-by-Step Navigation:** `◀ PREVIOUS` and `NEXT ▶` buttons to jump between changes instantly.
- **Drag-and-Drop & Instant Paste:** Drop files from Windows Explorer or paste raw text straight from the clipboard.
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
| [implementation_plan.md](file:///e:/java_compare_classes_project/doc/implementation_plan.md) | Step-by-step development roadmap and phase breakdown. |
| [design_system.md](file:///e:/java_compare_classes_project/doc/design_system.md) | Pure Black & White visual tokens, typography, layout, and UI guidelines. |
| [code_style.md](file:///e:/java_compare_classes_project/doc/code_style.md) | Java 21 coding conventions, SLF4J logging, and Clean Code principles. |
| [testing.md](file:///e:/java_compare_classes_project/doc/testing.md) | Testing strategy, JUnit 5 unit tests, and performance benchmarks. |
| [security.md](file:///e:/java_compare_classes_project/doc/security.md) | Security posture, offline privacy, memory limits, and file access safeguards. |
| [Agents.md](file:///e:/java_compare_classes_project/doc/Agents.md) | AI coding agent protocols, development guidelines, and context maps. |
| [memory.md](file:///e:/java_compare_classes_project/doc/memory.md) | Living project memory, component map, domain models, and key mechanics. |
| [documentation-audit.md](file:///e:/java_compare_classes_project/doc/documentation-audit.md) | Documentation audit tracking matrix and review status. |

---

## 🛠️ Technology Stack
- **Language:** Java 21 LTS
- **Build System:** Apache Maven (maven-shade-plugin)
- **UI Framework:** Java Swing + FlatLaf 3.5.4 (High-contrast Light & Dark)
- **Diff Engine:** `io.github.java-diff-utils:java-diff-utils`
- **Logging:** SLF4J + Logback
- **Testing:** JUnit 5 (Jupiter), AssertJ
