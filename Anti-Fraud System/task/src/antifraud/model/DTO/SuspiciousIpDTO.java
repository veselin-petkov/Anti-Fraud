package antifraud.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class SuspiciousIpDTO {

    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    Long id;
    @NotEmpty
    String ip;
}