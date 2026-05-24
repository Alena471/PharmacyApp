# PharmacyApp

JavaFX-приложение для управления аптекой с использованием PostgreSQL и Docker.

## Возможности

- Управление лекарствами
- Создание партий товаров
- Создание продаж
- Просмотр статичтики за период
- Работа с PostgreSQL


# Используемые технологии

- Java 21
- JavaFX
- Maven
- PostgreSQL 15
- Docker Compose


# Требования

Перед запуском должны быть установлены:

- Java 21
- Maven
- Docker Desktop
- Git

Проверить установку можно командами:
```
java -version
mvn -version
docker --version
git --version
```

# Клонирование проекта

git clone https://github.com/Alena471/PharmacyApp

Перейти в папку проекта:

cd PharmacyApp


# Запуск PostgreSQL через Docker

## Полный сброс базы данных (рекомендуется при первом запуске)

docker compose down -v

## Запуск контейнера PostgreSQL

docker compose up

После запуска Docker автоматически:

* создаст базу данных `pharmacy_db`
* создаст таблицы
* заполнит тестовые данные
* настроит пароль пользователя postgres

# Запуск приложения
## Через терминал (без IDE)

Открыть второй терминал и выполнить:

mvn clean javafx:run


# Настройки подключения PostgreSQL

```properties
Host: localhost
Port: 5433
Database: pharmacy_db
User: postgres
Password: 0000
```

# Полезные команды Docker

## Проверка контейнеров

docker ps


## Остановка контейнера

docker compose down

## Полное удаление контейнера и базы данных

docker compose down -v

# Структура проекта

* `database.sql` — создание таблиц и тестовых данных
* `init-user.sql` — настройка пароля PostgreSQL
* `docker-compose.yml` — конфигурация Docker
* `pom.xml` — зависимости Maven

# Возможные проблемы

## Ошибка подключения к PostgreSQL

Убедитесь, что:

* Docker Desktop запущен
* контейнер PostgreSQL работает
* порт 5433 не занят другим приложением

Попробуйте выполнить:

docker compose down -v
docker compose up


```
