-- Добавление пользователей
INSERT INTO users (email, name) VALUES
                                    ('user1@example.com', 'User One'),
                                    ('user2@example.com', 'User Two'),
                                    ('user3@example.com', 'User Three'),
                                    ('user4@example.com', 'User Four');

-- Добавление категории
INSERT INTO categories (name) VALUES
    ('Category One');

-- Добавление события
INSERT INTO events (annotation, category, initiator, description, event_date, created_on, location_lat, location_lon, paid, participant_limit, request_moderation, title, state)
VALUES
    ('Sample annotation for the event',
     1, -- ID категории, добавленной выше
     1, -- ID инициатора, здесь это первый пользователь
     'Detailed description of the event',
     NOW() + INTERVAL '1 day', -- Дата события через 1 день
     NOW() - INTERVAL '1 day', -- Дата создания события
     '37.7749', -- Широта
     '-122.4194', -- Долгота
     TRUE, -- Платное событие
     100, -- Лимит участников
     TRUE, -- Модерация заявок
     'Sample Event Title',
     'PUBLISHED');

-- Добавление реакций (лайков) от пользователей
INSERT INTO reactions (event_id, user_id, reaction_type, created_at) VALUES
                                                                         (1, -- ID события
                                                                          3, -- ID пользователя 3
                                                                          'LIKE',
                                                                          NOW()),
                                                                         (1, -- ID события
                                                                          4, -- ID пользователя 4
                                                                          'LIKE',
                                                                          NOW());
