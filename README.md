# foundit

Console-based lost-and-found system backed by MySQL.

## Requirements

- JDK 21+
- MySQL with the `foundit_db` schema loaded from `sql/db.sql`

## Layout

| Class | Responsibility |
|---|---|
| `Foundit` | entry point and main menu |
| `Database` | password prompt, connection, transaction rollback |
| `Console` | prompt/parse helpers shared by the menus |
| `ViewMenu` | listing each table |
| `InsertMenu` | adding rows (parent + child in one transaction) |
| `UpdateMenu` | editing rows |
| `DeleteMenu` | removing rows (children first) |
| `QueryMenu` | the canned report queries |

## Running

The app asks for the database password at startup; nothing is hardcoded. The
host, port and user live in `Foundit.java` as `DB_URL` / `DB_USER`.

```sh
javac -d target/classes \
  -cp src/main/java/com/mycompany/foundit/mysql-connector-j-26.10.0.jar \
  $(find src/main/java -name '*.java')

java -cp "target/classes:src/main/java/com/mycompany/foundit/mysql-connector-j-26.10.0.jar" \
  com.mycompany.foundit.Foundit
```

With Maven: `mvn exec:java` (see `exec.mainClass` in `pom.xml`).

## Menu

1. View records
2. Insert record
3. Update record
4. Delete record
5. Reports / queries
6. Exit
