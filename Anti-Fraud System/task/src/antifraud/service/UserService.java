package antifraud.service;

import antifraud.model.response.delete.DeletedUser;
import antifraud.model.dto.UserDTO;
import antifraud.model.request.UserRoleRequest;
import antifraud.model.request.UserStatusRequest;
import antifraud.model.response.UserResponse;
import antifraud.model.response.UserStatusChangeResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserResponse registerUser(UserDTO userDTO);

    DeletedUser deleteUser(String username);

    List<UserResponse> listUsers();

    UserResponse updateUserRole(UserRoleRequest userRoleRequest);

    UserStatusChangeResponse changeUserStatus(UserStatusRequest userStatusRequest);
}
