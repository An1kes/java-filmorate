package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Получен запрос на получение всех фильмов. Количество фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Попытка создания нового фильма с названием: {}", film.getName());
        validateFilm(film);
        long newID = getNextId();
        film.setId(newID);
        films.put(newID, film);
        log.info("Фильм успешно создан с ID: {}, название: {}", newID, film.getName());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {

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
