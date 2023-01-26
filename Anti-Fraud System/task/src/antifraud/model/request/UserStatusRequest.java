package antifraud.model.request;

import antifraud.model.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusRequest {
    @NotEmpty
    String username;
    @NotEmpty
    AccountStatus operation;
}
