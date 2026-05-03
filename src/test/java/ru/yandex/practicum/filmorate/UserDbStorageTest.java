package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({ UserDbStorage.class, UserRowMapper.class })
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

    private final UserDbStorage userStorage;
    private User testUser;
    private User anotherUser;


    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testuser");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));

        anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setLogin("anotheruser");
        anotherUser.setName("Another User");
        anotherUser.setBirthday(LocalDate.of(1995, 5, 15));
    }

    @Test
    void shouldCreateUserSuccessfully() {
        User createdUser = userStorage.create(testUser);


        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isPositive();
        assertThat(createdUser.getEmail()).isEqualTo("test@example.com");
        assertThat(createdUser.getLogin()).isEqualTo("testuser");
    }

    @Test
    void shouldGetAllUsers() {
        userStorage.create(testUser);
        userStorage.create(anotherUser);

        Collection<User> allUsers = userStorage.getAll();

        assertThat(allUsers).hasSize(7);
        assertThat(allUsers)
                .extracting(User::getEmail)
                .contains("test@example.com", "another@example.com");
    }

    @Test
    void shouldGetUserById() {
        User createdUser = userStorage.create(testUser);

        Optional<User> foundUser = userStorage.getById(createdUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(createdUser.getId());
        assertThat(foundUser.get().getName()).isEqualTo("Test User");
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserNotFound() {
        Optional<User> user = userStorage.getById(999L);

        assertThat(user).isEmpty();
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        User createdUser = userStorage.create(testUser);

        createdUser.setName("Updated Name");
        createdUser.setEmail("updated@example.com");

        User updatedUser = userStorage.update(createdUser);

        Optional<User> retrievedUser = userStorage.getById(updatedUser.getId());

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getName()).isEqualTo("Updated Name");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void shouldAddFriendSuccessfully() {
        User user1 = userStorage.create(testUser);
        User user2 = userStorage.create(anotherUser);

        userStorage.addFriend(user1.getId(), user2.getId());

        boolean isFriend = userStorage.isFriend(user1.getId(), user2.getId());
        assertThat(isFriend).isTrue();
    }

    @Test
    void shouldGetFriendsList() {
        User user1 = userStorage.create(testUser);
        User user2 = userStorage.create(anotherUser);

        userStorage.addFriend(user1.getId(), user2.getId());


        Collection<User> friends = userStorage.getFriends(user1.getId());

        assertThat(friends).hasSize(1);
        assertThat(friends)
                .extracting(User::getName)
                .contains("Another User");
    }

}
