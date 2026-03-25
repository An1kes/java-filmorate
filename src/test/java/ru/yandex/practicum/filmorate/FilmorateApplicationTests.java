package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.controller.FilmController.validateFilm;


class FilmorateApplicationTests {


    // Тест на валидное кино (все поля в порядке)
    @Test
    void validateFilm_ValidFilm_ShouldNotThrowException() {
        Film film = new Film(
                "Название фильма",
                "Краткое описание (менее 200 символов)",
                LocalDate.of(2023, 1, 1),
                120
        );
        Assertions.assertDoesNotThrow(() -> validateFilm(film));
    }


    @Test
    void validateFilm_NullName_ShouldThrowValidationException() {
        Film film = new Film(null, "Описание", LocalDate.of(2023, 1, 1),
                120);
        Assertions.assertThrows(ValidationException.class, () -> validateFilm(film));
    }

    @Test
    void validateFilm_BlankName_ShouldThrowValidationException() {
        Film film = new Film("", "Описание", LocalDate.of(2023, 1, 1),
                120);
        Assertions.assertThrows(ValidationException.class, () -> validateFilm(film));
    }


    @Test
    void validateFilm_DescriptionLongerThan200Chars_ShouldThrowValidationException() {
        String longDescription = "a".repeat(201); // Строка из 201 символа
        Film film = new Film("Название", longDescription, LocalDate.of(2023, 1, 1),
                120);
        Assertions.assertThrows(ValidationException.class, () -> validateFilm(film));
    }


    @Test
    void validateFilm_ReleaseDateBefore1895_ShouldThrowValidationException() {
        LocalDate invalidDate = LocalDate.of(1895, 12, 27);
        Film film = new Film("Название", "Описание", invalidDate, 120);
        Assertions.assertThrows(ValidationException.class, () -> validateFilm(film));
    }


    @Test
    void validateFilm_ValidReleaseDate_ShouldNotThrowException() {
        LocalDate validDate = LocalDate.of(1896, 1, 1);
        Film film = new Film("Название", "Описание", validDate, 120);
        Assertions.assertDoesNotThrow(() -> validateFilm(film));
    }


    @Test
    void validateFilm_ZeroDuration_ShouldThrowValidationException() {
        Film film = new Film("Название", "Описание", LocalDate.of(2023, 1, 1),
                0);
        Assertions.assertThrows(ValidationException.class, () -> validateFilm(film));
    }


    @Test
    void validateFilm_NegativeDuration_ShouldThrowValidationException() {
        Film film = new Film("Название", "Описание", LocalDate.of(2023, 1, 1),
                - 1);
        Assertions.assertThrows(ValidationException.class, () -> validateFilm(film));
    }


    @Test
    void validateFilm_NullDuration_ShouldThrowValidationException() {
        Film film = new Film("Название", "Описание", LocalDate.of(2023, 1, 1),
                0);
        Assertions.assertThrows(ValidationException.class, () -> validateFilm(film));
    }
}
