# TrafficTask

Скачать проект с https://github.com/alekszalata/TrafficTask/tree/develop

Для запуска:
1) Запустить Kafka и Zookeeper. Для этого перейти в папку src/docker-kafka, открыть консоль в этой папке и написать: docker compose up
2) Установить зависимости из pom.xml

3) Указать все необходимые параметры для запуска в src/main/resources/config.properties

Обязательные к заполнению:

База данных postgresql

ip - ip для подключения к базе данных

port - port для подключения к базе данных

user - логин для подключения к бд

password - пароль для подключения к бд

Опционально:

valuesUpdateSeconds - количество секунд для обновления значений из базы данных (По дефолту 20 мин)

valuesUpdateSeconds - длина окна в спарк, которое складывает длины пакетов (По дефолту 5 мин)

4) Запустить main в классе Main.java

После запуска в консоли необходимо будет выбрать интерфейс для получения пакетов.

![image](https://user-images.githubusercontent.com/22037063/137897647-de8b5645-36f3-4171-9f60-dd4e814ca60a.png)

Для проверки получения алертов в kafka:

1) Открыть консоль
 
2) docker exec -it kafka bash
 
3) cd opt/kafka
 
4) ./bin/kafka-console-consumer.sh —bootstrap-server localhost:9092 —topic alerts —from-beginning

