package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.*;

@Component
@Qualifier("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    @Override
    public Collection<Genre> getAll() {
        return jdbcTemplate.query("SELECT * FROM genres", genreRowMapper);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        List<Genre> genres = jdbcTemplate.query("SELECT * FROM genres WHERE id = ?", genreRowMapper, id);
        return genres.stream().findFirst();
    }

    @Override
    public Collection<Genre> findByFilmId(Long id) {
        return jdbcTemplate.query(
                " SELECT g.id, g.name FROM genres g " +
                        " INNER JOIN film_genres fg ON fg.genre_id = g.id " +
                        " WHERE fg.film_id = ?", genreRowMapper, id);
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        StringBuilder placeholders = new StringBuilder();
        for (Long id : ids) {
            if (! placeholders.isEmpty()) {
                placeholders.append(", ");
            }
            placeholders.append("?");
        }

        String sql = "SELECT id, name FROM genres WHERE id IN (" + placeholders + ")";
        return jdbcTemplate.query(sql, genreRowMapper, ids.toArray());
    }
}
