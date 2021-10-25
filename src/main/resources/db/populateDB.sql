DELETE FROM meals;
DELETE FROM user_roles;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);
 INSERT INTO meals (datetime, description, calories,user_id)
 VALUES ('2021-10-22 09:00:00', 'Завтрак', 500,100000),
        ('2021-10-22 12:00:00', 'Обед', 1000,100000),
        ('2021-10-23 19:00:00', 'Ужин на завтра', 2500,100000);
INSERT INTO meals (datetime, description, calories,user_id)
VALUES ('2021-10-22 09:00:00', 'Завтрак админа', 1500,100001),
       ('2021-10-22 12:00:00', 'Плотный обед админа', 1500,100001),
       ('2021-10-23 19:00:00', 'Ужин админа на завтра', 500,100001);