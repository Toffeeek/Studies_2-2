# Movie Scout Database Server

This separate Java project creates and initializes the `vpl_lab` database for the JavaFX client.

It connects to MariaDB at `127.0.0.1:3306` with user `root` and password `mysql`.

Run from the Lab9 root:

```bash
javac -cp "lib/mysql-connector-j.jar" -d DatabaseServerProject/out $(find DatabaseServerProject/src -name '*.java')
java -cp "DatabaseServerProject/out:lib/mysql-connector-j.jar" server.DatabaseServerApplication
```

After this succeeds, run the client project through `application.Main`.
