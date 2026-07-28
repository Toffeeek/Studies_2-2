# Stories Database Server

This helper initializes the `vpl_lab` database used by the JavaFX Stories client.

Run from the Lab12 root:

```bash
javac -cp "lib/mysql-connector-j.jar" -d DatabaseServerProject/out $(find DatabaseServerProject/src -name '*.java')
java -cp "DatabaseServerProject/out:lib/mysql-connector-j.jar" server.DatabaseServerApplication
```

After the setup succeeds, run the client through `application.Main`.
