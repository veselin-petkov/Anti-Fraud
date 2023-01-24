package antifraud.model.delete;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeletedIp {
    String status;

    public DeletedIp(String ip) {
        this.status =  "IP "+ ip +" successfully removed!";
    }
}
