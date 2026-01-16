# TranscribeAssistant - Setup Guide

## Prerequisites

### Java Development Kit (JDK)
This project requires **Java 17** (specified in `.java-version` file).

#### Recommended: Using jenv (Java Version Manager)

This project includes a `.java-version` file that automatically sets the correct Java version when using [jenv](https://www.jenv.be/).

**Install jenv:**
- **macOS:** `brew install jenv`
- **Linux:** See [jenv installation guide](https://www.jenv.be/)
- **Windows:** Use [jabba](https://github.com/shyiko/jabba) or manually set JAVA_HOME

**Add Java 17 to jenv:**
```bash
# Find your Java installations
/usr/libexec/java_home -V  # macOS
# or
ls /Library/Java/JavaVirtualMachines/  # macOS

# Add Java 17 to jenv
jenv add /Library/Java/JavaVirtualMachines/[your-jdk-17-folder]/Contents/Home

# jenv will automatically use Java 17 when in this project directory
```

#### Alternative: Manual Java Setup

If not using jenv, ensure Java 17 is installed and verify:
```bash
java -version
```

You should see Java 17.x.x.

## Building the Project

### Verify Gradle Setup
```bash
./gradlew --version
```

### Build the Project
```bash
./gradlew build
```

### Run on Android Device/Emulator
```bash
./gradlew installDebug
```

## Configuration Files

- **`.java-version`** - Specifies Java 17 for jenv/version managers (committed to git)
- **`gradle.properties`** - Shared project settings (committed to git)
- **`keystore.properties`** - Release signing configuration (gitignored, required for release builds)

## Troubleshooting

### "Java home supplied is invalid" Error
If you see this error, ensure:
1. You're using Java 17 (check with `java -version`)
2. If using jenv, ensure it's properly configured and Java 17 is added
3. Your `JAVA_HOME` environment variable points to a valid JDK 17 installation

### Checking Your Java Home
**macOS/Linux:**
```bash
echo $JAVA_HOME
```

**Windows (PowerShell):**
```powershell
echo $env:JAVA_HOME
```

**Windows (Command Prompt):**
```cmd
echo %JAVA_HOME%
```
