 Foreign Student Bot

Многоязычная информационная система поддержки иностранных студентов. Включает Telegram-бот для навигации по базе знаний, административную веб-панель для управления контентом и пользовательское веб-приложение.

## Архитектура

```
                    ┌─────────────────────┐
  Браузер / Telegram │   API Gateway :443  │
  ──────────────────►│  (маршрутизация,    │
                    │   JWT-валидация)     │
                    └────────┬────────────┘
                             │
              ┌──────────────┴──────────────┐
              ▼                             ▼
   ┌──────────────────┐         ┌──────────────────┐
   │  Core Service    │◄────────│  Tg-Bot Service  │
   │  :8081           │  HTTP   │  :8082           │
   │                  │         │                  │
   │  - REST API      │         │  - Telegram API  │
   │  - Бизнес-логика │         │  - State Machine │
   │  - Файлы         │         │  - Redis-сессии  │
   └────────┬─────────┘         └──────────────────┘
            │
     ┌──────▼──────┐   ┌─────────────┐
     │   MongoDB   │   │    Redis    │
     │   :27017    │   │    :6379    │
     └─────────────┘   └─────────────┘
```

| Сервис | Технология | Назначение |
|--------|-----------|------------|
| `api-gateway` | Spring Cloud Gateway (WebFlux) | Единая точка входа, JWT-валидация |
| `core-service` | Spring Boot 3 + MongoDB | Бизнес-логика, REST API, файловое хранилище |
| `tg-bot-service` | Spring Boot 3 + TelegramBots | Telegram-бот, машина состояний диалога |
| `contract` | Java (shared library) | Общие DTO между сервисами |
| `admin-panel` | React 19 + TypeScript + Tiptap | Веб-панель администратора |
| `web-app` | React 19 + TypeScript + Vite | Пользовательское веб-приложение |

## Быстрый старт

### Требования

- Docker Engine 24+
- Docker Compose v2+

### 1. Клонировать репозиторий

```bash
git clone <repo-url>
cd foreign_bot
```

### 2. Создать файл `.env`

```env
# Telegram
TELEGRAM_BOT_TOKEN=your_bot_token_here
TELEGRAM_BOT_NAME=your_bot_username

# Режим работы бота: long-polling или webhook
TELEGRAM_BOT_MODE=long-polling

# Webhook (только для режима webhook)
TELEGRAM_WEBHOOK_URL=https://your-domain.com/telegram/webhook
TELEGRAM_WEBHOOK_PATH=/telegram/webhook
TELEGRAM_WEBHOOK_SECRET=

# Прокси (если Telegram недоступен напрямую)
TELEGRAM_PROXY_ENABLED=false
TELEGRAM_PROXY_TYPE=HTTP
TELEGRAM_PROXY_HOST=
TELEGRAM_PROXY_PORT=0
TELEGRAM_PROXY_USERNAME=
TELEGRAM_PROXY_PASSWORD=

# Администратор по умолчанию
ADMIN_BOOTSTRAP_EMAIL=admin@example.com
ADMIN_BOOTSTRAP_PASSWORD=changeme

# JWT
JWT_EXPIRATION_HOURS=24

# База данных (true = очистить при каждом старте, false = сохранять данные)
RESET_MONGO_ON_STARTUP=false
```

### 3. Запустить

```bash
docker compose up -d
```

После запуска:

| URL | Описание |
|-----|----------|
| `http://localhost/app` | Пользовательское веб-приложение |
| `http://localhost/admin` | Административная панель |
| `http://localhost/api` | REST API |

Первый запуск занимает 2–3 минуты (сборка образов). Порядок старта контейнеров управляется healthcheck-пробами автоматически.

## Переменные окружения

### Core Service

| Переменная | По умолчанию | Описание |
|------------|-------------|----------|
| `RESET_MONGO_ON_STARTUP` | `true` | Очищать MongoDB при старте |
| `JWT_EXPIRATION_HOURS` | `24` | Время жизни JWT-токена администратора |
| `ADMIN_BOOTSTRAP_EMAIL` | `admin` | Email первого администратора |
| `ADMIN_BOOTSTRAP_PASSWORD` | `admin` | Пароль первого администратора |

### Tg-Bot Service

