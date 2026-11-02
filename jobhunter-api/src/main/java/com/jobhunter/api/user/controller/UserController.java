package com.jobhunter.api.user.controller;


import com.jobhunter.api.user.dto.CreateUserRequest;
import com.jobhunter.api.user.dto.UserResponse;
import com.jobhunter.api.user.entity.User;
import com.jobhunter.api.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/createUser")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request){

        User user = new User();
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setMiddleName(request.middleName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());

        User savedUser = userService.createUser(user);

        UserResponse response = new UserResponse(
                savedUser.getUserId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getMiddleName(),
                savedUser.getLastName(),
                savedUser.getPhoneNumber(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }
}
