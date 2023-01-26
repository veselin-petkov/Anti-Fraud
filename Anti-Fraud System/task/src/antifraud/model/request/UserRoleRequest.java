package antifraud.model.request;

import antifraud.model.enums.Roles;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class UserRoleRequest{

    @NotEmpty
    String username;
    @NotEmpty
    Roles role;

    @Override
    public String toString() {
        return "UserRoleRequest{" +
                "username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}
