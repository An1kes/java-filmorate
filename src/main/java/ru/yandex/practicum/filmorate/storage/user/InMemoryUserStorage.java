package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();


    @Override
    public User create(User user) {
        log.info("Попытка создания нового пользователя с email: {}", user.getEmail());
        validateUser(user); // Валидация перед сохранением

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

    @Override
    public void delete(Long id) {
        if (! users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        users.remove(id);
        log.info("Пользователь с ID {} успешно удалён", id);
    }

    @Override
    public User getById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return user;
    }


    @Override
    public Collection<User> getFriends(Long userId) {
        User user = getById(userId);
        Set<Long> friendIds = user.getFriends();
        List<User> friends = new ArrayList<>(friendIds.size());

        for (Long friendId : friendIds) {
            User friend = getById(friendId);
            friends.add(friend);
        }

        log.info("Получен список из {} друзей для пользователя {}", friends.size(), userId);
        return friends;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId); // двусторонняя связь

        log.info("Пользователь {} добавлен в друзья пользователя {}", friendId, userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId); // двустороннее удаление

        log.info("Пользователь {} удалён из друзей пользователя {}", friendId, userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        Set<Long> user1Friends = getById(userId1).getFriends();
        Set<Long> user2Friends = getById(userId2).getFriends();

        // Находим общие ID друзей
        Set<Long> commonFriendIds = new HashSet<>(user1Friends);
        commonFriendIds.retainAll(user2Friends);

        // Преобразуем ID в объекты User
        List<User> commonFriends = new ArrayList<>(commonFriendIds.size());
        for (Long friendId : commonFriendIds) {
            User commonFriend = getById(friendId);
            commonFriends.add(commonFriend);
        }

        log.info("Найдено {} общих друзей для пользователей {} и {}",
                commonFriends.size(), userId1, userId2);
        return commonFriends;
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