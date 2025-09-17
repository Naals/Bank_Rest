package com.aslan.project.bank_rest.controller;

import com.aslan.project.bank_rest.dto.UserDto;
import com.aslan.project.bank_rest.dto.request.RegisterRequest;
import com.aslan.project.bank_rest.dto.response.ApiResponse;
import com.aslan.project.bank_rest.service.AuthService;
import com.aslan.project.bank_rest.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final UserService userService;
    private final AuthService authService;

    public AdminUserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok(new ApiResponse("User registered"));
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> all(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.listAllUsers(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse("User deleted"));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<?> block(@PathVariable Long id) {
        userService.blockUser(id);
        return ResponseEntity.ok(new ApiResponse("User blocked"));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok(new ApiResponse("User activated"));
    }
}
