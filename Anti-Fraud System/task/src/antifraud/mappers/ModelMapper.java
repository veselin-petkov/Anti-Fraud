package antifraud.mappers;

import antifraud.model.DTO.UserDTO;
import antifraud.model.User;
import antifraud.model.UserResponse;
import org.springframework.context.annotation.Bean;

public class ModelMapper {

    public static User userDTOtoUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        return user;
    }

    public static UserResponse userToUserResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getUsername());
    }
}
