# System Architecture & Technical Design

## 1. Architecture Overview

The **Offline Java Compare Tool** is designed following clean, modular desktop architecture. It cleanly separates the Myers diff computation engine from the presentation layer, enabling fast, thread-safe, and testable file comparisons.

```mermaid
graph TD
    subgraph UI Layer [UI Layer (Java Swing + FlatLaf)]
        MainWindow[MainWindow / Root Layout]
        DiffViewPanel[DiffViewPanel (Dual-Pane Table)]
        DiffMiniMap[DiffMiniMap Overview Bar]
        ScrollSyncCoordinator[ScrollSyncCoordinator]
    end

    subgraph Diff Engine [Core Diff Engine Layer]
        DiffService[DiffService]
        MyersDiffUtils[java-diff-utils (Myers Diff)]
        WordDiffCalculator[WordDiffCalculator (LCS)]
        DiffResultModel[DiffResult & DiffRow Models]
    end

    subgraph Infrastructure [File I/O & Logging]
        FileLoader[FileLoader & Charset Detector]
        ReportExporter[ReportExporter (HTML Export)]
        SLF4J[SLF4J + Logback Structured Logging]
    end

    MainWindow --> DiffViewPanel
    MainWindow --> DiffService
    MainWindow --> FileLoader
    MainWindow --> ReportExporter
    DiffViewPanel --> ScrollSyncCoordinator
    DiffViewPanel --> DiffMiniMap
    DiffService --> MyersDiffUtils
    DiffService --> WordDiffCalculator
    DiffService --> DiffResultModel
    MainWindow --> SLF4J
    DiffService --> SLF4J
    FileLoader --> SLF4J
```

---

## 2. Technology Stack & Dependencies

| Layer / Concern | Technology Selection | Purpose |
| :--- | :--- | :--- |
| **Language** | **Java 21 LTS** | Modern Java features (Records, Pattern Matching, Text Blocks, Virtual Threads). |
| **GUI Framework** | **Java Swing + FlatLaf 3.5.4** | Modern, high-DPI scaled, pure White and pure Black themes. |
| **Diff Engine** | `io.github.java-diff-utils:java-diff-utils:4.12` | Industry-standard Myers diff algorithm. |
| **Logging** | `org.slf4j:slf4j-api` + `ch.qos.logback:logback-classic` | Clean, structured console logging. |
| **Build System** | **Apache Maven** (`maven-shade-plugin`) | Standard build lifecycle and executable standalone Fat-JAR packaging. |
| **Testing** | JUnit 5 + AssertJ | Comprehensive unit testing. |

---

## 3. Core Subsystems

### 3.1. Diff Engine Subsystem
1. **Line Tokenizer & Normalizer:** Tokenizes raw text into line arrays, applying options (`trimWhitespace`, `ignoreCase`).
2. **Myers Delta Computation:** Computes edit deltas (`INSERT`, `DELETE`, `CHANGE`, `EQUAL`).
3. **WordDiffCalculator (LCS):** For modified lines, computes character and token-level Longest Common Subsequence to generate `TextSpan` inline highlights.
4. **Spacer Alignment Generator:** Synthesizes virtual spacer rows so matching unchanged lines remain horizontally aligned.

### 3.2. Synchronized Scrolling Coordinator
- **ScrollSyncCoordinator:** Hooks into both left and right `JScrollPane` vertical and horizontal scrollbars.
- **Mutual Scroll Binding:** Updates sibling viewport translation with recursion guards.
- **Mini-Map Linkage:** Maps document line percentages to canvas pixel offsets with click-to-jump.

### 3.3. File I/O & Export Subsystem
- **FileLoader:** Reads files safely with UTF-8, UTF-16, and ISO-8859-1 charset detection and BOM stripping.
- **ReportExporter:** Exports standalone HTML comparison reports (with escaped entities for XSS prevention) and unified `.patch` files.

---

## 4. Package Structure

```
java_compare_classes_project/
├── pom.xml                                  # Maven Java 21 build definition
├── doc/                                     # Documentation Suite
│   ├── prd.md
│   ├── Architecture.md
│   ├── Readme.md
│   ├── design_system.md
│   ├── code_style.md
│   ├── implementation_plan.md
│   ├── testing.md
│   ├── security.md
│   ├── Agents.md
│   └── documentation-audit.md
└── src/
    ├── main/
    │   ├── java/com/javacompare/
    │   │   ├── App.java                     # Main entry point & theme initialization
    │   │   ├── diff/
    │   │   │   ├── DiffService.java         # Myers diff calculation & alignment
    │   │   │   └── WordDiffCalculator.java  # Token/word LCS difference calculator
    │   │   ├── model/
    │   │   │   ├── DiffDelta.java           # Diff delta model
    │   │   │   ├── DiffOptions.java         # Comparison options record
    │   │   │   ├── DiffResult.java          # Aggregated diff result record
    │   │   │   ├── DiffRow.java             # Aligned row record
    │   │   │   ├── DiffType.java            # Diff state enum
    │   │   │   └── TextSpan.java            # Inline highlight span record
    │   │   ├── ui/
    │   │   │   ├── DiffMiniMap.java         # Right-edge overview bar
    │   │   │   ├── DiffViewPanel.java       # Dual-pane table renderer
    │   │   │   ├── MainWindow.java          # Primary JFrame & controller
    │   │   │   └── ScrollSyncCoordinator.java # Dual scrollbar synchronizer
    │   │   └── util/
    │   │       ├── FileLoader.java          # Charset & BOM detector
    │   │       └── ReportExporter.java      # HTML & Patch export generator
    │   └── resources/
    │       └── logback.xml                  # Logging configuration
    └── test/java/com/javacompare/           # JUnit 5 test suites
```
