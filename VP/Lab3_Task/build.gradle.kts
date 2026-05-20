plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // your dependencies here
}

javafx {
    version = "21.0.4"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("org.example.Main")
}