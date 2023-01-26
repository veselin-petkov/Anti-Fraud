package antifraud.model.DTO;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class UserDTO {
    @NotEmpty
    String name;
    @NotEmpty
    String username;
    @NotEmpty
    String password;

    @Override
    public String toString() {
        return "UserDTO{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
