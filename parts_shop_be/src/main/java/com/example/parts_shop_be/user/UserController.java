package com.example.parts_shop_be.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.ok().body("{\"message\": \"User registered successfully\"}");
    }

    @GetMapping("/get")
    public ResponseEntity<?> getUser(@RequestParam String email) {
        return ResponseEntity.ok(userService.findUserByEmail(email).get());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return ResponseEntity.ok().body("{\"message\": \"User updated successfully\"}");
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String email,
                                            @RequestParam String oldPassword,
                                            @RequestParam String newPassword) {
        boolean updated = userService.changePassword(email, oldPassword, newPassword);
        if (updated) {
            return ResponseEntity.ok().body("{\"message\": \"Password changed successfully\"}");
        } else {
            return ResponseEntity.status(401).body("{\"error\": \"Invalid old password\"}");
        }
    }
}

