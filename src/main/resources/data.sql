INSERT INTO genres (id, name, description) VALUES (1, 'Комедия', 'Загадки, расследования и поиск улик.');
INSERT INTO genres (id, name, description) VALUES (2, 'Драма', 'Юмор, смех и забавные ситуации.');
INSERT INTO genres (id, name, description) VALUES (3, 'Мультфильм', 'Глубокие эмоции и жизненные конфликты.');
INSERT INTO genres (id, name, description) VALUES (4, 'Триллер', 'Будущее, технологии и иные миры.');
INSERT INTO genres (id, name, description) VALUES (5, 'Документальный', 'Экшен, погони и напряжённые схватки.');
INSERT INTO genres (id, name, description) VALUES (6, 'Боевик', 'Напряжение, страх и неожиданные повороты.');


INSERT INTO mpa_rating (id, name, description) VALUES (1, 'G', 'Нет возрастных ограничений');
INSERT INTO mpa_rating (id, name, description) VALUES (2, 'PG', 'Рекомендуется смотреть с родителями');
INSERT INTO mpa_rating (id, name, description) VALUES (3, 'PG-13', 'До 13 лет не рекомендуется');
INSERT INTO mpa_rating (id, name, description) VALUES (4, 'R', 'До 17 лет только с родителями');
INSERT INTO mpa_rating (id, name, description) VALUES (5, 'NC-17', 'Только для зрителей старше 18 лет');


INSERT INTO films (name, description, duration, release_date, mpa_rating_id) VALUES
('Король Лев', 'История львёнка Симбы, который учится ответственности и мужеству.', 88, '1994-06-15', 1),
('Назад в будущее', 'Подросток Марти МакФлай путешествует во времени с учёным.', 116, '1985-07-03', 2),
('Побег из Шоушенка', 'Банкир Энди Дюфрейн попадает в тюрьму и строит план побега.', 142, '1994-09-23', 3),
('Гравитация', 'Астронавты борются за выживание после катастрофы в открытом космосе.', 91, '2013-10-04', 3),
('Джон Уик', 'Наёмный убийца мстит за убийство своей собаки и украденную машину.', 101, '2014-10-24', 4),
('Реинкарнация', 'Семья сталкивается с жуткими событиями после смерти бабушки.', 127, '2018-06-08', 5);

INSERT INTO film_genres (film_id, genre_id) VALUES
(1, 3),
(2, 4),
(3, 3),
(4, 4),
(5, 5),
(6, 6);

INSERT INTO users (email, login, name, birthday) VALUES
('test_email1@yandex.ru', 'test1', 'Иван Иванов', '1990-05-15'),
('test_email2@yandex.ru', 'test2', 'Мария Петрова', '1985-08-22'),
('test_email3@yandex.ru', 'test3', 'Алексей Смирнов', '1995-11-03'),
('test_email4@yandex.ru', 'test4', 'Елена Козлова', '1988-02-14'),
('test_email5@yandex.ru', 'test5', 'Дмитрий Волков', '1992-07-30');


INSERT INTO film_like (film_id, user_id) VALUES
(1, 2),
(2, 1),
(3, 3),
(4, 5),
(5, 4);

INSERT INTO users_friends (user_id, friend_id, confirmed) VALUES
(1, 2, FALSE),
(3, 4, FALSE),
(2, 5, TRUE),
(4, 1, TRUE),
(5, 3, TRUE);
