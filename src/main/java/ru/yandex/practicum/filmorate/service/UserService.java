package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;


    private User getUserOrThrow(Long userId) {
        return userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User createUser(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {

        validateUser(user);
        getUserOrThrow(user.getId());
        return userStorage.update(user);
    }

    public void deleteUser(Long id) {
        getUserOrThrow(id);
        userStorage.delete(id);
    }


    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя добавить самого себя в друзья");
        }
        getUserOrThrow(userId);
        getUserOrThrow(friendId);
        userStorage.addFriend(userId, friendId);
    }


    public void removeFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя удалить самого себя из друзей");
        }
        getUserOrThrow(userId);
        getUserOrThrow(friendId);
        userStorage.removeFriend(userId, friendId);
    }


    public Collection<User> getUserFriends(Long userId) {
        getUserOrThrow(userId);
        return userStorage.getFriends(userId);

    }

    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Нельзя найти общий друзей у самого себя");
        }
        getUserOrThrow(userId1);
        getUserOrThrow(userId2);
        return userStorage.getCommonFriends(userId1, userId2);
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

}
