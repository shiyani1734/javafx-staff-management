# JavaFX Staff Management

A JavaFX staff management module integrated into a university question-and-answer application. It provides a graphical interface for managing staff accounts stored in an H2 database.

Developed for CSE 360 coursework using an existing application and shared database infrastructure.

## Features

- Display staff usernames, email addresses, and roles in a table.
- Add staff accounts.
- Check for existing usernames before adding accounts.
- Delete selected staff accounts.
- Refresh the displayed staff list.
- Store account data in a local H2 database.
- Test staff account creation and deletion with JUnit 5.

## Technologies

Java, JavaFX 23.0.2, H2 Database, JDBC, JUnit 5, Eclipse

## Getting Started

1. Import the repository into Eclipse as an existing Java project.
2. Configure a JDK compatible with JavaFX 23.0.2.
3. Add the JavaFX SDK and H2 JDBC driver to the project.
4. Ensure JUnit 5 is available for running tests.
5. Update Eclipse library paths for your local installation.
6. Run `application.StartCSE360` as a JavaFX application.

Depending on your IDE configuration, JavaFX may require these VM arguments:

```text
--module-path "/path/to/javafx-sdk/lib" --add-modules javafx.controls
```

Replace the SDK path with your local JavaFX installation.

The application creates its database tables when it connects. Complete the initial account setup, then use an account with the staff role to access the staff management page.

## Main Components

- `StaffHomePage.java` — staff management interface
- `StaffManager.java` — staff account operations
- `DatabaseHelper.java` — H2 database access
- `User.java` — account model
- `StartCSE360.java` — application entry point
- `StaffTest.java` — staff creation and deletion test

## Database

The application uses:

```text
jdbc:h2:~/FoundationDatabase
```

Data is stored locally under the current user's home directory.

## Testing

Run `application.StaffTest` as a JUnit test in Eclipse.

The test creates and deletes a test staff account using the configured database.
