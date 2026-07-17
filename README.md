# Libris Library Management System

Libris is a JavaFX desktop application backed by MySQL. It provides separate librarian and member interfaces for catalog management, accounts, borrowing and returns, holds, fines, audit history, reports, settings, and database backups.

## Requirements

- Java Development Kit (JDK) 21
- JavaFX SDK 21.0.11
- MySQL Community Server 8
- Git

The MySQL Connector/J driver is already included at `src/mysql-connector-j-9.7.0.jar`. JavaFX is not committed to Git, so each developer must download it separately.

## 1. Clone the project

```bash
git clone https://github.com/chasemcclenning/CSC-4350-Project-Group-7.git
cd CSC-4350-Project-Group-7
git switch ui-dev
```

## 2. Install JavaFX

Download JavaFX SDK 21.0.11 from [Gluon](https://gluonhq.com/products/javafx/) for your operating system.

Extract it into the repository root so this path exists:

```text
javafx-sdk-21.0.11/lib
```

## 3. Create the MySQL database

Start MySQL, then import the included database dump from the repository root:

```bash
mysql -u root -p < dump-librarydb-202606271316.sql
```

Enter the password used with your local MySQL installation. The dump creates the `librarydb` database, tables, relationships, triggers, and initial records.

If the `mysql` command is not on your PATH on macOS, use:

```bash
/usr/local/mysql/bin/mysql -u root -p < dump-librarydb-202606271316.sql
```

## 4. Configure database access

The first time the app cannot connect, it displays a one-time MySQL setup dialog. Enter:

- URL: `jdbc:mysql://localhost:3306/librarydb`
- Username: your local MySQL username, commonly `root`
- Password: your local MySQL password

The app saves these settings in `config/database.properties`. This file is ignored by Git, so the database password is not committed.

You can also create it manually by copying `config/database.properties.example`:

```properties
url=jdbc:mysql://localhost:3306/librarydb
user=root
password=your_mysql_password
```

Environment variables can be used instead:

```bash
export LIBRARY_DB_URL="jdbc:mysql://localhost:3306/librarydb"
export LIBRARY_DB_USER="root"
export LIBRARY_DB_PASSWORD="your_mysql_password"
```

## 5. Compile the application

From the repository root on macOS or Linux:

```bash
mkdir -p bin/app

javac \
  --module-path javafx-sdk-21.0.11/lib \
  --add-modules javafx.controls,javafx.fxml \
  -cp src/mysql-connector-j-9.7.0.jar \
  -d bin/app \
  src/App.java \
  $(find src/database src/model src/session src/ui -name '*.java')
```

The command intentionally compiles the JavaFX application packages and not the older console demonstration files in `src`.

## 6. Run the application

On macOS or Linux:

```bash
java \
  --module-path javafx-sdk-21.0.11/lib \
  --add-modules javafx.controls,javafx.fxml \
  -cp "bin/app:src:src/mysql-connector-j-9.7.0.jar" \
  App
```

On Windows, use semicolons in the classpath:

```powershell
java --module-path javafx-sdk-21.0.11/lib --add-modules javafx.controls,javafx.fxml -cp "bin/app;src;src/mysql-connector-j-9.7.0.jar" App
```

## Sample accounts

The included database dump creates these project accounts:

| Role | Name | Email | Password |
|---|---|---|---|
| Librarian | Librarian Sample | `librarian@example.com` | `libexample1` |
| Member | Member Sample | `member@example.com` | `memexample1` |

Librarians open the librarian dashboard. Members open the member portal. New librarian and member accounts can be created from the librarian **Accounts** page by choosing the appropriate account type.

## Main workflows

- **Books:** add or edit titles and the number of physical copies. Titles with borrowing history are protected from deletion so circulation records remain valid.
- **Accounts:** create, view, edit, or delete member and librarian accounts.
- **Borrow & Return:** check out an available title to a member and return books from the currently borrowed list.
- **Holds and fines:** manage records stored in MySQL.
- **Profiles:** open book and user profiles from dashboard search, dashboard activity, Books, or Accounts.
- **Audit Log:** displays real application actions and the user responsible for them.
- **Reports:** generates summaries from current database records and can export CSV files.
- **Backup:** Settings → Backup → Back up now creates a `.sql` file containing tables, records, and triggers.

## Database sharing

By default, every developer has a separate local MySQL database. Git shares the application code and database dump, but it does not synchronize changes made inside each developer's local database.

To share live data, the team must host one MySQL database that every computer can reach and configure `LIBRARY_DB_URL`, `LIBRARY_DB_USER`, and `LIBRARY_DB_PASSWORD` for that server.

## Troubleshooting

### Cannot connect to the library database

- Confirm that MySQL is running.
- Confirm that `librarydb` exists with `SHOW DATABASES;`.
- Check `config/database.properties` or the three database environment variables.
- Verify the credentials with `mysql -u root -p`.

### JavaFX modules are missing

Confirm that `javafx-sdk-21.0.11/lib` exists in the project root and contains files such as `javafx.controls.jar` and `javafx.fxml.jar`.

### Main class cannot be found

Run the compile command first, remain in the repository root, and keep `bin/app`, `src`, and the MySQL connector in the runtime classpath.

### A book cannot be deleted

Books referenced by checkout history cannot be deleted because removing them would invalidate borrowing and audit records. Titles without checkout history can be deleted with their unreferenced copies.

## Security note

This is an academic project. Account passwords currently use the values stored in the project database. A production system should hash passwords and use restricted database accounts rather than a MySQL root account.
