# Wielowątkowa Aplikacja Klient–Serwer TCP/IP

## Opis projektu

Projekt przedstawia implementację wielowątkowej aplikacji klient–serwer wykorzystującej protokół TCP/IP. System umożliwia komunikację pomiędzy klientem a serwerem, przesyłanie obiektów Java przy użyciu strumieni obiektowych oraz zarządzanie wieloma połączeniami jednocześnie.

Projekt został zakończony pomyślnie i zweryfikowany za pomocą testów jednostkowych, integracyjnych oraz testów End-to-End.

## Funkcjonalności

* Komunikacja klient–serwer przez TCP/IP.
* Obsługa wielu klientów jednocześnie z wykorzystaniem `ExecutorService`.
* Bezpieczne zarządzanie współbieżnym dostępem do danych przy użyciu `ConcurrentHashMap`.
* Ograniczenie liczby aktywnych połączeń klientów.
* Przesyłanie i odbiór serializowanych obiektów Java.
* Wykorzystanie Stream API do przetwarzania kolekcji.
* Obsługa wyjątków `ClassCastException`.
* Kompleksowe testy JUnit 5.

---

## Struktura projektu

```text
src/
├── main/
│   ├── java/
│   │   ├── client/
│   │   │   └── AppClient.java
│   │   ├── server/
│   │   │   └── AppServer.java
│   │   └── model/
│   │       ├── Kot.java
│   │       ├── Pies.java
│   │       └── Samochod.java
│
└── test/
    └── java/
        └── ProjectApplicationTest.java
```

## Technologie

* Java 17+
* Apache Maven
* JUnit 5
* TCP/IP Sockets
* ExecutorService
* ConcurrentHashMap
* AtomicInteger
* Stream API

---

## Wymagania

Przed uruchomieniem projektu należy zainstalować:

* Java JDK 17 lub nowsza
* Apache Maven
* IntelliJ IDEA (zalecane)

Sprawdzenie wersji:

```bash
java -version
mvn -version
```

---

## Budowanie projektu

Pobranie zależności oraz kompilacja:

```bash
mvn clean compile
```

---

## Uruchomienie aplikacji

### 1. Uruchomienie serwera

W pierwszej kolejności należy uruchomić serwer:

```bash
mvn exec:java -Dexec.mainClass="server.AppServer"
```

Po uruchomieniu serwer:

* załaduje dane testowe,
* uruchomi repozytorium obiektów,
* rozpocznie nasłuchiwanie na porcie `12129`.

### 2. Uruchomienie klienta

Po uruchomieniu serwera należy uruchomić klienta:

```bash
mvn exec:java -Dexec.mainClass="client.AppClient"
```

Klient:

1. nawiązuje połączenie z serwerem,
2. wykonuje proces autoryzacji (handshake),
3. wysyła zapytania o kolekcje obiektów,
4. odbiera dane i przetwarza je przy użyciu Stream API.

---

## Uruchamianie testów

Wykonanie wszystkich testów:

```bash
mvn test
```

Zakres testów:

### Testy jednostkowe

* poprawność modeli danych,
* działanie metod `equals()` i `hashCode()`,
* poprawność stanu pamięci serwera.

### Testy integracyjne

* zapis i odczyt obiektów przy użyciu:

    * `ByteArrayOutputStream`
    * `ByteArrayInputStream`

### Testy End-to-End (E2E)

* uruchomienie serwera w tle,
* obsługa trzech poprawnych klientów,
* odrzucenie czwartego klienta (`REFUSED`),
* weryfikacja przechwycenia `ClassCastException`.

---

## Modele danych

Projekt zawiera trzy serializowalne klasy domenowe:

### Kot

```java
String imie;
int wiek;
```

### Pies

```java
String rasa;
double waga;
```

### Samochod

```java
String marka;
int rokProdukcji;
```

Każda klasa:

* implementuje `Serializable`,
* posiada `serialVersionUID`,
* nadpisuje:

    * `toString()`
    * `equals()`
    * `hashCode()`

---

## Architektura systemu

### Serwer

Odpowiada za:

* zarządzanie połączeniami klientów,
* obsługę współbieżności,
* przechowywanie danych,
* kontrolę limitu połączeń.

Wykorzystane mechanizmy:

```java
ExecutorService
ConcurrentHashMap
AtomicInteger
```

### Klient

Odpowiada za:

* komunikację TCP/IP,
* wysyłanie zapytań,
* odbiór kolekcji obiektów,
* przetwarzanie danych przy użyciu Stream API.

---

## Autorzy projektu

| Rola   | Odpowiedzialność                      |
| ------ | ------------------------------------- |
| Albert | Modele danych i konfiguracja Maven    |
| Michał | Implementacja serwera i współbieżność |
| Adam   | Implementacja klienta i Stream API    |
| Wiktor | Testy JUnit 5 oraz dokumentacja       |

---

## Status projektu

✅ Projekt ukończony

✅ Wszystkie testy zakończone powodzeniem

✅ Zweryfikowana komunikacja klient–serwer

✅ Poprawna obsługa wielowątkowości

---

## Informacja o wykorzystaniu AI

Podczas realizacji projektu wykorzystywano wsparcie narzędzi Generative AI w zakresie:

* generowania modeli danych,
* projektowania mechanizmów współbieżności,
* implementacji klienta TCP/IP,
* przygotowania testów JUnit 5.

AI pełniło funkcję wspomagającą proces projektowania i implementacji, natomiast końcowa weryfikacja poprawności rozwiązania została przeprowadzona przez członków zespołu.
