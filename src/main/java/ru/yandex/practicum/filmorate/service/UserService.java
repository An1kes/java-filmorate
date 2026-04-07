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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;


    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User createUser(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {

        Optional<User> result = userStorage.getById(user.getId());

        if (result.isPresent()) {

            if (user.getId() == null) {
                throw new ValidationException("ID пользователя должен быть указан");
            }
            return userStorage.update(user).get();
        } else {
            throw new NotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }

    }



    public void deleteUser(Long id) {
        Optional<User> result = userStorage.getById(id);

        if (result.isPresent()) {
            userStorage.delete(id);
        } else {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

    }


    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя добавить самого себя в друзья");
        }
        Optional<User> optionalUser = userStorage.getById(userId);
        Optional<User> optionalFriend = userStorage.getById(friendId);

        if (! optionalUser.isPresent()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        if (! optionalFriend.isPresent()) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя удалить самого себя из друзей");
        }
        Optional<User> optionalUser = userStorage.getById(userId);
        Optional<User> optionalFriend = userStorage.getById(friendId);
        if (! optionalUser.isPresent()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        if (! optionalFriend.isPresent()) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        userStorage.removeFriend(userId, friendId);
    }


    public Collection<User> getUserFriends(Long userId) {
        Optional<User> result = userStorage.getById(userId);
        if (! result.isPresent()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        } else {
            return userStorage.getFriends(userId);
        }
    }

    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Нельзя найти общий друзей у самого себя");
        }
        Optional<User> optionalUser = userStorage.getById(userId1);
        Optional<User> optionalFriend = userStorage.getById(userId2);
        if (! optionalUser.isPresent()) {
            throw new NotFoundException("Пользователь с id = " + userId1 + " не найден");
        }

        if (! optionalFriend.isPresent()) {
            throw new NotFoundException("Пользователь с id = " + userId2 + " не найден");
        }
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
