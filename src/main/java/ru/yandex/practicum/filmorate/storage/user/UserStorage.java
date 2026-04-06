package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User create(User user);

    void delete(Long id);

    Collection<User> getAll();

    User getById(Long id);

    User update(User user);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Collection<User> getCommonFriends(Long userId1, Long userId2);

    Collection<User> getFriends(Long userId);
}
