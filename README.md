# System do zarządzania procesami życia domowego — moduł uwierzytelniania

## Opis
Jest to prototypowy moduł finalnej aplikacji, który umożliwia:
- logowanie użytkowników,
- rejestrację nowych użytkowników,
- rejestrowanie się za pomocą kont zewnętrznych (Google),
- wylogowanie się,
- łączenie kont zewnętrznych z kontem lokalnym,
- uwierzytelnianie użytkowników przy użyciu tokenów JWT,
- odświeżanie tokenów JWT,

## Instalacja (wymaga JAVA 21 i PostgreSQL 17)
### Konfiguracja bazy danych
1. Utwórz bazę danych PostgreSQL o dowolnej nazwie (np. `auth_tutorial`).
2. Utwórz użytkownika w bazie o odpowiednich uprawnieniach do tej bazy.
3. W pliku `example.env.txt` ustaw zmienne środowiskowe dla połączenia z bazą danych:
   - `DB_URL`: URL do bazy danych (np. `jdbc:postgresql://localhost:5432/auth_tutorial`)
   - `DB_USERNAME`: nazwa użytkownika bazy danych
   - `DB_PASSWORD`: hasło użytkownika bazy danych

### Konfiguracja aplikacji
1. Edytuj plik example.env.txt, aby ustawić odpowiednie wartości dla pozostałych zmiennych środowiskowych:
    - `JWT_SECRET_KEY`: klucz do podpisywania tokenów JWT
    - `GOOGLE_CLIENT_ID`: identyfikator klienta Google (uzyskany z konsoli Google Cloud)
    - `GOOGLE_CLIENT_SECRET`: sekret klienta Google (uzyskany z konsoli Google Cloud)
2. Zmień nazwę pliku example.env.txt na .env (np. za pomocą: `mv example.env.txt .env`).
3. Zmienne środowiskowe będą automatycznie wczytane przez Gradle podczas uruchamiania aplikacji.
4. Aby uruchomić aplikację bez jej budowania użyj polecenia: `./gradlew bootRun`.

#### Opcjonalnie
5. Zbuduj aplikację za pomocą Gradle: `./gradlew build`.
6. Uruchom aplikację: `java -jar build/libs/spring-auth-tutorial-0.0.1-SNAPSHOT.jar`.
