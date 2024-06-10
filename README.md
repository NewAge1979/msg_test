# MESSENGER CHAT RELEASE V.0.1.0

### Данный релиз предназначен для промежуточной реализации проекта "Messenger".
### Реализованы следующие возможности:
1. Генерациа и отправка через СМС OTP кода.
2. Верификация OTP кода, подсчет количества ошибок ввода OTP кода и временная блокировка пользователя при определенном количестве ошибок ввода OTP кода.
3. Регистрация нового пользователя.
4. Авторизация существующего пользователя.
5. Отправка сообщения.
6. Удаление сообщения.
7. Перессылка сообщения.
8. Очистка чата.
9. Поиск сообщений по ключевой фразе.
### Так же реализована первичная настройка безопасности, для исключения не авторизованного доступа к закрытому функционалу сервиса.
### В дальнейшем для ключевых пользователей сервиса планируем сделать двухфакторную авторизацию.


# WebSocket API

## Подключение

Подключитесь к WebSocket по следующему URL:
ws://localhost:8080/api/v1/ws/chat


## Сообщения

### При подключении

При подключении вы получите следующее сообщение:

```json
{
  "type": "chat_list",
  "chats": [
    {
      "chat_name": "ivan",
      "description": null,
      "created": "23:15:14 10.06.2024",
      "participant_id": [1,2],
      "messages": [
        {
          "message": "Hello2",
          "sender": 1,
          "created": "23:21:47 10.06.2024",
          "attachments": []
        }
      ]
    }
  ]
}

Отправка сообщения
Чтобы отправить сообщение, отправьте следующий JSON:


{
  "action": "send_message",
  "message": "Hello, how are you?",
  "participant_id": 2,
  "attachments": [
    {
      "type": "image",
      "url": "http://example.com/image1.jpg"
    },
    {
      "type": "video",
      "url": "http://example.com/video1.mp4"
    }
  ]
}

Участник получит следующее сообщение:

{
  "type": "chat_message",
  "message": {
    "message": "Hello, how are you?",
    "sender": 1,
    "created": "23:56:40 10.06.2024",
    "attachments": [
      {
        "type": "image",
        "url": "http://example.com/image1.jpg"
      },
      {
        "type": "video",
        "url": "http://example.com/video1.mp4"
      }
    ]
  },
  "chat_id": 1,
  "original_sender": null
}

Очистка чата
Чтобы очистить чат, отправьте следующий JSON:

{
  "action":"clear_chat",
  "chat_id":"1"
}

Вы получите следующее сообщение:

{
  "type": "chat_cleared",
  "message": [],
  "chat_id": 1
}

Поиск сообщений
Чтобы найти сообщения, отправьте следующий JSON:

{
  "action": "search_messages",
  "search_query": "ello",
  "chat_id": "1"
}

Вы получите следующее сообщение:

{
  "type": "search_results",
  "messages": [
    {
      "message": "Hello, how are you?",
      "sender": 1,
      "created": "23:56:40 10.06.2024",
      "attachments": [
        {
          "type": "image",
          "url": "http://example.com/image1.jpg"
        },
        {
          "type": "video",
          "url": "http://example.com/video1.mp4"
        }
      ]
    }
  ]
}

Пересылка сообщения
Чтобы переслать сообщение, отправьте следующий JSON:

{
  "action": "forward_message",
  "participant_id": 2,
  "message": "Original message content",
  "attachments_message": [
    {
      "type": "image",
      "url": "http://example.com/image1.jpg"
    }
  ],
  "forwarded_message": "This is a forwarded message",
  "attachments_forwarded_message": [
    {
      "type": "video",
      "url": "http://example.com/video1.mp4"
    }
  ],
  "original_sender": "John Doe"
}

Вы получите следующее сообщение:

{
  "type": "confirmation",
  "message": "Message forwarded successfully",
  "created": false,
  "chat_id": 1
}

Участник получит следующее сообщение:

{
  "type": "chat_message",
  "message": {
    "message": "This is a forwarded message",
    "sender": 1,
    "created": "00:03:55 11.06.2024",
    "attachments": [
      {
        "type": "video",
        "url": "http://example.com/video1.mp4"
      }
    ]
  },
  "chat_id": 1,
  "original_sender": "John Doe"
}
{
  "type": "chat_message",
  "message": {
    "message": "Original message content",
    "sender": 1,
    "created": "00:03:55 11.06.2024",
    "attachments": [
      {
        "type": "image",
        "url": "http://example.com/image1.jpg"
      }
    ]
  },
  "chat_id": 1,
  "original_sender": null
}

Удаление сообщения
Чтобы удалить сообщение, отправьте следующий JSON:

{
  "action": "delete_message",
  "chat_id": "1",
  "message_id": "3",
  "delete_for_everyone": "false"
}

Вы получите следующее сообщение:

{
  "type": "message_deleted",
  "chat_id": 1,
  "message_id": 3
}