| Переменная | По умолчанию | Описание |
|------------|-------------|----------|
| `TELEGRAM_BOT_TOKEN` | — | Токен бота из @BotFather |
| `TELEGRAM_BOT_NAME` | — | Username бота (без @) |
| `TELEGRAM_BOT_MODE` | `long-polling` | Режим: `long-polling` или `webhook` |
| `TELEGRAM_WEBHOOK_URL` | — | Публичный URL для webhook |
| `TELEGRAM_PROXY_ENABLED` | `false` | Включить прокси для Telegram API |

## Структура проекта

```
foreign_bot/
├── api-gateway/          # Spring Cloud Gateway
├── contract/             # Общие DTO (shared library)
├── core-service/         # Основной сервис
│   └── src/main/java/ru/vsu/core/
│       ├── controller/   # REST-контроллеры (api/, bot/, gateway/)
│       ├── service/      # Бизнес-логика
│       ├── model/        # Сущности MongoDB
│       ├── repository/   # Spring Data репозитории
│       └── component/    # Mapper'ы, Bootstrap-инициализация
├── tg-bot-service/       # Telegram-бот
│   └── src/main/java/ru/vsu/tgbot/
│       ├── services/statehandler/  # Машина состояний диалога
│       └── model/entity/Session.java
├── front/
│   ├── admin-panel/      # React-приложение администратора
│   └── web-app/          # React-приложение пользователя
└── docker-compose.yml
```

## Режимы работы Telegram-бота

### Long Polling (разработка)

Бот сам опрашивает серверы Telegram. Не требует публичного адреса.

```env
TELEGRAM_BOT_MODE=long-polling
```

### Webhook (продакшен)

Telegram доставляет обновления на ваш сервер. Требует публичный HTTPS-адрес.

```env
TELEGRAM_BOT_MODE=webhook
TELEGRAM_WEBHOOK_URL=https://your-domain.com/telegram/webhook
TELEGRAM_WEBHOOK_SECRET=random_secret_string
```

## Модель данных

Многоязычные тексты хранятся как словарь прямо в документе MongoDB:

```json
{
  "name": "postupleniye",
  "title": {
    "ru": "Поступление",
    "en": "Admission"
  },
  "parents": ["root"]
}
```

Добавление нового языка не требует изменений схемы — достаточно добавить новый ключ в словарь через административную панель.

## API

### Публичные эндпоинты (без авторизации)

```
GET  /group/root                   — корневое меню
GET  /group/{name}/children        — дочерние группы
GET  /question/group/{name}        — вопросы в группе
GET  /question/{id}/files          — вложения вопроса
GET  /file/{hash}/content          — скачать файл
GET  /language                     — список языков
GET  /ui-message                   — строки интерфейса бота
GET  /user/{chatId}                — язык пользователя
POST /user                         — сохранить язык пользователя
```

### Административный API (Bearer JWT)

```
POST   /api/auth/login             — получить токен
POST   /api/auth/register          — зарегистрировать администратора

GET    /api/group                  — все группы
POST   /api/group                  — создать группу
PATCH  /api/group/{name}/title     — обновить заголовок
DELETE /api/group/{name}           — удалить группу (каскадно)

GET    /api/question/group/{name}  — вопросы группы
POST   /api/question               — создать вопрос
PATCH  /api/question/{id}          — обновить вопрос
DELETE /api/question/{id}          — удалить вопрос
PUT    /api/question/{id}/files    — обновить вложения

POST   /api/file                   — загрузить файл
PATCH  /api/ui-message/{name}      — обновить строку интерфейса
```

## Разработка

### Локальный запуск отдельного сервиса

Поднять только инфраструктуру:

```bash
docker compose up mongo redis -d
```

Запустить Core Service из IDE или:

```bash
cd core-service
./gradlew bootRun
```

### Сборка фронтенда

```bash
cd front/admin-panel && npm install && npm run build
cd front/web-app     && npm install && npm run build
```

Собранные файлы копируются в `core-service/src/main/resources/static/` при Docker-сборке автоматически.

### Сброс базы данных

Установить в `.env`:

```env
RESET_MONGO_ON_STARTUP=true
```

и перезапустить `core`:

```bash
docker compose restart core
```

После сброса вернуть `false`.
