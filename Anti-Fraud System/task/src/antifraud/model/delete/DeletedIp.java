package antifraud.model.delete;


import com.fasterxml.jackson.annotation.JsonProperty;

public record DeletedIp(@JsonProperty("status") String ip) {

    public DeletedIp(String ip) {
        this.ip =  "IP "+ ip +" successfully removed!";
    }
}
