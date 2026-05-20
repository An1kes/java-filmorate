package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {

    Collection<Genre> getAll();

    Optional<Genre> findById(Long id);

    Collection<Genre> findByFilmId(Long id);

    List<Genre> findAllByIds(Set<Long> ids);
}
