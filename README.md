# Курсовой проект "Сетевой чат"

## Описание проекта

Вам нужно разработать два приложения для обмена текстовыми сообщениями по сети с помощью консоли (терминала) между двумя и более пользователями.

**Первое приложение - сервер чата**, должно ожидать подключения пользователей.

**Второе приложение - клиент чата**, подключается к серверу чата и осуществляет доставку и получение новых сообщений.

Все сообщения должны записываться в file.log как на сервере, так и на клиентах. File.log должен дополняться при каждом запуске, а также при отправленном или полученном сообщении. Выход из чата должен быть осуществлен по команде exit.

## Требования к серверу

- Установка порта для подключения клиентов через файл настроек (например, settings.txt);
- Возможность подключиться к серверу в любой момент и присоединиться к чату;
- Отправка новых сообщений клиентам;
- Запись всех отправленных через сервер сообщений с указанием имени пользователя и времени отправки.

## Требования к клиенту

- Выбор имени для участия в чате;
- Прочитать настройки приложения из файла настроек - например, номер порта сервера;
- Подключение к указанному в настройках серверу;
- Для выхода из чата нужно набрать команду выхода - “/exit”;
- Каждое сообщение участников должно записываться в текстовый файл - файл логирования. При каждом запуске приложения файл должен дополняться.

## Требования в реализации

- Сервер должен уметь одновременно ожидать новых пользователей и обрабатывать поступающие сообщения от пользователей;
- Использован сборщик пакетов gradle/maven;
- Код размещен на github;
- Код покрыт unit-тестами.

## Шаги реализации:

1. Нарисовать схему приложений;

![simple_schema.png](docs%2Fsimple_schema.png)

2. Описать архитектуру приложений (сколько потоков за что отвечают, придумать протокол обмена сообщениями между приложениями);

---

Диаграмма классов сервера:

![1.png](docs%2F1.png)

Как выглядит вызов функций сервера:

![2.png](docs%2F2.png)

---

Диаграмма классов клиента:

![3.png](docs%2F3.png)

Как выглядит вызов функций клиента:

![4.png](docs%2F4.png)

---

Sequence Schema:

![sequence_diagram.png](docs%2Fsequence_diagram.png)

---

3. Создать репозиторий проекта на github;
4. Написать сервер;
5. Провести интеграционный тест сервера, например с помощью telnet;
6. Написать клиент;
7. Провести интеграционный тест сервера и клиента;

Простой интеграционный тест:

[ServerClientIntegrationTest.java](server%2Fsrc%2Ftest%2Fjava%2Forg%2Fdromakin%2FServerClientIntegrationTest.java)

Результат:

![5.png](docs%2F5.png)

Подробные тесты проводились вручную с записью вызовов с помощью AppMap. (Изображения выше: Sequence Diagram)

Логи клиента:
[client_example.log](logs%2Fclient_example.log)

Логи сервера:
[server_example.log](logs%2Fserver_example.log)

8. Протестировать сервер при подключении нескольких клиентов;

Результат тестирования:

![6.png](docs%2F6.png)

![7.png](docs%2F7.png)

