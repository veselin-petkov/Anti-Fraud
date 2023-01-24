package antifraud.model.delete;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeletedUser {
    String username;
    public final String status = "Deleted successfully!";

    public DeletedUser(String username) {
        this.username = username;
    }
}