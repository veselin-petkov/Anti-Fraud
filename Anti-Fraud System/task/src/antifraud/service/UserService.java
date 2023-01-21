package antifraud.service;

import antifraud.mappers.ModelMapper;
import antifraud.model.*;
import antifraud.model.DTO.UserDTO;
import antifraud.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static antifraud.mappers.ModelMapper.*;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;

    public UserResponse registerUser(UserDTO userDTO) {
        System.out.println(userDTO);
        User user = userDTOtoUser(userDTO);
        System.out.println(user);
        user.setPassword(encoder.encode(user.getPassword()));
        try {
            userRepository.save(user);
            if (user.getId() == 1) {
                user.setRole(Roles.ADMINISTRATOR);
                userRepository.updateRoleById(Roles.ADMINISTRATOR, user.getId());
            } else {
                user.setRole(Roles.MERCHANT);
                userRepository.updateRoleById(Roles.MERCHANT, user.getId());
            }
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        return userToUserResponse(user);
    }

    public DeletedUser deleteUser(String username) {
        if (userRepository.deleteUserByUsername(username) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new DeletedUser(username);
    }

    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(ModelMapper::userToUserResponse).toList();
    }

    public UserResponse updateUserRole(UserRoleRequest userRoleRequest) {
        checkUserRole(userRoleRequest.getRole());
        int updatedEntities = userRepository.updateRoleByUsername(userRoleRequest.getRole(), userRoleRequest.getUsername());
        System.out.println(updatedEntities);
        if (updatedEntities == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        else {
            User user = userRepository.findByUsername(userRoleRequest.getUsername());
            return userToUserResponse(user);
        }
    }

    private void checkUserRole(Roles role) {
        if (!role.equals(Roles.SUPPORT) && !role.equals(Roles.MERCHANT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
