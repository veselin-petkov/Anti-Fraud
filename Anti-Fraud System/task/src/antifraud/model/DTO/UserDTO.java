package antifraud.model.DTO;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserDTO {
    @NotNull
    @NotEmpty
    String name;
    @NotNull
    @NotEmpty
    String username;
    @NotNull
    @NotEmpty
    String password;
}
