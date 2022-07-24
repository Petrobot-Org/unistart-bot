# Запуск
1. Задать следующие переменные среды

| Переменная       | Описание               |
|------------------|------------------------|
| `TELEGRAM_TOKEN` | Токен от Telegram бота |

2. Создать файл конфигурации `application.yaml` с таким содержанием:
```yaml
timezone: Europe/Moscow
jdbc: "jdbc:sqlite:main.sqlite"
public_hostname: "https://unistart.ithersta.com" # Публичный адрес веб-приложения
root_admin_user_ids: # Список Telegram user id админов
  - 105293829
default_step_durations_seconds: # Длительность шагов по умолчанию
  1: 604800
  2: 604800
  3: 604800
  4: 604800
notifications:
  next_step:
    after_days: 10
    at: 19:00
```
