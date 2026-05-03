package ru.yandex.practicum.filmorate.storage.user;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;


    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);

    }

    @Override
    public Collection<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM users ORDER BY id", userRowMapper);
    }

    @Override
    public Optional<User> getById(Long id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", userRowMapper, id);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(users.get(0));
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {

        String sql = "INSERT INTO users_friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void updateFriendStatus(Long userId, Long friendId, FriendStatus status) {
        String sql = "UPDATE users_friends SET confirmed = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, status == FriendStatus.CONFIRMED.CONFIRMED, userId, friendId);
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        String sql = " SELECT COUNT(*) " +
                "FROM users_friends " +
                "WHERE user_id = ? AND friend_id = ? ";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);
        return count > 0;
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sql = " DELETE " +
                "FROM users_friends " +
                "WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        String sql = " SELECT u.* " +
                "FROM users u " +
                "JOIN users_friends f1 ON u.id = f1.friend_id " +
                "JOIN users_friends f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        return jdbcTemplate.query(sql, userRowMapper, userId1, userId2);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        String sql = " SELECT u.* " +
                "FROM users u " +
                "JOIN users_friends f ON u.id = f.friend_id " +
                "WHERE f.user_id = ? " +
                "ORDER BY u.id ";

        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    @Override
    public boolean checkEmailDublication(Long userId, String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE id <> ? AND email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, email);
        return count != null && count > 0;
    }
}
