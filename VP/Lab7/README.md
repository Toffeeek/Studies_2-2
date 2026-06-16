# JavaFX Wordle

A JavaFX word-guessing game in the style of Wordle. The UI is defined with FXML and styled with an external CSS file in `src/main/resources/com/example/wordle`.

## Run

This machine has JavaFX jars in `/usr/lib/jvm/java-21-openjdk/lib`.

```bash
export JAVAFX_MODULE_PATH=/usr/lib/jvm/java-21-openjdk/lib/javafx.base.jar:/usr/lib/jvm/java-21-openjdk/lib/javafx.graphics.jar:/usr/lib/jvm/java-21-openjdk/lib/javafx.controls.jar:/usr/lib/jvm/java-21-openjdk/lib/javafx.fxml.jar
mkdir -p out
javac --module-path "$JAVAFX_MODULE_PATH" --add-modules javafx.controls,javafx.fxml -d out $(find src/main/java -name '*.java')
cp -r src/main/resources/* out/
java --module-path "$JAVAFX_MODULE_PATH:out" --module com.example.wordle/com.example.wordle.Main
```
