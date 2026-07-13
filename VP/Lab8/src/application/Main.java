package application;

import javafx.application.Application;

// mariadb -uroot -pmysql -e "USE vpl_lab; SHOW TABLES; SELECT * FROM users;"

public class Main {
    public static void main(String[] args) {
        Application.launch(AuthApplication.class, args);
    }
}
