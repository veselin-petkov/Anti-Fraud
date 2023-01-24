package antifraud.model.request;

import antifraud.model.Roles;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoleRequest{
    String username;
    Roles role;

    @Override
    public String toString() {
        return "UserRoleRequest{" +
                "username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}
