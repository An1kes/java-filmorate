package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей. Количество пользователей: {}", users.size());
        return users.values();
    }


    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Попытка создания нового пользователя с email: {}", user.getEmail());
        validateUser(user); // Валидация перед сохранением

        long newId = getNextId();
        user.setId(newId);
        users.put(newId, user);
        log.info("Пользователь успешно создан с ID: {}", newId);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User updatedUser) {
        if (updatedUser.getId() == null) {
            throw new ValidationException("ID пользователя должен быть указан");
        }

        User existingUser = users.get(updatedUser.getId());
        if (existingUser == null) {
            throw new NotFoundException("Пользователь с ID " + updatedUser.getId() + " не найден");
        }

        validateUser(updatedUser);


        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setLogin(updatedUser.getLogin());
        existingUser.setName(updatedUser.getName());
        existingUser.setBirthday(updatedUser.getBirthday());
        log.info("Пользователь с ID {} успешно обновлён", updatedUser.getId());
        return existingUser;
    }


    private void validateUser(User user) {

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Ошибка валидации: электронная почта не может быть пустой");
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (! user.getEmail().contains("@")) {
            log.error("Ошибка валидации: электронная почта должна содержать символ @");
            throw new ValidationException("Электронная почта должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("Ошибка валидации: логин не может быть пустым");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            log.error("Ошибка валидации: логин не может содержать пробелы");
            throw new ValidationException("Логин не может содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя не указано, использовано имя из логина: {}", user.getLogin());
            user.setName(user.getLogin());
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }


    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++ currentMaxId;
    }
}
