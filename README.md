# Credit Advisor

Консольний застосунок, що формує набір пропозицій цільових кредитів різних
банків і допомагає клієнту обрати оптимальну — з урахуванням можливості
дострокового погашення та/або збільшення кредитної лінії.

## Технології

- Java 17
- Maven (Maven Wrapper, окремого встановлення Maven не потрібно)
- JUnit 5, Mockito — unit-тести

## Структура пакетів

- `com.creditadvisor.model` — доменні класи: `Bank`, `Client`,
  `CreditPurpose`, `CreditSearchCriteria`, ієрархія `CreditOffer`.
- `com.creditadvisor.model.feature` — інтерфейси додаткових
  можливостей пропозиції (дострокове погашення, збільшення кредитної лінії).
- `com.creditadvisor.io` — читання/парсинг файлу з даними пропозицій.
- `com.creditadvisor.repository` — доступ до колекції пропозицій.
- `com.creditadvisor.service` — пошук, фільтрація та вибір
  оптимальної пропозиції.
- `com.creditadvisor.console` — мінімальний консольний ввід/вивід.

## Збірка та запуск

```bash
./mvnw test      # запустити unit-тести
./mvnw package    # зібрати jar
./mvnw exec:java  # запустити консольний застосунок
```

На Windows замість `./mvnw` використовуйте `mvnw.cmd`.
