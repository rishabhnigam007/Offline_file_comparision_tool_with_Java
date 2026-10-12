# Security, Privacy & Data Protection Policy

## 1. Privacy Principles
The **Offline Java Compare Tool** is built with an **Offline-First & Zero-Trust Privacy** model.
- **Zero Outbound Connections:** The application makes 0 network requests, contains no telemetry, and transmits no analytics.
- **Local In-Memory Execution:** All Myers diff calculations, word comparisons, and rendering occur purely inside local JVM memory.

---

## 2. File Access & Input Safety

### 2.1. Read-Only File Handling
- Input files are opened exclusively in read mode (`StandardOpenOption.READ`).
- The application never modifies or locks user source files.

### 2.2. HTML Export Sanitization
- Standalone HTML reports escape all HTML entities (`<`, `>`, `&`, `"`, `'`) to prevent Cross-Site Scripting (XSS) when exported reports are viewed in corporate web browsers.

### 2.3. Memory Protection
- Streaming file readers prevent excessive memory consumption on large files.
