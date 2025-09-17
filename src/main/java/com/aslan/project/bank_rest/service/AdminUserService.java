package com.aslan.project.bank_rest.service;

import com.aslan.project.bank_rest.dto.UserDto;
import com.aslan.project.bank_rest.entity.User;
import com.aslan.project.bank_rest.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Service
public class AdminUserService {
    private final UserRepository userRepo;

    public AdminUserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public Page<UserDto> listAllUsers(int page, int size) {
        return userRepo.findAll(PageRequest.of(page, size))
                .map(UserDto::fromEntity);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepo.findById(id).orElseThrow( () -> new EntityNotFoundException("User with id " + id + " not found"));
        userRepo.deleteById(user.getId());
    }
}


