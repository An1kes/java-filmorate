package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;


@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private static final int DEFAULT_POPULAR_COUNT = 10;
    private static LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private Film getFilmOrThrow(Long filmId) {
        return filmStorage.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
    }

    private User getUserOrThrow(Long userId) {
        return userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        getFilmOrThrow(film.getId());
        return filmStorage.update(film);
    }


    public Collection<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public void addLike(Long filmId, Long userId) {

        getFilmOrThrow(filmId);
        getUserOrThrow(userId);

        filmStorage.addLike(filmId, userId);

    }

    public void removeLike(Long filmId, Long userId) {

        getFilmOrThrow(filmId);
        getUserOrThrow(userId);

        filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        int filmCount = count != null ? count : DEFAULT_POPULAR_COUNT;
        return filmStorage.getPopularFilms(filmCount);
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
