package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    void delete(Long id);

    Collection<User> getAll();

    Optional<User> getById(Long id);

    User update(User user);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Collection<User> getCommonFriends(Long userId1, Long userId2);

    Collection<User> getFriends(Long userId);

    boolean isFriend(Long userId, Long friendId);

    void updateFriendStatus(Long userId, Long friendId, FriendStatus status);

    boolean checkEmailDublication(Long userId, String email);
}
