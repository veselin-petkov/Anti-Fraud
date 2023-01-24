package antifraud.mappers;

import antifraud.model.DTO.StolenCardDTO;
import antifraud.model.DTO.SuspiciousIpDTO;
import antifraud.model.DTO.UserDTO;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.model.User;
import antifraud.model.UserResponse;

public class ModelMapper {

    public static User userDTOtoUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        return user;
    }
    public static UserResponse userToUserResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getUsername(), user.getRole());
    }

    public static SuspiciousIp suspiciousIpDTOtoSuspiciousIp(SuspiciousIpDTO ipDTO){
        SuspiciousIp ip =  new SuspiciousIp();
        ip.setIp(ipDTO.getIp());
        return ip;
    }
    public static StolenCard stolenCardDTOtoStolenCard(StolenCardDTO stolenCardDTO) {
        StolenCard stolenCard = new StolenCard();
        stolenCard.setNumber(stolenCardDTO.getNumber());
        return stolenCard;
    }
}
