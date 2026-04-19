package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.getAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User update(@RequestBody User updatedUser) {
        return userService.update(updatedUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addUserFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос от пользователя {} на добавления друга  {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Попытка удалить пользователя {} из друзей пользователя {}", friendId, id);
        userService.removeFriend(id, friendId);
        log.info("Пользователь {} успешно удалён из друзей пользователя {}", friendId, id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable Long id) {
        log.info("Получен запрос на получение списка друзей пользователя {}", id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен запрос на получение общих друзей пользователей {} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }


}
