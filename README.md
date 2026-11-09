# localRepo - Expense Manager (Java + MySQL)

A console expense tracker written in Java. Expenses are stored in a local MySQL database.

## Features

- Add an expense (date format `dd/mm/yyyy`)
- View all expenses and the total
- View expenses by category
- Set a monthly budget and check budget status
- Delete an expense
- Coloured terminal menu

## Tech stack

Java, JDBC, MySQL (driver: `lib/mysql-connector-j-9.4.0.jar`, bundled).

## Getting started

Prerequisites: a JDK (compiling was tested with JDK 17) and a running MySQL server on `localhost:3306`.

1. Open `DBHelper.java` and set `USER` and `PASSWORD` to your own MySQL credentials. The values in the file are only the author's local development settings; use your own.
2. Compile and run (Windows; on Linux/macOS use `:` instead of `;` in the classpath):

```bash
javac -encoding UTF-8 -cp "lib/mysql-connector-j-9.4.0.jar" *.java
java -cp ".;lib/mysql-connector-j-9.4.0.jar" Main
```

The program creates the `expense_manager` database and its table on first run.

## Project structure

```
Main.java       menu and user interaction
DBHelper.java   MySQL connection, database/table creation
Expense2.java   expense data class
lib/            MySQL JDBC driver
```

## Roadmap / known limitations

- The database credentials are hardcoded in `DBHelper.java`; they should be moved to environment variables.
- No automated tests.
- Only `javac` compilation was verified for this README; running needs a live MySQL server.

## Author

Mrittunjay Dubey, GitHub [@JayD108](https://github.com/JayD108)

## License

No license file is included yet, so all rights are reserved by default.