package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final InMemoryUserStorage userStorage;

    private static LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);

    private final Map<Long, Film> films = new HashMap<>();


    @Override
    public Film create(Film film) {
        log.info("Попытка создания нового фильма с названием: {}", film.getName());
        validateFilm(film);
        long newID = getNextId();
        film.setId(newID);
        films.put(newID, film);
        log.info("Фильм успешно создан с ID: {}, название: {}", newID, film.getName());
        return film;
    }


    @Override
    public Film update(Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            validateFilm(newFilm);
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setName(newFilm.getName());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Фильм с ID {} успешно обновлён", newFilm.getId());
            return oldFilm;
        }
        throw new NotFoundException("Пост с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Collection<Film> getAll() {
        log.info("Получен запрос на получение всех фильмов. Количество фильмов: {}", films.size());
        return films.values();
    }

    @Override
    public Film getById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }
        return film;
    }


    @Override
    public void addLike(Long filmId, Long userId) {

        Film film = getById(filmId);


        Collection<User> allUsers = userStorage.getAll();
        Set<Long> userIds = allUsers.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        if (! userIds.contains(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        } else {
            film.getLikes().add(userId);
            log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        }


    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        // Проверяем существование фильма
        Film film = getById(filmId);

        // Проверяем существование пользователя
        userStorage.getById(userId); // если пользователя нет, будет выброшено исключение

        boolean removed = film.getLikes().remove(userId);
        if (removed) {
            log.info("Пользователь {} убрал лайк у фильма {}", userId, filmId);
        } else {
            log.warn("Попытка удалить несуществующий лайк: пользователь {} для фильма {}", userId, filmId);
        }
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {

        List<Film> sortedFilms = films.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .collect(Collectors.toList());


        int actualCount = Math.min(count, sortedFilms.size());
        return sortedFilms.subList(0, actualCount);
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++ currentMaxId;

    }

    public static void validateFilm(Film film) {

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }


        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может превышать 200 символов");
        }


        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(earliestReleaseDate)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {

            throw new ValidationException("Продолжительность фильма должна быть положительным числом");

        }
    }

}
