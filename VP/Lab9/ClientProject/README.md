# Movie Scout Client

This JavaFX project is the client side of Movie Scout. It reads movie data from the `vpl_lab` MariaDB database initialized by `DatabaseServerProject`.

Run the database server project first, then run the client main class:

```bash
javac --module-path lib/javafx --add-modules javafx.controls,javafx.fxml \
  -cp lib/mysql-connector-j.jar \
  -d ClientProject/out \
  $(find ClientProject/src/application -name '*.java')

java --module-path lib/javafx --add-modules javafx.controls,javafx.fxml \
  -cp "ClientProject/out:lib/mysql-connector-j.jar:ClientProject/src" \
  application.Main
```
