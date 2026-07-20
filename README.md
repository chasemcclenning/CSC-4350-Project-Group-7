# Libris Library Management System

Libris is a JavaFX desktop application for managing a library catalog, accounts, borrowing and returns, holds, fines, audit history, reports, settings, and database backups.

The app uses an embedded local database. Its initial books, accounts, checkouts, holds, and audit records are bundled with the application, so teammates do not need to install MySQL or configure database credentials.

## Easiest way to run Libris

Download the installer for your computer from the GitHub Actions artifacts:

- `Libris-macOS` contains the macOS `.dmg` installer.
- `Libris-Windows` contains the Windows `.exe` installer.

Install and open Libris normally. The installer already contains Java, JavaFX, the database engine, and the project data.

These academic installers are not signed with paid Apple or Microsoft developer certificates. If macOS displays an unidentified-developer warning, Control-click Libris, choose **Open**, and confirm **Open**. If Windows SmartScreen appears, choose **More info** and then **Run anyway** only after confirming the installer came from this project repository.

On first launch, Libris creates a writable copy of the bundled library data in the current user's home folder:

- macOS and Linux: `~/.libris/librarydb.mv.db`
- Windows: `%USERPROFILE%\.libris\librarydb.mv.db`

Changes made in the app remain available on that computer after Libris closes. Each installation has its own independent copy, so changes made by different teammates do not synchronize.

## Sample accounts

| Role | Name | Email | Password |
|---|---|---|---|
| Librarian | Librarian Sample | `librarian@example.com` | `libexample1` |
| Member | Member Sample | `member@example.com` | `memexample1` |

Librarians open the librarian portal. Members open the member portal. A librarian can create additional member or librarian accounts from the **Members** page by selecting the appropriate account type.

## Run from source

### Requirements

- JDK 21
- Git

Maven does not need to be installed separately because the Maven Wrapper is included in the repository.

### Clone and run

On macOS or Linux:

```bash
git clone https://github.com/chasemcclenning/CSC-4350-Project-Group-7.git
cd CSC-4350-Project-Group-7
git switch ui-dev
./mvnw javafx:run
```

On Windows PowerShell:

```powershell
git clone https://github.com/chasemcclenning/CSC-4350-Project-Group-7.git
cd CSC-4350-Project-Group-7
git switch ui-dev
.\mvnw.cmd javafx:run
```

The first source build requires internet access so Maven can download JavaFX, H2, and the build plugins. Later builds reuse the local Maven cache.

## Build and test

On macOS or Linux:

```bash
./mvnw clean package
```

On Windows:

```powershell
.\mvnw.cmd clean package
```

The integration tests create a temporary database, verify the bundled data and sample credentials, perform a checkout and return, and create a database backup.

## Build native installers

Build the macOS `.dmg` on a Mac with JDK 21:

```bash
./packaging/package-macos.sh
```

Build the Windows `.exe` from PowerShell on Windows with JDK 21 and WiX Toolset:

```powershell
.\packaging\package-windows.ps1
```

Installers are written to `target/installer`. Native installers must be built on their target operating system, so a Mac builds the `.dmg` and Windows builds the `.exe`.

The GitHub Actions workflow **Build native installers** builds both automatically. Open the repository's **Actions** tab, select that workflow, choose **Run workflow**, and download the `Libris-macOS` and `Libris-Windows` artifacts after both jobs finish.

## Bundled data and resets

`src/database/seed-h2.sql` is the snapshot bundled into every build. It represents the existing project data at the time the installer was created. The original MySQL dump remains in the repository as a historical/export reference; teammates do not need it to run Libris.

To discard changes on one computer and restore the bundled snapshot:

1. Close Libris.
2. Delete `librarydb.mv.db` from the `.libris` folder in that user's home directory.
3. Open Libris again.

Libris will create a fresh local database from the bundled snapshot. This permanently removes the local changes on that computer, so create a backup first if the data is needed.

## Main workflows

- **Books:** add or edit titles and physical-copy counts. Titles with borrowing history are protected from deletion so circulation records remain valid.
- **Members:** create, view, edit, or remove member and librarian accounts.
- **Borrow & Return:** check out an available title to a member and return books from the currently borrowed section.
- **Holds and fines:** manage holds and fine records in the local database.
- **Profiles:** open book and user profiles from dashboard search, activity, Books, or Members.
- **Audit Log:** view application actions and the user responsible for each one.
- **Reports:** generate summaries from current local records and export CSV files.
- **Backup:** use **Settings → Backup → Back up now** to create an SQL backup of the local database.

## Troubleshooting

### The app does not open from source

Confirm that JDK 21 is active with `java -version`, run the command from the repository root, and try `./mvnw clean package` before `./mvnw javafx:run`.

### Maven cannot download dependencies

Confirm that the computer has internet access during the first build. If a build was interrupted, delete the `target` folder and run the build again.

### Local library data cannot be opened

Close other copies of Libris that may be using the same local database. Confirm that the current user can write to the `.libris` folder in their home directory.

### A book cannot be deleted

Books referenced by checkout history cannot be deleted because removing them would invalidate circulation and audit records. Titles without checkout history can be deleted with their unreferenced copies.

## Security note

This is an academic project. Account passwords currently use the values stored in the project database. A production system should hash passwords and protect local application data with operating-system permissions.
