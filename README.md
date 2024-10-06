# MESSENGER CHAT RELEASE V.0.1.0

### Данный релиз предназначен для промежуточной реализации проекта "Messenger".

### Реализованы следующие возможности:

1. Генерация и отправка через СМС OTP кода.
2. Верификация OTP кода, подсчет количества ошибок ввода OTP кода и временная блокировка пользователя при определенном
   количестве ошибок ввода OTP кода.
3. Регистрация нового пользователя.
4. Авторизация существующего пользователя.
5. Отправка сообщения.
6. Удаление сообщения.
7. Перессылка сообщения.
8. Очистка чата.
9. Поиск сообщений по ключевой фразе.
10. Блокировка пользователя (запрет отправки вам сообщения).
11. Поиск пользователя по никнейм или по номеру телефона.

### Так же реализована первичная настройка безопасности, для исключения не авторизованного доступа к закрытому функционалу сервиса.

### В дальнейшем для ключевых пользователей сервиса планируем сделать двухфакторную авторизацию и авторизацию прав пользователя на основе ролевой модели. В данном разделе приводится описание команд, реализующие стандартные возможности чата.


# WebSocket API

## Подключение

Подключитесь к WebSocket по следующему URL:
ws://(адрес хоста)/api/v1/ws/chat

## Сообщения

### ***При подключении***

При подключении вы получите следующее сообщение:

```json
{
    "type": "chat_list",
    "chats": [
        {
            "id": 3,
            "chat_name": "",
            "description": null,
            "created": "21:09:03 27.08.2024",
            "participant_id": [
                3,
                2
            ],
            "messages": [
                {
                    "id": 3,
                    "message": "hi, user 4",
                    "sender": 3,
                    "created": "21:09:04 27.08.2024",
                    "attachments": [
                        {
                            "name": "file2",
                            "type": "image",
                            "data": "iVBORw0KGgoAA..." 
                        }
                    ]
                }
            ]
        }
    ]
}
```

### ***Отправка сообщения***
```json
Чтобы отправить сообщение, отправьте следующий JSON (здесь и далее, в поле "data" ожидается файл закодированный в base64, при отсутствии вложений в письме поле "documents" передается пустым ([])):

 {
                "action":"send_message",
                "message":"hi, user 7",
                "participant_id":7,
                "documents":[
                    {
                        "name":"file2",
                        "type":"image",
                        "data":""
                    }
                ]
}

При успешной отправке сообщения мы получим:
{
    "type": "confirmation",
    "message": "Message successfully",
    "created": boolean  (true false)
    "chat_id": 4
}

Если создан новый чат, получателю сообщения придет уведомление уведомление:
{
    "type": "chat_created",
    "message": "You have been added to a new chat!",
    "chat_id": 4,
    "sender": 3
}

Участник получит следующее сообщение:
{
    "type": "chat_message",
    "messages": {
        "id": 4,
        "message": "hi, user 7",
        "sender": 3,
        "created": "15:41:27 29.08.2024",
        "attachments": [
            {
                "name": "file2",
                "type": "image",
                "data": ""
            }
        ]
    },
    "chat_id": 4,
    "original_sender": null
}
```
### ***Удаление истории переписки (только у себя)***
```json
Чтобы очистить чат, отправьте следующий JSON:

{
  "action": "clear_chat",
  "chat_id": 4
}

Вы получите следующее сообщение:

{
    "type": "chat_cleared",
    "message": [],
    "chat_id": 4
}
```
### ***Поиск сообщений в чате***
```json
Чтобы найти сообщения, отправьте следующий JSON:

{
  "action": "search_messages",
  "search_query": "hi",
  "chat_id": "4"
}

Вы получите следующее сообщение:

{
    "type": "search_results",
    "messages": [
        {
            "id": 4,
            "message": "hi, user 7",
            "sender": 3,
            "created": "15:41:27 29.08.2024",
            "attachments": [
                {
                    "name": "file2",
                    "type": "image",
                    "data": ""
                }
            ]
        }
    ]
}
```
### ***Переслать сообщение (за один запрос можно переслать только одно сообщение)***
```json
Чтобы переслать сообщение, отправьте следующий JSON:

{
  "action": "forward_message",
  "participant_id": 7,
  "message": "Original message content",
  "attachments_message": [
    {
        "name":"Original attachments",
      "type": "image",
      "data":""
    }
  ],
  "forwarded_message": "This is a forwarded message",
  "attachments_forwarded_message": [
    {
        "name":"forwarded attachments",
      "type": "image",
      "data":""
    }
  ],
  "original_sender": "User 2"
}

Вы получите следующее сообщение:

{
    "type": "confirmation",
    "message": "Message forwarded successfully",
    "created": false,
    "chat_id": 4
}

Если создан новый чат, участник получает уведомление:
{
    "type": "chat_created",
    "chat_id": 4,
    "message": "You have been added to a new chat",
    "sender": 3
}

Участник получит следующее сообщение (первым придет сообщение которое перессылали, вторым новое сообщение):

{
    "type": "chat_message",
    "messages": {
        "id": 5,
        "message": "This is a forwarded message",
        "sender": 3,
        "created": "15:53:22 29.08.2024",
        "attachments": [
            {
                "name": "forwarded attachments",
                "type": "image",
                "data": ""
            }
        ]
    },
    "chat_id": 4,
    "original_sender": "User 2"
}
{
    "type": "chat_message",
    "messages": {
        "id": 6,
        "message": "Original message content",
        "sender": 3,
        "created": "15:53:22 29.08.2024",
        "attachments": [
            {
                "name": "Original attachments",
                "type": "image",
                "data": ""
            }
        ]
    },
    "chat_id": 4,
    "original_sender": null
}
```
### ***Удаление сообщения***
```json
Чтобы удалить сообщение, отправьте следующий JSON:

{
  "action": "delete_message",
  "chat_id": "1",
  "message_id": "3",
  "delete_for_everyone": "false" (true - удалить у всех (только если пользователь является отправителем сообщения)
                                  false - удалить у себя)
}

Вы получите следующее сообщение:

{
  "type": "message_deleted",
  "chat_id": 1,
  "message_id": 3
}
```
### ***Блокировка пользователя***
```json
Чтобы запретить отправлять вам сообщения, отправьте следующий JSON:
{
  "action": "block_contact",
  "user_id": 3
}

При успешной блокировке вы пролучите:
{
    "type": "confirmation",
    "message": "User 3 is blocked",
    "created": false,
    "chat_id": null
}
Заблокированный пользователь при попытке отправить/переслать вам сообщение получит ошибку:

{
    "type": "exception",
    "request": "...",
    "exception": "we are blocked by user"
}

Чтобы разблокировать пользователя надо повторно отправить:
{
  "action": "block_contact",
  "user_id": 3
}
```
### ***Поиск пользователя по никнейм***
```json
команда:
{
  "action": "search_user",
  "search_data": "Test_nickname1" (или номер телефон без 8, как при регистрации)
}
результат:
{
    "type": "search_user_results",
    "users": [
        {
            "id": 1,
            "phone": "9000000001",
            "firstName": "Test_first_name1",
            "lastName": "Test_last_name1",
            "email": null,
            "nickname": "Test_nickname1"
        }
    ]
}