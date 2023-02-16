package antifraud.service.impl;

import antifraud.model.User;
import antifraud.model.dto.UserDTO;
import antifraud.repository.UserRepository;
import antifraud.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserService userService;
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder encoder;
    private antifraud.model.dto.UserDTO userDTO;
    private User user;
    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @BeforeEach
    void setup() {
        this.user = new User();
        user.setName("JohnDoe");
        user.setUsername("johndoe1");

        this.userDTO = new UserDTO();
        userDTO.setName("JohnDoe");
        userDTO.setUsername("johndoe1");

        this.userService = userServiceImpl;
        this.stringCaptor = ArgumentCaptor.forClass(String.class);
    }

    @Test
    void WhenRegisterNewUserThenReturnRegisteredUser() {
        given(userRepository.save(user)).willReturn(user);

        Optional<> customUser = userService.registerUser(userDTO);

        assertEquals(Optional.of(userDTO), customUser);
    }

    @Test
    void WhenRegisterNewUserThenInvokeAllInnerMethods() {
        given(userRepository.save(user)).willReturn(user);

        userService.registerUser(userDTO);

        then(encoder).should(times(1)).encode(any());
        verifyNoMoreInteractions(encoder);

        then(userRepository).should(times(1)).count();
        then(userRepository).should(times(1)).existsByUsername(any());
        then(userRepository).should(times(1)).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void WhenRegisterNewUserThenPasswordToBeEncodedIsSameAsProvidedByUser() {
        given(userRepository.save(user)).willReturn(user);
        String expectedPassword = userDTO.getPassword();

        userService.registerUser(userDTO);

        then(encoder).should(times(1)).encode(stringCaptor.capture());
        verifyNoMoreInteractions(encoder);

        String actualArgumentPassword = stringCaptor.getValue();
        assertEquals(expectedPassword, actualArgumentPassword);
    }

    @Test
    void WhenRegisterFirstUserThenRoleIsAdministrator() {
        given(userRepository.save(userDTO)).willReturn(userDTO);
        UserRole expectedRole = UserRole.ADMINISTRATOR;

        antifraud.model.dto.UserDTO customUser = userService.registerUser(userDTO).get();
        UserRole resultRole = customUser.getRole();

        assertEquals(expectedRole, resultRole);
    }

    @Test
    void WhenRegisterFirstUserThenAccessIsUnlocked() {
        given(userRepository.save(userDTO)).willReturn(userDTO);
        UserAccess expectedAccess = UserAccess.UNLOCK;

        antifraud.model.dto.UserDTO customUser = userService.registerUser(userDTO).get();
        UserAccess resultAccess = customUser.getAccess();

        assertEquals(expectedAccess, resultAccess);
    }

    @Test
    void WhenRegisterSecondUserThenRoleIsMerchant() {
        antifraud.model.dto.UserDTO secondUser = antifraud.model.dto.UserDTOFactory.create("JaneDoe", "jane333doe", "secretz");
        given(userRepository.save(any()))
                .willReturn(userDTO)
                .willReturn(secondUser);
        given(userRepository.count())
                .willReturn(0L)
                .willReturn(1L);
        UserRole expectedRoleFirstUser = UserRole.ADMINISTRATOR;
        UserRole expectedRoleSecondUser = UserRole.MERCHANT;

        antifraud.model.dto.UserDTO customUser = userService.registerUser(userDTO).get();
        UserRole resultRoleFirstUser = customUser.getRole();
        antifraud.model.dto.UserDTO secondUserDTO = userService.registerUser(secondUser).get();
        UserRole resultRoleSecondUser = antifraud.model.dto.UserDTO.getRole();

        assertAll(
                () -> assertEquals(expectedRoleFirstUser, resultRoleFirstUser),
                () -> assertEquals(expectedRoleSecondUser, resultRoleSecondUser)
        );
    }

    @Test
    void WhenRegisterSecondUserThenAccessIsLocked() {
        antifraud.model.dto.UserDTO secondUser = antifraud.model.dto.UserDTOFactory.create("JaneDoe", "jane333doe", "secretz");
        given(userRepository.save(any()))
                .willReturn(userDTO)
                .willReturn(secondUser);
        given(userRepository.count())
                .willReturn(0L)
                .willReturn(1L);
        UserAccess expectedAccessFirstUser = UserAccess.UNLOCK;
        UserAccess expectedAccessSecondUser = UserAccess.LOCK;

        antifraud.model.dto.UserDTO customUser = userService.registerUser(userDTO).get();
        UserAccess resultAccessFirstUser = customUser.getAccess();
        antifraud.model.dto.UserDTO secondUserDTO = userService.registerUser(secondUser).get();
        UserAccess resultAccessSecondUser = antifraud.model.dto.UserDTO.getAccess();

        assertAll(
                () -> assertEquals(expectedAccessFirstUser, resultAccessFirstUser),
                () -> assertEquals(expectedAccessSecondUser, resultAccessSecondUser)
        );
    }

    @Test
    void WhenRegisterTwoUsersThenUseCorrectUsernamesToFindThem() {
        antifraud.model.dto.UserDTO secondUser = antifraud.model.dto.UserDTOFactory.create("JaneDoe", "jane333doe", "secretz");
        given(userRepository.save(any()))
                .willReturn(userDTO)
                .willReturn(secondUser);
        List<String> expectedValues = Arrays.asList("johndoe1", "jane333doe");

        userService.registerUser(userDTO);
        userService.registerUser(secondUser);

        then(userRepository).should(times(2))
                .existsByUsername(stringCaptor.capture());
        List<String> resultValues = stringCaptor.getAllValues();
        assertEquals(expectedValues, resultValues);
    }

    @Test
    void WhenRegisterNewUserTwiceThenFirstTimeReturnUserAndSecondTimeReturnEmpty() {
        given(userRepository.existsByUsername(userDTO.getUsername()))
                .willReturn(false)
                .willReturn(true);
        given(userRepository.save(userDTO)).willReturn(userDTO);
        Optional<antifraud.model.dto.UserDTO> expectedFirstSave = Optional.of(userDTO);

        Optional<antifraud.model.dto.UserDTO> firstTimeRegister = userService.registerUser(userDTO);
        Optional<antifraud.model.dto.UserDTO> secondTimeRegister = userService.registerUser(userDTO);

        assertAll(
                () -> assertEquals(expectedFirstSave, firstTimeRegister),
                () -> assertThat(secondTimeRegister).isEmpty());
    }

    @Test
    void WhenRegisterExistingUserThenReturnEmpty() {
        given(userRepository.existsByUsername(any()))
                .willReturn(true);

        Optional<antifraud.model.dto.UserDTO> customUser = userService.registerUser(userDTO);

        assertThat(customUser).isEmpty();
    }

    @Test
    void WhenRegisterExistingUserThenNotInvokeSave() {
        given(userRepository.existsByUsername(any()))
                .willReturn(true);

        userService.registerUser(userDTO);

        then(userRepository).should(never()).save(any());
    }

    @Test
    void WhenRepoIsEmptyThenGetUsersReturnEmptyCollection() {
        List<antifraud.model.dto.UserDTO> users = userService.getUsers();

        assertThat(users).isEmpty();
    }

    @Test
    void WhenRepoIsNotEmptyThenGetUsersReturnCollection() {
        List<antifraud.model.dto.UserDTO> customUsers = Arrays.asList(userDTO,
                antifraud.model.dto.UserDTOFactory.create("JaneDoe", "jane333doe", "secretz"));
        given(this.userRepository.findAll())
                .willReturn(customUsers);
        int expectedSize = 2;

        List<antifraud.model.dto.UserDTO> users = userService.getUsers();
        int resultSize = users.size();

        assertEquals(expectedSize, resultSize);
    }

    @Test
    void WhenDeletingNonExistentUserThenThrowException() {
        doThrow(UsernameNotFoundException.class)
                .when(userRepository).findByUsernameIgnoreCase(any());

        Executable executable = () -> userService.deleteUser(any());

        assertThrows(UsernameNotFoundException.class, executable);
    }

    @Test
    void WhenDeleteNonExistentUserThenThrowExceptionAndDoNotInvokeDelete() {
        doThrow(UsernameNotFoundException.class)
                .when(userRepository).findByUsernameIgnoreCase(any());

        try {
            userService.deleteUser(any());
        } catch (UsernameNotFoundException ignored) {
        }

        then(userRepository).should(never()).deleteById(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void WhenDeletingExistentUserThenDoesNotThrowException() {
        given(userRepository.findByUsernameIgnoreCase(any()))
                .willReturn(Optional.of(userDTO));

        Executable executable = () -> userService.deleteUser(any());

        assertDoesNotThrow(executable);
    }

    @Test
    void WhenDeletingExistentUserThenDoNothing() {
        given(userRepository.findByUsernameIgnoreCase(any()))
                .willReturn(Optional.of(userDTO));
        doNothing().when(userRepository).deleteById(any());

        userService.deleteUser(any());

        then(userRepository).should(times(1)).deleteById(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void WhenChangingRoleToNonExistentUserThenThrowException() {
        Executable executable = () -> userService.changeUserRole(userDTO);

        assertThrows(UsernameNotFoundException.class, executable);
    }

    @Test
    void WhenChangeRoleWithSameRoleThenThrowException() {
        userDTO.setRole(UserRole.MERCHANT);
        given(userRepository.findByUsernameIgnoreCase(any()))
                .willReturn(Optional.of(userDTO));

        Executable executable = () -> userService.changeUserRole(userDTO);

        assertThrows(AlreadyProvidedException.class, executable);
    }

    @Test
    void WhenChangeAdministratorRoleThenThrowException() {
        antifraud.model.dto.UserDTO secondUser = antifraud.model.dto.UserDTOFactory.create("JaneDoe", "jane333doe", "secretz");
        secondUser.setRole(UserRole.MERCHANT);
        userDTO.setRole(UserRole.ADMINISTRATOR);
        given(userRepository.findByUsernameIgnoreCase(any()))
                .willReturn(Optional.of(userDTO));

        Executable executable = () -> userService.changeUserRole(secondUser);

        assertThrows(ExistingAdministratorException.class, executable);
    }

    @Test
    void WhenChangingNonConflictRoleThenTheRoleWillBeChanged() {
        antifraud.model.dto.UserDTO userInDB = antifraud.model.dto.UserDTOFactory.create("JohnDoe", "johndoe1", "secret");
        userInDB.setRole(UserRole.MERCHANT);
        userDTO.setRole(UserRole.SUPPORT);
        given(userRepository.findByUsernameIgnoreCase(any()))
                .willReturn(Optional.of(userInDB));
        UserRole expectedRole = UserRole.SUPPORT;

        userService.changeUserRole(userDTO);
        UserRole resultRole = userInDB.getRole();

        assertEquals(expectedRole, resultRole);
    }

    @Test
    void WhenChangeUserRoleThenInvokeSave() {
        antifraud.model.dto.UserDTO userInDB = antifraud.model.dto.UserDTOFactory.create("JohnDoe", "johndoe1", "secret");
        userInDB.setRole(UserRole.MERCHANT);
        userDTO.setRole(UserRole.SUPPORT);
        given(userRepository.findByUsernameIgnoreCase(any()))
                .willReturn(Optional.of(userInDB));

        userService.changeUserRole(userDTO);

        then(userRepository).should(times(1)).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void WhenChangingAccessToNonExistentUserThenThrowException() {
        Executable executable = () -> userService.grantAccess(userDTO);

        assertThrows(UsernameNotFoundException.class, executable);
    }

    @Test
    void WhenChangingAccessToAdministratorThenThrowException() {
        userDTO.setRole(UserRole.ADMINISTRATOR);
        given(userRepository.findByUsernameIgnoreCase(any()))
                .willReturn(Optional.of(userDTO));

        Executable executable = () -> userService.grantAccess(userDTO);

        assertThrows(AccessViolationException.class, executable);
    }

    @Test
    void WhenChangeAccessThenAccessWillBeChanged() {
        antifraud.model.dto.UserDTO userInDB = antifraud.model.dto.UserDTOFactory.create("JohnDoe", "johndoe1", "secret");
        userInDB.setAccess(UserAccess.LOCK);
        userDTO.setAccess(UserAccess.UNLOCK);
        given(userRepository.findByUsernameIgnoreCase(any()))
                .willReturn(Optional.of(userInDB));
        UserAccess expectedAccessLevel = UserAccess.UNLOCK;

        userService.grantAccess(userDTO);
        UserAccess resultAccess = userInDB.getAccess();

        assertEquals(expectedAccessLevel, resultAccess);
    }

    @Test
    void WhenChangeAccessThenInvokeSave() {
        antifraud.model.dto.UserDTO userInDB = new antifraud.model.dto.UserDTO();
        "JohnDoe", "johndoe1", "secret";
        userInDB.setAccess(UserAccess.LOCK);
        userDTO.setAccess(UserAccess.UNLOCK);
        given(userRepository.findByUsernameIgnoreCase(any()))
                .willReturn(Optional.of(userInDB));

        userService.grantAccess(userDTO);

        then(userRepository).should(times(1)).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void WhenRetrievingNonExistentUsernameThenThrowException() {
        Executable executable = () -> userService.retrieveRealUsername(any());

        assertThrows(UsernameNotFoundException.class, executable);
    }

    @Test
    void WhenRetrievingUsernameThenReturnRealUsername() {
        antifraud.model.dto.UserDTO userInDB = antifraud.model.dto.UserDTOFactory.create("JohnDoe", "JoHnDoe1", "secret");
        String expectedUsername = "JoHnDoe1";
        given(userRepository.findByUsernameIgnoreCase(expectedUsername))
                .willReturn(Optional.of(userInDB));

        userService.retrieveRealUsername(expectedUsername);
        String resultUsername = userInDB.getUsername();

        assertEquals(expectedUsername, resultUsername);
    }

    @Test
    void WhenLoadByNonExistentUsernameThenThrowException() {
        Executable executable = () -> userService.loadUserByUsername(any());

        assertThrows(UsernameNotFoundException.class, executable);
    }

    @Test
    void WhenLoadByUsernameThenReturnUserPrincipal() {
        userDTO.setRole(UserRole.MERCHANT);
        UserPrincipal userPrincipal = new UserPrincipal(userDTO);
        given(userRepository.findByUsernameIgnoreCase(any()))
                .willReturn(Optional.of(userDTO));

        UserDetails userDetails = userService.loadUserByUsername(any());

        assertThat(userPrincipal).usingRecursiveComparison().isEqualTo(userDetails);
    }
}