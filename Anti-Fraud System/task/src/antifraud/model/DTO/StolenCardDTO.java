package antifraud.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class StolenCardDTO {
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    Long id;
    @NotEmpty
    String number;
}
