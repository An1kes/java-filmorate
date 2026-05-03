package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({ FilmDbStorage.class, FilmRowMapper.class, GenreRowMapper.class })
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;


    private Film createTestFilm() {
        Film film = new Film();
        film.setName("Тест-фильм");
        film.setDescription("Описание тест-фильма");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setMpa(createMpa(1));
        film.setGenres(Set.of(createGenre(3), createGenre(4)));
        return film;
    }

    private MpaRating createMpa(Integer id) {
        MpaRating mpa = new MpaRating();
        mpa.setId(id.longValue());
        return mpa;
    }

    private Genre createGenre(Integer id) {
        Genre genre = new Genre();
        genre.setId(id.longValue());
        return genre;
    }

    @Test
    void shouldCreateFilm() {
        Film createdFilm = filmStorage.create(createTestFilm());

        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo("Тест-фильм");
        assertThat(filmStorage.getById(createdFilm.getId()))
                .isPresent()
                .get()
                .satisfies(film -> {
                    assertThat(film.getDescription()).isEqualTo("Описание тест-фильма");
                    assertThat(film.getDuration()).isEqualTo(120);
                    assertThat(film.getReleaseDate()).isEqualTo(LocalDate.of(2023, 1, 1));
                });
    }

    @Test
    void shouldUpdateFilm() {
        Film film = createTestFilm();
        Film createdFilm = filmStorage.create(film);

        createdFilm.setName("Обновлённый фильм");
        createdFilm.setDuration(150);
        filmStorage.update(createdFilm);

        Optional<Film> updatedFilm = filmStorage.getById(createdFilm.getId());
        assertThat(updatedFilm).isPresent();
        assertThat(updatedFilm.get().getName()).isEqualTo("Обновлённый фильм");
        assertThat(updatedFilm.get().getDuration()).isEqualTo(150);
    }

    @Test
    void shouldFindFilmById() {
        Film film = createTestFilm();
        Film createdFilm = filmStorage.create(film);

        Optional<Film> foundFilm = filmStorage.getById(createdFilm.getId());
        assertThat(foundFilm).isPresent();
        assertThat(foundFilm.get().getName()).isEqualTo("Тест-фильм");
        assertThat(foundFilm.get().getGenres()).hasSize(2); // проверка жанров
    }

    @Test
    void shouldFindAllFilms() {
        Film film1 = createTestFilm();
        filmStorage.create(film1);

        Film film2 = createTestFilm();
        film2.setName("Ещё один фильм");
        film2.setDuration(90);
        film2.setReleaseDate(LocalDate.of(2022, 12, 31));
        filmStorage.create(film2);

        List<Film> allFilms = filmStorage.getAll();
        assertThat(allFilms).hasSize(8);
        assertThat(allFilms).extracting(Film::getName)
                .contains("Тест-фильм", "Ещё один фильм");
    }

    @Test
    void shouldAddLike() {
        Film film = createTestFilm();
        Film createdFilm = filmStorage.create(film);

        long userId = 4L;
        filmStorage.addLike(createdFilm.getId(), userId);

        filmStorage.addLike(createdFilm.getId(), userId);
    }

    @Test
    void shouldRemoveLike() {
        Film film = createTestFilm();
        Film createdFilm = filmStorage.create(film);

        long userId = 2L;
        filmStorage.addLike(createdFilm.getId(), userId);

        filmStorage.removeLike(createdFilm.getId(), userId);
        filmStorage.removeLike(createdFilm.getId(), userId);
    }

    @Test
    void shouldReturnPopularFilms() {
        Film film1 = createTestFilm();
        Film film2 = createTestFilm();
        film2.setName("Фильм 2");
        Film film3 = createTestFilm();
        film3.setName("Фильм 3");

        Film f1 = filmStorage.create(film1);
        Film f2 = filmStorage.create(film2);
        Film f3 = filmStorage.create(film3);

        filmStorage.addLike(f1.getId(), 2L);
        filmStorage.addLike(f1.getId(), 3L); // 2 лайка
        filmStorage.addLike(f2.getId(), 2L); // 1 лайк

        Collection<Film> popularFilms = filmStorage.getPopularFilms(3);
        assertThat(popularFilms).hasSize(3);


    }
}

