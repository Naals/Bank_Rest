package com.aslan.project.bank_rest.service;

import com.aslan.project.bank_rest.dto.UserDto;
import com.aslan.project.bank_rest.entity.User;
import com.aslan.project.bank_rest.entity.UserRole;
import com.aslan.project.bank_rest.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public Page<UserDto> listAllUsers(int page, int size) {
        return userRepo.findAll(PageRequest.of(page, size))
                .map(UserDto::fromEntity);
    }

    public UserDto getById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserDto.fromEntity(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepo.deleteById(id);
    }

    @Transactional
    public void blockUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);
        userRepo.save(user);
    }

    @Transactional
    public void activateUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepo.save(user);
    }

    @Transactional
    public void changeRole(Long id, UserRole newRole) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRoles(newRole);
        userRepo.save(user);
    }

    @Transactional
    public User register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setRoles(UserRole.ROLE_USER);
        user.setEnabled(true);

        return userRepo.save(user);
    }
}
