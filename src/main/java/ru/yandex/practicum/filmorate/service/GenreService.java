package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    public GenreService(@Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Collection<Genre> getAll() {
        return genreStorage.getAll();
    }

    public Genre getById(Long genreId) {
        return getGenreOrThrow(genreId);

    }

    private Genre getGenreOrThrow(Long genreId) {
        return genreStorage.findById(genreId)
                .orElseThrow(() -> new NotFoundException("Жанр с айди id= " + genreId + " не найден."));
    }

    public List<Genre> findAllGenreById(Set<Long> genresId) {
        return genreStorage.findAllByIds(genresId);
    }
}
