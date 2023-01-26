package antifraud.model.delete;


public record DeletedUser(String username,String status) {
    public DeletedUser(String username) {
        this(username,"Deleted successfully!");
    }

}