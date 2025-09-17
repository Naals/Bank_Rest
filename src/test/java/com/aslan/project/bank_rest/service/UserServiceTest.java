package com.aslan.project.bank_rest.service;

import com.aslan.project.bank_rest.entity.User;
import com.aslan.project.bank_rest.entity.UserRole;
import com.aslan.project.bank_rest.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private UserRepository userRepo;
    @InjectMocks private UserService userService;

    @BeforeEach
    void init() { MockitoAnnotations.openMocks(this); }

    @Test
    void createUser_shouldSaveUser() {
        User u = new User();
        u.setUsername("test");
        u.setRoles(UserRole.ROLE_USER);

        when(userRepo.save(any(User.class))).thenReturn(u);

        User created = userService.register("test", "pwd");

        assertEquals("test", created.getUsername());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void changeRole_shouldUpdateUserRole() {
        User u = new User();
        u.setId(1L);
        u.setRoles(UserRole.ROLE_USER);

        when(userRepo.findById(1L)).thenReturn(Optional.of(u));
        when(userRepo.save(any(User.class))).thenReturn(u);

        userService.changeRole(1L, UserRole.ROLE_ADMIN);

        assertEquals(UserRole.ROLE_ADMIN, u.getRoles());
    }
}
