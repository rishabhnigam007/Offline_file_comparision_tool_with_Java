# Implementation & Development Plan

## 1. Roadmap Overview
This document tracks the phased development plan for the **Offline Java Compare Tool**, implemented in **Java 21** using **Maven**, **FlatLaf**, and **java-diff-utils**.

---

## 2. Phase Breakdown & Status

### Phase 1: Project Setup & Maven Architecture ✅ (Completed)
- Configured [`pom.xml`](file:///e:/java_compare_classes_project/pom.xml) for Java 21 compilation.
- Integrated `flatlaf`, `flatlaf-extras`, `java-diff-utils`, `slf4j-api`, `logback-classic`, `junit-jupiter`, and `assertj-core`.
- Configured `maven-shade-plugin` for standalone executable fat-JAR packaging.
- Set up structured logging via [`src/main/resources/logback.xml`](file:///e:/java_compare_classes_project/src/main/resources/logback.xml).

### Phase 2: Core Diff Engine & Word-Level LCS ✅ (Completed)
- Implemented [`DiffService`](file:///e:/java_compare_classes_project/src/main/java/com/javacompare/diff/DiffService.java) with Myers diff algorithm.
- Implemented [`WordDiffCalculator`](file:///e:/java_compare_classes_project/src/main/java/com/javacompare/diff/WordDiffCalculator.java) for intra-line word/character highlights.
- Implemented virtual spacer row alignment for horizontal synchronization.
- Created data models: `DiffRow`, `DiffResult`, `DiffOptions`, `TextSpan`, `DiffType`.

### Phase 3: High-Contrast Pure White / Pure Black UI ✅ (Completed)
- Implemented [`MainWindow`](file:///e:/java_compare_classes_project/src/main/java/com/javacompare/ui/MainWindow.java) with interactive action buttons and iconography.
- Implemented [`DiffViewPanel`](file:///e:/java_compare_classes_project/src/main/java/com/javacompare/ui/DiffViewPanel.java) with line gutters (`1-`, `1+`, `~`), soft highlight rows, and drag-and-drop file ingestion.
- Implemented [`DiffMiniMap`](file:///e:/java_compare_classes_project/src/main/java/com/javacompare/ui/DiffMiniMap.java) overview bar with color ticks and click-to-jump.
- Implemented [`ScrollSyncCoordinator`](file:///e:/java_compare_classes_project/src/main/java/com/javacompare/ui/ScrollSyncCoordinator.java) for mutual scroll synchronization.

### Phase 4: File I/O & Export Subsystem ✅ (Completed)
- Implemented [`FileLoader`](file:///e:/java_compare_classes_project/src/main/java/com/javacompare/util/FileLoader.java) with UTF-8 / UTF-16 / ISO-8859-1 charset and BOM detection.
- Implemented [`ReportExporter`](file:///e:/java_compare_classes_project/src/main/java/com/javacompare/util/ReportExporter.java) for standalone HTML diff reports and unified `.patch` files.

### Phase 5: Testing & Verification ✅ (Completed)
- Created 11 automated unit tests across `DiffServiceTest`, `WordDiffCalculatorTest`, and `FileLoaderTest`.
- 100% test pass rate.
