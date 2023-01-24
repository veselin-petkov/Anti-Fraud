package antifraud.model.delete;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteCard {
    String status;

    public DeleteCard(String number) {
        this.status =  "Card "+ number +" successfully removed!";
    }

}
