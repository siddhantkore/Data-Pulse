# Data-Pulse


## <img src="src/main/resources/img.png" alt="Terminal Icon" width="40px" height="40px"> [docs](DOCS.md)


## Code Style and Quality

This project uses **Checkstyle** and **Spotless** to maintain consistent code quality and formatting.

### Checkstyle
Checkstyle validates code against the project's style rules.

**Run Checkstyle:**
```
mvn checkstyle:check
```

### Spotless
Spotless ensures formatting using Google Java Format (AOSP).

**Check formatting:**
```
mvn spotless:check
```

**Automatically format code:**
```
mvn spotless:apply
```

Both Checkstyle and Spotless also run automatically during:
```
mvn verify
```

Please make sure your code passes these checks before opening a pull request.
