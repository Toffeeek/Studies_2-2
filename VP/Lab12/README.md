# Lab 12 Stories App

JavaFX multithreading lab project based on the Lab 11 Stories assignment.

The project keeps the Lab 9 database format:

- Database: `vpl_lab`
- User: `root`
- Password: `mysql`
- JDBC URL: `jdbc:mysql://localhost:3306/vpl_lab?useSSL=false&allowPublicKeyRetrieval=true`

Run the database setup first:

```bash
javac -cp "lib/mysql-connector-j.jar" -d DatabaseServerProject/out $(find DatabaseServerProject/src -name '*.java')
java -cp "DatabaseServerProject/out:lib/mysql-connector-j.jar" server.DatabaseServerApplication
```

Then run the JavaFX client:

```bash
javac -cp "lib/mysql-connector-j.jar:lib/javafx/*" -d out/production/Lab12 $(find src -name '*.java')
mkdir -p out/production/Lab12/resources
cp src/resources/* out/production/Lab12/resources/
java --module-path lib/javafx --add-modules javafx.controls,javafx.fxml -cp "out/production/Lab12:lib/mysql-connector-j.jar" application.Main
```

In IntelliJ IDEA, use the included `DatabaseServer` run configuration first, then `Main`.
