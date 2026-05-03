package ru.yandex.practicum.filmorate.storage.film;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Component
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;


    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, duration, release_date,  mpa_rating_id) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setInt(3, film.getDuration());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));

            if (film.getMpa() != null) {
                stmt.setLong(5, film.getMpa().getId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        saveFilmGenres(film);
        return film;
    }

    private void saveFilmGenres(Film film) {
        if (film.getGenres() == null) {
            return;
        }
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql, film.getGenres(), film.getGenres().size(),
                (ps, genre) -> {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, genre.getId());
                });
    }

    private void deleteGenre(Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());

        deleteGenre(film);
        saveFilmGenres(film);
        return film;
    }

    @Override
    public Optional<Film> getById(Long id) {
        String sql = """
                SELECT f.id, f.name, f.description,f.duration, f.release_date,
                       m.id AS mpa_rating_id, m.name AS mpa_name, m.description AS mpa_description
                FROM films f
                JOIN mpa_rating m ON f.mpa_rating_id = m.id
                WHERE f.id = ?
                """;

        Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, id);
        filmGetGenres(film);
        return Optional.ofNullable(film);
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.id," +
                " f.name," +
                " f.description," +
                " f.duration," +
                " f.release_date," +
                " m.id AS mpa_rating_id," +
                " m.name AS mpa_name," +
                " m.description AS mpa_description " +
                " FROM films f " +
                " JOIN mpa_rating m ON f.mpa_rating_id = m.id " +
                " ORDER BY f.id ";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        films.forEach(this::filmGetGenres);

        return films;
    }


    private Film filmGetGenres(Film film) {
        String sql = " SELECT g.id, g.name, g.description " +
                " FROM genres g " +
                " JOIN film_genres fg ON g.id = fg.genre_id " +
                " WHERE fg.film_id = ? " +
                " ORDER BY g.id ";

        Set<Genre> genres = new LinkedHashSet<>(
                jdbcTemplate.query(sql, genreRowMapper, film.getId())
        );
        film.setGenres(genres);
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        if (hasLike(filmId, userId)) {
            return;
        }
        String sql = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);

    }

    private boolean hasLike(Long filmId, Long userId) {
        String sql = "SELECT COUNT(*) FROM film_like WHERE film_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
        return count != null && count > 0;
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        if (! hasLike(filmId, userId)) {
            return;
        }
        String sql = """
                DELETE FROM film_like
                WHERE user_id = ? AND film_id = ?
                """;

        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        String sql = " SELECT f.id, f.name, f.description,f.duration, f.release_date," +
                " m.id AS mpa_rating_id, m.name AS mpa_name," +
                " m.description AS mpa_description" +
                " FROM films f " +
                " JOIN mpa_rating m ON f.mpa_rating_id = m.id" +
                " LEFT JOIN film_like fl ON fl.film_id = f.id" +
                " GROUP BY f.id" +
                " ORDER BY COUNT(fl.user_id) DESC, f.id ASC " +
                " LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, count);
        films.forEach(this::filmGetGenres);
        return films;
    }


}
