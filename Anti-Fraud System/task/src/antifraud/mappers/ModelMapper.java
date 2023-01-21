package antifraud.mappers;

import antifraud.model.DTO.UserDTO;
import antifraud.model.User;
import antifraud.model.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public class ModelMapper {

    public static User userDTOtoUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        return user;
    }
    public static UserResponse userToUserResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getUsername(), user.getRole());
    }

    public static UserResponse optionalUserToUserResponse(Optional<User> user) {
        if (user.isPresent()){
            return new UserResponse(user.get().getId(), user.get().getName(), user.get().getUsername(), user.get().getRole());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
