# Libris Library Management System

Libris is a JavaFX desktop application backed by MySQL. It provides separate librarian and member interfaces for catalog management, accounts, borrowing and returns, holds, fines, audit history, reports, settings, and database backups.

## Requirements

- Java Development Kit (JDK) 21
- MySQL Community Server 8
- Git

Maven manages JavaFX and MySQL Connector/J automatically. The included Maven Wrapper lets developers build the project without installing Maven globally.

## 1. Clone the project

```bash
git clone https://github.com/chasemcclenning/CSC-4350-Project-Group-7.git
cd CSC-4350-Project-Group-7
git switch ui-dev
```

## 2. Create the MySQL database

Start MySQL, then import the included database dump from the repository root:

```bash
mysql -u root -p < dump-librarydb-202606271316.sql
```

Enter the password used with your local MySQL installation. The dump creates the `librarydb` database, tables, relationships, triggers, and initial records.

If the `mysql` command is not on your PATH on macOS, use:

```bash
/usr/local/mysql/bin/mysql -u root -p < dump-librarydb-202606271316.sql
```

## 3. Configure database access

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

## 4. Build the application

From the repository root on macOS or Linux:

```bash
./mvnw clean package
```

On Windows:

```powershell
mvnw.cmd clean package
```

The first build downloads Maven, JavaFX, MySQL Connector/J, and the required build plugins. Later builds reuse the local Maven cache. The Maven compiler configuration intentionally excludes the older console demonstration files in `src`.

## 5. Run the application

On macOS or Linux:

```bash
./mvnw javafx:run
```

On Windows:

```powershell
mvnw.cmd javafx:run
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

### Maven cannot download dependencies

Confirm that the computer has internet access for the first build and that JDK 21 is active with `java -version`. Delete `target` and retry `./mvnw clean package` if a previous build was interrupted.

### Main class cannot be found

Run `./mvnw clean package` first and remain in the repository root when using `./mvnw javafx:run`.

### A book cannot be deleted

Books referenced by checkout history cannot be deleted because removing them would invalidate borrowing and audit records. Titles without checkout history can be deleted with their unreferenced copies.

## Security note

This is an academic project. Account passwords currently use the values stored in the project database. A production system should hash passwords and use restricted database accounts rather than a MySQL root account.
