# 🏭 Weyland-Yutani Core

> *"Building Better Worlds" - Weyland-Yutani Corporation*

[![License](https://img.shields.io/badge/License-Proprietary-blue.svg)](https://www.weylandindustries.com)
[![Build Status](https://img.shields.io/badge/Status-Experimental-yellow)](https://github.com/staveelxd/weyland-yutani)

## 🌌 О проекте

Добро пожаловать в официальный репозиторий Weyland-Yutani Core - высокопроизводительной платформы для создания корпоративных решений нового поколения. Наш фреймворк обеспечивает надежную инфраструктуру для построения масштабируемых и отказоустойчивых систем, способных работать в самых экстремальных условиях.

## 🚀 Быстрый старт через Docker

### Установка

1. Клонируйте репозиторий:
```bash
git clone https://github.com/staveelxd/weyland-yutani.git
cd weyland-yutani/synthetic-human-core-starter
```
2. Соберите библиотеку:
```bash
mvn clean install
```
3. Настройте конфигурацию в bishop-prototype/.../application.yml
```yaml
weyland:
  security:
    audit:
      enabled: true
      mode: KAFKA # по умолчанию, или CONSOLE
```
4. Соберите проект:
```bash
cd ../bishop-prototype
mvn clean install
```
5. Запустите контейнер через Docker
```bash
docker-compose up --build
```
## 🛠️ Использование
> *"В космосе никто не услышит, как ты кричишь... но наша система логирования точно все запишет." - Отдел контроля качества Weyland-Yutani*
### Отправка команд
1. Просмотр текущей занятости андроида (количество задач в очереди) и количества выполненных заданий для каждого автора осуществляется по адресу
http://localhost:8080/commands/queue-status
2. Просмотр сообщений аудита через Kafka осуществляется по адресу http://localhost:8081.
3. Отправление команды осуществляет POST-запросом по адресу http://localhost:8080/commands.

Примитивные команды можно посмотреть в [Postman коллекции](https://staveel.postman.co/workspace/staveel's-Workspace~cc3c3c3e-9ea6-4a96-82ca-a77432bd9246/collection/46849287-168ea32a-0309-4d7e-92c5-e07c48f7a4c4?action=share&source=copy-link&creator=46849287)

Пример тела команды:
```json
{
    "description": "Помыть пол",
    "priority": "COMMON",
    "author": "Генерал Мама",
    "time": "2025-07-18T12:01:00Z"
}
```

© 2025 Weyland-Yutani Corporation. Все права защищены.

"Building Better Worlds" - это зарегистрированная торговая марка Weyland-Yutani Corp.

