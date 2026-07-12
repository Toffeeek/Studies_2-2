# JavaFX Login/Register with PostgreSQL JDBC

A JavaFX login/register desktop app that stores users in PostgreSQL through JDBC. The first screen is login, with a button that opens the signup screen. The FXML files are in `src/main/resources/com/example/auth`.

The app creates this table automatically when it can connect:

```sql
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL
);
```

## Database settings

Default values:

```text
DB_URL=jdbc:postgresql://localhost:5432/javafx_auth
DB_USER=postgres
DB_PASSWORD=postgres
```

Create the database before running:

```sql
CREATE DATABASE javafx_auth;
```

You can override the defaults with environment variables.

## Run

This machine has JavaFX jars in `/usr/share/java/java-openjfx`.
Those jars are JavaFX 25, so use JDK 25 with them. If you stay on JDK 21, install/download JavaFX 21 and point `JAVAFX_MODULE_PATH` to that SDK instead.
The PostgreSQL JDBC driver is stored in `lib/postgresql-42.7.13.jar`.

In IntelliJ, set the project SDK to `/usr/lib/jvm/java-25-openjdk`, add JavaFX and the PostgreSQL JDBC jar as project libraries, then run `com.example.auth.Main`.
This project intentionally does not use `module-info.java`, which avoids the common `module not found: javafx.controls` build error in lab setups.

Command-line example:

```bash
export JAVAFX_LIB=/usr/share/java/java-openjfx
export POSTGRES_JAR=lib/postgresql-42.7.13.jar
mkdir -p out
javac -cp "$JAVAFX_LIB/*:$POSTGRES_JAR" -d out $(find src/main/java -name '*.java')
cp -r src/main/resources/* out/
java --enable-native-access=javafx.graphics -Djava.library.path=/usr/lib/java-openjfx -Dprism.order=sw --module-path "$JAVAFX_LIB" --add-modules javafx.controls,javafx.fxml -cp "out:$POSTGRES_JAR" com.example.auth.Main
```
