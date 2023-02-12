package antifraud.mappers;

import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.model.Transaction;
import antifraud.model.User;
import antifraud.model.dto.StolenCardDTO;
import antifraud.model.dto.SuspiciousIpDTO;
import antifraud.model.dto.UserDTO;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.UserResponse;

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

    public static Transaction transactionRequestToTransaction(TransactionRequest transactionRequest){
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setIp(transactionRequest.getIp());
        transaction.setNumber(transactionRequest.getNumber());
        transaction.setRegion(transactionRequest.getRegion());
        transaction.setDate(transactionRequest.getDate());
        return transaction;
    }
}
