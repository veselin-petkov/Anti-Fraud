package antifraud.model.delete;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteCard(@JsonProperty("status") String number) {
    public DeleteCard(String number) {
        this.number =  "Card "+ number +" successfully removed!";
    }
}
