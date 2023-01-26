package antifraud.controller;

import antifraud.model.*;
import antifraud.model.DTO.UserDTO;
import antifraud.model.delete.DeletedUser;
import antifraud.model.request.UserRoleRequest;
import antifraud.model.request.UserStatusRequest;
import antifraud.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {
    UserService userService;

    @PostMapping("/api/auth/user")
    ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserDTO userDTO){
        return new ResponseEntity(userService.registerUser(userDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/api/auth/user/{username}")
    DeletedUser deleteUser(@PathVariable String username){
        return userService.deleteUser(username);
    }


    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPPORT')")
    @GetMapping("/api/auth/list")
    List<UserResponse> listUsers() {
        return userService.listUsers();
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/api/auth/role")
    UserResponse changeUserRole(@Valid @RequestBody UserRoleRequest userRoleRequest){
        return userService.updateUserRole(userRoleRequest);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/api/auth/access")
    UserStatusChange changeAccountStatus(@Valid @RequestBody UserStatusRequest userStatusRequest){
        return userService.changeUserStatus(userStatusRequest);
    }
}
