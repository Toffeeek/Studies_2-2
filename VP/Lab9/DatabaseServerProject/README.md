# Movie Scout Database Server

This separate Java project initializes the `vpl_lab` database for the JavaFX client.

Run from the Lab9 root:

```bash
javac -cp "lib/mysql-connector-j.jar" -d DatabaseServerProject/out $(find DatabaseServerProject/src -name '*.java')
java -cp "DatabaseServerProject/out:lib/mysql-connector-j.jar" server.DatabaseServerApplication
```

After this succeeds, run the client project through `application.Main`.
