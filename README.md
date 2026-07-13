# Yandex Disk API Automation Tests

Проект автоматизированного тестирования **REST API Яндекс.Диска** с использованием **Java, Rest Assured, TestNG, Allure** и других современных инструментов.

---

## Описание

Набор тестов проверяет ключевые сценарии работы с папками и файлами в Яндекс.Диске:

- Создание папки (синхронное, с валидацией ошибок)
- Удаление папки (синхронное и асинхронное, с/без помещения в корзину)
- Восстановление папки из корзины (синхронное и асинхронное)
- Загрузка файлов, копирование, скачивание
- Получение списка файлов с валидацией JSON‑схемы
- Проверка авторизации (валидный/невалидный токен)
- Обработка негативных сценариев (некорректные пути, отсутствие параметров, конфликты, блокировки)

Все тесты написаны с учётом параллельного выполнения (за счёт `thread-count="9"` в `testng.xml`), используют паттерны **Page Object** (через хелперы) и **DTO** для сериализации/десериализации ответов.

---

## Технологии

| Инструмент | Версия | Назначение |
|------------|--------|------------|
| Java       | 21     | Язык программирования |
| Maven      | 3.9+   | Сборка и управление зависимостями |
| Rest Assured | 6.0.0 | HTTP‑клиент для тестирования API |
| TestNG     | 7.12.0 | Фреймворк для написания и запуска тестов |
| Allure     | 2.34.0 | Генерация отчётов (с аннотациями `@Epic`, `@Feature`, `@Severity` и т.д.) |
| Owner      | 1.0.12 | Управление конфигурацией (`config.properties`) |
| Awaitility | 4.3.0  | Ожидание завершения асинхронных операций |
| Lombok     | 1.18.46 | Генерация getter/setter, конструкторов |
| Jackson    | 2.22.0 | Преобразование JSON ↔ Java‑объекты |
| JSON Schema Validator | 6.0.0 | Валидация ответов по схеме |

---

## Структура проекта

```
Yandex-Disk-API-Project/
├── src/
│   ├── test/
│   │   ├── java/
│   │   │   ├── dto/
│   │   │   │   ├── ErrorResponse.java          # Модель ошибок API
│   │   │   │   ├── OperationResponse.java      # Модель ответа для операций (href, method, templated)
│   │   │   │   └── User.java                   # DTO для пользователя (из скачанного файла)
│   │   │   ├── tests/
│   │   │   │   ├── BaseTest.java               # Базовый класс с настройкой RequestSpecification
│   │   │   │   ├── AuthOnDiskTest.java         # Тесты авторизации
│   │   │   │   ├── CreateFolderTest.java       # Тесты создания папки
│   │   │   │   ├── DeleteFolderTest.java       # Тесты синхронного удаления
│   │   │   │   ├── RestoreFolderTest.java      # Тесты синхронного восстановления
│   │   │   │   ├── AsyncDeleteFolderTest.java  # Тесты асинхронного удаления
│   │   │   │   ├── AsyncRestoreFolderTest.java # Тесты асинхронного восстановления
│   │   │   │   ├── UploadAndCopyFileTest.java  # Тесты загрузки и копирования файла
│   │   │   │   ├── DownloadFileTest.java       # Тест скачивания файла
│   │   │   │   └── GetListFilesTest.java       # Тест получения списка файлов (с проверкой схемы)
│   │   │   └── utils/
│   │   │       ├── Configuration.java          # Интерфейс для Owner (читает config.properties)
│   │   │       ├── FileHelper.java             # Утилиты для работы с файлами (копирование, парсинг)
│   │   │       └── FolderHelper.java           # Утилиты для работы с папками (создание, удаление, проверка, ожидание асинхронных операций)
│   │   └── resources/
│   │       ├── config.properties               # Конфигурация (токен, URL, имена папок)
│   │       └── schemas/
│   │           └── files-schema.json           # JSON‑схема для ответа /v1/disk/resources/files
├── testng.xml                                   # Конфигурация TestNG (параллельный запуск, listeners)
└── pom.xml                                      # Maven‑проект
```

---

## Настройка

### 1. Клонирование репозитория

```bash
git clone https://github.com/MielPops828/Yandex-Disk-API-Project
cd Yandex-Disk-API-Project
```

### 2. Настройка конфигурации

Отредактируйте файл `src/test/resources/config.properties`:

```properties
base.url=https://cloud-api.yandex.net
access.token=<ваш OAuth-токен Яндекс.Диска>
user.login=<ваш логин>
user.display.name=<ваше отображаемое имя>
create.folder.name=test
upload.folder.input.name=input_data
upload.folder.output.name=output_data
download.folder.name=sdet_data
```

- **access.token** – получите в [Яндекс.Диске → Политика доступа](https://yandex.ru/dev/disk/poligon/).
- Остальные параметры можно оставить по умолчанию или изменить под свои нужды.

### 3. Установка зависимостей

```bash
mvn clean install
```

---

## Запуск тестов

Все тесты запускаются через `testng.xml`, который содержит все классы и настроен на параллельный запуск (9 потоков).

```bash
mvn test
```

или, чтобы сразу сгенерировать Allure-отчёт:

```bash
mvn test allure:report
```

### Отдельные тесты

Можно запустить конкретный класс:

```bash
mvn test -Dtest=CreateFolderTest
```

или конкретный метод:

```bash
mvn test -Dtest=CreateFolderTest#testSuccessCreateFolderTest
```

---

## Генерация Allure-отчёта

После выполнения тестов результаты сохраняются в `target/allure-results`.

Чтобы открыть отчёт в браузере:

```bash
allure serve allure-results
```

Если Allure не установлен глобально, используйте Maven‑плагин:

```bash
mvn allure:serve
```

Отчёт содержит:

- Статистику по тестам (успешно/упало/сломано)
- Время выполнения
- Логи и вложения (скриншоты, если добавлены)
- Теги `@Epic`, `@Feature`, `@Severity`, `@Description` для навигации

---

## Структура тестов

### Базовый класс `BaseTest`

- Инициализирует `RequestSpecification` с базовым URL и `ContentType.JSON`.
- Загружает конфигурацию через Owner (`Configuration`).

### Хелперы (`FolderHelper`, `FileHelper`)

- Инкапсулируют часто используемые API-вызовы.
- `FolderHelper.waitForOperationCompletion()` – ожидание завершения асинхронных операций (использует Awaitility).
- `FolderHelper.uploadFileOnFolder()` – загрузка файла (получение URL для загрузки, затем PUT).

### DTO

- `ErrorResponse`, `OperationResponse` – используются для десериализации ответов.
- `User` – для проверки содержимого скачанного файла.

### JSON‑схема

- `files-schema.json` – валидация ответа `/v1/disk/resources/files` с помощью `matchesJsonSchemaInClasspath`.

---

## Примечания

- **Токен** должен иметь доступ к Яндекс.Диску. Храните его в `config.properties` и **не** коммитьте в публичный репозиторий (добавьте `config.properties` в `.gitignore` при необходимости).
- **Асинхронные тесты** используют `force_async=true` и ожидают статуса `success` через `waitForOperationCompletion`.
- **Параллельный запуск** требует, чтобы тесты не использовали общие данные (каждый тест создаёт папки с уникальным именем на основе `System.currentTimeMillis()`).
- **Allure** даёт удобную навигацию по тестам, группируя их по эпикам и фичам.
