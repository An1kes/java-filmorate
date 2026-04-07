package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {


    private final Map<Long, Film> films = new HashMap<>();


    @Override
    public Film create(Film film) {
        log.info("Попытка создания нового фильма с названием: {}", film.getName());
        long newID = getNextId();
        film.setId(newID);
        films.put(newID, film);
        log.info("Фильм успешно создан с ID: {}, название: {}", newID, film.getName());
        return film;
    }


    @Override
    public Optional<Film> update(Film newFilm) {

        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setName(newFilm.getName());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        log.info("Фильм с ID {} успешно обновлён", newFilm.getId());
        return Optional.ofNullable(oldFilm);

    }

    @Override
    public Collection<Film> getAll() {
        log.info("Получен запрос на получение всех фильмов. Количество фильмов: {}", films.size());
        return films.values();
    }

    @Override
    public Optional<Film> getById(Long id) {
        Film film = films.get(id);
        return Optional.ofNullable(film);
    }


    @Override
    public void addLike(Long filmId, Long userId) {

        Film film = films.get(filmId);

        film.getLikes().add(userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }


    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = films.get(filmId);

        boolean removed = film.getLikes().remove(userId);
        if (removed) {
            log.info("Пользователь {} убрал лайк у фильма {}", userId, filmId);
        } else {
            log.warn("Попытка удалить несуществующий лайк: пользователь {} для фильма {}", userId, filmId);
        }

    }

    @Override
    public Collection<Film> getPopularFilms(int count) {

        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++ currentMaxId;

    }


}
