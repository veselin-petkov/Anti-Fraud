package antifraud.model;

import antifraud.model.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    Long id;
    String name;
    String username;
    Roles role;
}
