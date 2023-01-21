package antifraud.controller;

import antifraud.model.DTO.UserDTO;
import antifraud.model.DeletedUser;
import antifraud.model.UserResponse;
import antifraud.model.UserRoleRequest;
import antifraud.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {
    UserService userService;

    @PostMapping("/api/auth/user")
    ResponseEntity<UserResponse> registerUser(@RequestBody @Valid UserDTO userDTO){
        System.out.println(userDTO);
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

}