Terminal client:
```shell
14-Jun-2023 23:29 [main] INFO  org.dromakin.Client - Welcome to terminal chat!
14-Jun-2023 23:29 [main] INFO  org.dromakin.Client - Please write who you are:
14-Jun-2023 23:29 [main] INFO  org.dromakin.Client - Write your nickname:
dromakin
14-Jun-2023 23:30 [main] INFO  org.dromakin.Client - Write your name:
d
14-Jun-2023 23:30 [main] INFO  org.dromakin.Client - Write your surname:
r
14-Jun-2023 23:30 [main] INFO  org.dromakin.ChatClient - User @dromakin try to connect to server localhost:8888
14-Jun-2023 23:30 [main] INFO  org.dromakin.Client - Welcome to client menu!
Client CLI commands:
/chat - view chat
/msg - type msg and send
/exit - stop client and close menu
/msg
14-Jun-2023 23:31 [main] INFO  org.dromakin.Client - Write your message:
Hi, Admin!
14-Jun-2023 23:31 [main] INFO  org.dromakin.Client - Go back to menu...
14-Jun-2023 23:31 [main] INFO  org.dromakin.Client - Welcome to client menu!
Client CLI commands:
/chat - view chat
/msg - type msg and send
/exit - stop client and close menu
/msg
14-Jun-2023 23:31 [main] INFO  org.dromakin.Client - Write your message:
I am fine, thanks! What's you?
14-Jun-2023 23:32 [main] INFO  org.dromakin.Client - Go back to menu...
14-Jun-2023 23:32 [main] INFO  org.dromakin.Client - Welcome to client menu!
Client CLI commands:
/chat - view chat
/msg - type msg and send
/exit - stop client and close menu
/msg
14-Jun-2023 23:32 [main] INFO  org.dromakin.Client - Write your message:
Thank you! You too!
14-Jun-2023 23:33 [main] INFO  org.dromakin.Client - Go back to menu...
14-Jun-2023 23:33 [main] INFO  org.dromakin.Client - Welcome to client menu!
Client CLI commands:
/chat - view chat
/msg - type msg and send
/exit - stop client and close menu
/chat
14-Jun-2023 23:30 | delay: 30 ms | @System BOT: User @dromakin has joined our chat!
14-Jun-2023 23:30 | delay: 2 ms | @System BOT: User @admin has joined our chat!
14-Jun-2023 23:31 | delay: 3 ms | @dromakin: Hi, Admin!
14-Jun-2023 23:31 | delay: 3 ms | @dromakin: Hi, dromakin!
14-Jun-2023 23:31 | delay: 2 ms | @dromakin: How are you?
14-Jun-2023 23:32 | delay: 3 ms | @dromakin: I am fine, thanks! What's you?
14-Jun-2023 23:32 | delay: 3 ms | @dromakin: I am good, thanks!
14-Jun-2023 23:32 | delay: 5 ms | @dromakin: Have a nice day! Bye!
14-Jun-2023 23:33 | delay: 3 ms | @dromakin: Thank you! You too!
14-Jun-2023 23:33 [main] INFO  org.dromakin.Client - Go back to menu...
14-Jun-2023 23:33 [main] INFO  org.dromakin.Client - Welcome to client menu!
Client CLI commands:
/chat - view chat
/msg - type msg and send
/exit - stop client and close menu
/exit
14-Jun-2023 23:33 [Thread-1] INFO  org.dromakin.ChatClient - Server disconnected!

Process finished with exit code 0
```

Server client:
```shell
14-Jun-2023 23:29 [main] INFO  org.dromakin.ChatController - Welcome to server management console!
14-Jun-2023 23:29 [main] INFO  org.dromakin.ChatController - Welcome to server menu!
Server CLI commands:
/start - start server
/stop - stop server
/exit - stop server and close menu
/stat - print statistic from server
/start
14-Jun-2023 23:30 [Thread-1] INFO  org.dromakin.ConnectionHandler - Start listening incoming messages!
14-Jun-2023 23:30 [Thread-1] INFO  org.dromakin.ConnectionHandler - Listening on port: 8888
14-Jun-2023 23:30 [Thread-1] INFO  org.dromakin.ChatController - Connected new client: /127.0.0.1:64358
14-Jun-2023 23:30 [Thread-1] INFO  org.dromakin.ChatController - Connected new client: /127.0.0.1:64359
/stat
14-Jun-2023 23:30 [main] INFO  org.dromakin.ChatController - Server statistic:
Messages count: 2
2 active users out of 10
/stat
14-Jun-2023 23:33 [main] INFO  org.dromakin.ChatController - Server statistic:
Messages count: 9
2 active users out of 10
14-Jun-2023 23:33 [Thread-3] INFO  org.dromakin.Server - Thread client @admin finished!
14-Jun-2023 23:33 [Thread-3] INFO  org.dromakin.Server - Thread user @admin finished!
14-Jun-2023 23:33 [Thread-2] INFO  org.dromakin.Server - Thread client @dromakin finished!
14-Jun-2023 23:33 [Thread-2] INFO  org.dromakin.Server - Thread user @dromakin finished!
/exit
14-Jun-2023 23:34 [main] INFO  org.dromakin.ChatController - System BOT: Get command to close chat! Start processing...
14-Jun-2023 23:34 [main] INFO  org.dromakin.ChatController - Server finished!

Process finished with exit code 0
```

Логи клиента 1:
[client_1_multithread_example.log](logs%2Fclient_1_multithread_example.log)

Логи клиента 2:
[client_2_multithread_example.log](logs%2Fclient_2_multithread_example.log)

Логи сервера:
[server_multithread_example.log](logs%2Fserver_multithread_example.log)


9. Написать README.md к проекту;
10. Отправить на проверку.

