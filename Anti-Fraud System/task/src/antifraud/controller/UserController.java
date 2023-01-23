package antifraud.controller;

import antifraud.model.*;
import antifraud.model.DTO.UserDTO;
import antifraud.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class UserController {
    UserService userService;

    @PostMapping("/api/auth/user")
    ResponseEntity<UserResponse> registerUser(@RequestBody @Valid UserDTO userDTO){
        return new ResponseEntity(userService.registerUser(userDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/api/auth/user/{username}")
    DeletedUser deleteUser(@PathVariable String username){
        return userService.deleteUser(username);
    }

    @GetMapping("/api/auth/list")
    List<UserResponse> listUsers() {
        return userService.listUsers();
    }

    @PutMapping("/api/auth/role")
    UserResponse changeUserRole(@RequestBody @Valid UserRoleRequest userRoleRequest){
        return userService.updateUserRole(userRoleRequest);
    }

    @PutMapping("/api/auth/access")
    UserStatusChange changeAccountStatus(@RequestBody @Valid UserStatusRequest userStatusRequest){
        return userService.changeUserStatus(userStatusRequest);
    }


}
