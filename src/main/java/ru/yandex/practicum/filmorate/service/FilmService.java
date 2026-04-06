package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {

    private static final int DEFAULT_POPULAR_COUNT = 10;
    private final InMemoryFilmStorage filmStorage;


    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }


    public Collection<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        int filmCount = count != null ? count : DEFAULT_POPULAR_COUNT;
        return filmStorage.getPopularFilms(filmCount);
    }

}
