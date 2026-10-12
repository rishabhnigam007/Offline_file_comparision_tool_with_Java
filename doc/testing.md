# Testing Strategy & Quality Assurance Plan

## 1. Overview
The test suite validates algorithmic accuracy of Myers diff calculation, intra-line word diffs, row alignment, filter toggles, charset detection, and BOM stripping.

---

## 2. Test Execution

### Run all tests via Maven:
```bash
mvn test
```

---

## 3. Test Suites & Scenarios

| Test Class | Scenario Tested | Status |
| :--- | :--- | :--- |
| **`WordDiffCalculatorTest`** | Detects single word changes (e.g. `hii` vs `hi`). | ✅ Passing |
| **`WordDiffCalculatorTest`** | Returns empty spans for identical strings. | ✅ Passing |
| **`WordDiffCalculatorTest`** | Respects `ignoreCase` option during token matching. | ✅ Passing |
| **`DiffServiceTest`** | Detects identical multiline texts with 0 diffs. | ✅ Passing |
| **`DiffServiceTest`** | Detects single line modification matching reference demo (`hii` vs `hi`). | ✅ Passing |
| **`DiffServiceTest`** | Aligns insertions with blank virtual spacers on original side. | ✅ Passing |
| **`DiffServiceTest`** | Aligns deletions with blank virtual spacers on modified side. | ✅ Passing |
| **`DiffServiceTest`** | Trims leading/trailing whitespace when `trimWhitespace` enabled. | ✅ Passing |
| **`DiffServiceTest`** | Folds unchanged lines when `hideUnchanged` enabled. | ✅ Passing |
| **`FileLoaderTest`** | Reads standard UTF-8 text file into string. | ✅ Passing |
| **`FileLoaderTest`** | Detects and strips UTF-8 BOM byte sequence without data corruption. | ✅ Passing |

---

## 4. Test Results Summary
- **Total Tests:** 11
- **Passed:** 11 (100%)
- **Failed:** 0
- **Execution Time:** ~250ms
