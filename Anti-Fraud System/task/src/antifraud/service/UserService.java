package antifraud.service;

import antifraud.mappers.ModelMapper;
import antifraud.model.User;
import antifraud.model.delete.DeletedUser;
import antifraud.model.dto.UserDTO;
import antifraud.model.enums.AccountStatus;
import antifraud.model.enums.Roles;
import antifraud.model.request.UserRoleRequest;
import antifraud.model.request.UserStatusRequest;
import antifraud.model.response.UserResponse;
import antifraud.model.response.UserStatusChangeResponse;
import antifraud.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static antifraud.exception.ExceptionMessages.*;
import static antifraud.mappers.ModelMapper.userDTOtoUser;
import static antifraud.mappers.ModelMapper.userToUserResponse;

@Service
public class UserService {
    final UserRepository userRepository;
    final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    /**
     * @param userDTO
     * @return register user in the database
     * throwing 409 Conflict when username is already taken
     */
    public UserResponse registerUser(UserDTO userDTO) {
        User user = userDTOtoUser(userDTO);
        user.setPassword(encoder.encode(user.getPassword()));
        try {
            userRepository.save(user);
            if (user.getId() == 1) {
                user.setRole(Roles.ADMINISTRATOR);
                user.setAccountNonLocked(true);
                userRepository.updateRoleById(Roles.ADMINISTRATOR, user.getId());
            } else {
                user.setRole(Roles.MERCHANT);
                userRepository.updateRoleById(Roles.MERCHANT, user.getId());
            }
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, UNIQUE_USERNAME);
        }
        return userToUserResponse(user);
    }

    public DeletedUser deleteUser(String username) {
        if (userRepository.deleteUserByUsername(username) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND);
        }
        return new DeletedUser(username);
    }

    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(ModelMapper::userToUserResponse).toList();
    }

    public UserResponse updateUserRole(UserRoleRequest userRoleRequest) {
        checkUserRole(userRoleRequest.getRole());
        User user = userRepository.findByUsername(userRoleRequest.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
        if (user.getRole() != userRoleRequest.getRole()) {
            userRepository.updateRoleByUsername(userRoleRequest.getRole(), userRoleRequest.getUsername());
            user.setRole(userRoleRequest.getRole());
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, PRESENT_ROLE);
        }
        return userToUserResponse(user);
    }

    private void checkUserRole(Roles role) {
        if (!role.equals(Roles.SUPPORT) && !role.equals(Roles.MERCHANT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, FORBIDDEN_ROLE);
        }
    }

    public UserStatusChangeResponse changeUserStatus(UserStatusRequest userStatusRequest) {
        User user = userRepository.findByUsername(userStatusRequest.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (user.isAccountNonLocked() && userStatusRequest.getOperation().equals(AccountStatus.LOCK)) {
            user.setAccountNonLocked(false);
            userRepository.save(user);
            return new UserStatusChangeResponse("User " + user.getUsername() + " locked!");
        } else if (!user.isAccountNonLocked() && userStatusRequest.getOperation().equals(AccountStatus.UNLOCK)) {
            user.setAccountNonLocked(true);
            userRepository.save(user);
            return new UserStatusChangeResponse("User " + user.getUsername() + " unlocked!");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_REQUEST);
    }
}
