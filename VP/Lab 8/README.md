# Student Management System with MySQL JDBC

Console-based Java student management app using JDBC and MySQL.

Main classes:

- `database_connection.DatabaseConnection`
- `database_connection.Student`
- `database_connection.StudentDAO`
- `database_connection.Main`

The app creates the `students` table automatically when it can connect:

```sql
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    department VARCHAR(120) NOT NULL,
    cgpa DOUBLE NOT NULL
);
```

## Database settings

Default values:

```text
DB_URL=jdbc:mysql://localhost:3306/vpl_lab
DB_USER=root
DB_PASSWORD=mysql
```

Create the database before running:

```sql
CREATE DATABASE vpl_lab;
```

You can override the defaults with environment variables.

## Arch Linux MySQL/MariaDB setup

This machine is Arch Linux. MariaDB is the MySQL-compatible server available from the official package repository.

```bash
sudo pacman -S --needed mariadb mariadb-clients
sudo mariadb-install-db --user=mysql --basedir=/usr --datadir=/var/lib/mysql
sudo systemctl enable --now mariadb
sudo mariadb -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'mysql'; CREATE DATABASE IF NOT EXISTS vpl_lab;"
```

If MariaDB was already initialized before, skip the `mariadb-install-db` command.

## Run

The MySQL Connector/J jar should be stored in `lib/mysql-connector-j.jar`.

```bash
mkdir -p build/classes
javac -cp lib/mysql-connector-j.jar -d build/classes $(find src/main/java -name '*.java')
java -cp "build/classes:lib/mysql-connector-j.jar" database_connection.Main
```
