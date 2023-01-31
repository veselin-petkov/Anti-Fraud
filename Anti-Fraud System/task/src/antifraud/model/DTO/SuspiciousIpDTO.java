package antifraud.model.DTO;

import antifraud.validation.IpAddress;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SuspiciousIpDTO {
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    Long id;
    @IpAddress
    String ip;
}