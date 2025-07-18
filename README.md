# 🏭 Weyland-Yutani Core

> *"Building Better Worlds" - Weyland-Yutani Corporation*

[![License](https://img.shields.io/badge/License-Proprietary-blue.svg)](https://www.weylandindustries.com)
[![Build Status](https://img.shields.io/badge/Status-Experimental-yellow)](https://github.com/staveelxd/weyland-yutani)

## 🌌 О проекте

Добро пожаловать в официальный репозиторий Weyland-Yutani Core - высокопроизводительной платформы для создания корпоративных решений нового поколения. Наш фреймворк обеспечивает надежную инфраструктуру для построения масштабируемых и отказоустойчивых систем, способных работать в самых экстремальных условиях.

## 🚀 Быстрый старт

### Установка

1. Клонируйте репозиторий:
```bash
git clone https://github.com/staveelxd/weyland-yutani.git
cd weyland-yutani
```
2. Соберите проект:
```bash
mvn clean install
```
3. Настройте конфигурацию в application.yml
```yaml
weyland:
  security:
    audit:
      enabled: true
      mode: CONSOLE  # или KAFKA для продакшна
```
4. Запустите приложение
```bash
cd bishop-prototype
mvn spring-boot:run
```
## 🛠️ Использование
> *"В космосе никто не услышит, как ты кричишь... но наша система логирования точно все запишет." - Отдел контроля качества Weyland-Yutani*
### Отправка команд
1. Просмотр текущей занятости андроида (количество задач в очереди) и количества выполненных заданий для каждого автора осуществляется по адресу
http://localhost:8080/commands/queue-status
2. Отправление команды осуществляет POST-запросом по адресу http://localhost:8080/commands.
Пример тела команды:
```json
{
    "description": "Проверить состояние энергоблока",
    "priority": "CRITICAL",
    "author": "Лейтенант Эллен Рипли",
    "time": "2025-07-17T12:00:00Z"
}
```
Или еще такой:
```json
{
    "description": "Помыть пол",
    "priority": "COMMON",
    "author": "Папа",
    "time": "2025-07-18T12:01:00Z"
}
```
© 2025 Weyland-Yutani Corporation. Все права защищены.
"Building Better Worlds" - это зарегистрированная торговая марка Weyland-Yutani Corp.

