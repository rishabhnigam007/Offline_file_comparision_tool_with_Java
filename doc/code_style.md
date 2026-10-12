# Java Code Style & Engineering Standards

## 1. Overview & Principles
This document outlines the coding standards, style guidelines, and design conventions for the **Offline Java Compare Tool**. It targets **Java 21 LTS** with modern language constructs, clean logging, and robust architecture.

---

## 2. General Java Guidelines

### 2.1. Language Version & Modern Features
- Target Java: **Java 21 LTS**.
- Modern Java capabilities:
  - **Records** for immutable data transfer models ([`DiffOptions`](file:///e:/java_compare_classes_project/src/main/java/com/javacompare/model/DiffOptions.java), [`DiffRow`](file:///e:/java_compare_classes_project/src/main/java/com/javacompare/model/DiffRow.java), [`DiffResult`](file:///e:/java_compare_classes_project/src/main/java/com/javacompare/model/DiffResult.java), [`TextSpan`](file:///e:/java_compare_classes_project/src/main/java/com/javacompare/model/TextSpan.java)).
  - **Pattern Matching for `switch` and `instanceof`**.
  - **Text Blocks (`"""`)** for templates and multiline constants.
  - **`var`** keyword: Use for local variables when the assigned type is explicitly clear.

### 2.2. Source File Formatting
- **Encoding:** UTF-8 exclusively.
- **Indentation:** 4 spaces (no tabs).
- **Line Length:** Maximum 120 characters per line.
- **Braces:** Standard K&R style.

---

## 3. Naming Conventions

| Identifier | Convention | Example |
| :--- | :--- | :--- |
| **Packages** | Lowercase, dot-separated | `com.javacompare.diff` |
| **Classes / Records / Enums** | `PascalCase` | `DiffService`, `DiffResult`, `DiffType` |
| **Methods** | `camelCase` | `compare()`, `computeWordDiff()` |
| **Variables / Fields** | `camelCase` | `originalText`, `alignedRows` |
| **Constants** | `UPPER_SNAKE_CASE` | `LIGHT_BG`, `COLOR_INSERT` |

---

## 4. Architecture & UI Conventions

- **Pure Java & Maven:** Standalone project buildable via standard `mvn clean package` and runnable via `java -jar`.
- **UI Event Dispatch Thread (EDT):** All GUI modifications must occur on Swing's EDT via `SwingUtilities.invokeLater()`.
- **High-Contrast Theming:** Adhere to pure White and pure Black themes without muddy intermediate grays.

---

## 5. Logging with SLF4J

- Every core service, utility, and UI controller must instantiate an SLF4J logger:
  ```java
  private static final Logger log = LoggerFactory.getLogger(DiffService.class);
  ```
- Use parameterized logging (`log.info("Diff computed in {}ms", duration)`) instead of string concatenation.
- Never use `System.out.println()` or print stack traces directly to stdout in production code.

---

## 6. Testing Conventions
- Use **JUnit 5** and **AssertJ** for expressive unit testing.
- Test class names must end with `Test` (e.g. `DiffServiceTest`, `WordDiffCalculatorTest`).
