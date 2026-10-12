# AI Agent Guidelines & Engineering Handbook

## 1. Project Vision
- **Product:** Offline Java Compare Tool.
- **Target Platform:** Java 21 LTS + Apache Maven.
- **UI Framework:** Java Swing + FlatLaf 3.5.4 (Pure White & Pure Black themes).
- **Core Engine:** `io.github.java-diff-utils:java-diff-utils`.
- **Logging:** SLF4J + Logback.

---

## 2. Mandatory Rules for Agents
1. **Clean Architecture:** Keep core Myers diff calculations (`com.javacompare.diff`) decoupled from UI presentation (`com.javacompare.ui`).
2. **Pure Java 21 & Maven:** Do not introduce external scripting dependencies; all builds and runs must work via standard `mvn` and `java -jar`.
3. **High-Contrast Theming:** Adhere strictly to pure White and pure Black themes without intermediate muddy gray backgrounds.
4. **EDT Safety:** Always update Swing components on the Event Dispatch Thread (`SwingUtilities.invokeLater`).
5. **Proper Logging:** Use `private static final Logger log = LoggerFactory.getLogger(...)` instead of `System.out.println`.

---

## 3. Standard Maven Commands

```bash
# Compile and run
mvn clean compile exec:java

# Execute all tests
mvn test

# Build standalone executable Fat-JAR
mvn clean package
java -jar target/java-diff-compare-1.0.0.jar
```
