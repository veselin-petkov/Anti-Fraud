package antifraud.service;

import antifraud.mappers.ModelMapper;
import antifraud.model.DTO.UserDTO;
import antifraud.model.DeletedUser;
import antifraud.model.User;
import antifraud.model.UserResponse;
import antifraud.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static antifraud.mappers.ModelMapper.userDTOtoUser;
import static antifraud.mappers.ModelMapper.userToUserResponse;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;

    public UserResponse registerUser(UserDTO userDTO) {
        User user = userDTOtoUser(userDTO);
        user.setPassword(encoder.encode(user.getPassword()));
        try {
            userRepository.save(user);
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
}
