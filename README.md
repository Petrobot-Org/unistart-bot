# Запуск

1. Задать следующие переменные среды

| Переменная | Описание               |
|------------|------------------------|
| `TOKEN`    | Токен от Telegram бота |

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
    before_seconds: 86400
duration_to_bonus:
  - duration_factor: 0.5
    bonus: 8
  - duration_factor: 1.0
    bonus: 5
  - duration_factor: 2.0
    bonus: 3
```
