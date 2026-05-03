package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();


    @Override
    public User create(User user) {
        log.info("Попытка создания нового пользователя с email: {}", user.getEmail());
        long newId = getNextId();
        user.setId(newId);
        users.put(newId, user);
        log.info("Пользователь успешно создан с ID: {}", newId);
        return user;
    }

    @Override
    public Collection<User> getAll() {
        log.info("Получен запрос на получение всех пользователей. Количество пользователей: {}", users.size());
        return users.values();
    }

    @Override
    public User update(User updatedUser) {

        User existingUser = users.get(updatedUser.getId());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setLogin(updatedUser.getLogin());
        existingUser.setName(updatedUser.getName());
        existingUser.setBirthday(updatedUser.getBirthday());
        log.info("Пользователь с ID {} успешно обновлён", updatedUser.getId());
        return existingUser;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
        log.info("Пользователь с ID {} успешно удалён", id);
    }

    @Override
    public Optional<User> getById(Long id) {
        User user = users.get(id);
        return Optional.ofNullable(user);
    }


    @Override
    public Collection<User> getFriends(Long userId) {
        User user = users.get(userId);
        Set<Long> friendIds = user.getFriends();
        List<User> friends = new ArrayList<>(friendIds.size());

        for (Long friendId : friendIds) {
            User friend = users.get(friendId);
            friends.add(friend);
        }

        log.info("Получен список из {} друзей для пользователя {}", friends.size(), userId);
        return friends;
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        return false;
    }

    @Override
    public void updateFriendStatus(Long userId, Long friendId, FriendStatus status) {

    }

    @Override
    public boolean checkEmailDublication(Long userId, String email) {
        return false;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        log.info("Пользователь {} добавлен в друзья пользователя {}", friendId, userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Пользователь {} удалён из друзей пользователя {}", friendId, userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        Set<Long> user1Friends = users.get(userId1).getFriends();
        Set<Long> user2Friends = users.get(userId2).getFriends();


        Set<Long> commonFriendIds = new HashSet<>(user1Friends);
        commonFriendIds.retainAll(user2Friends);


        List<User> commonFriends = new ArrayList<>(commonFriendIds.size());
        for (Long friendId : commonFriendIds) {
            User commonFriend = users.get(friendId);
            commonFriends.add(commonFriend);
        }

        log.info("Найдено {} общих друзей для пользователей {} и {}",
                commonFriends.size(), userId1, userId2);
        return commonFriends;
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