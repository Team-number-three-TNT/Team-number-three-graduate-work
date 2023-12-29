package ru.skypro.homework.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Test
    void testGetAuthorizedUserReturnsExpectedUser() {
        // Arrange
        User expectedUser = new User();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(expectedUser));

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        UserService userService = new UserServiceImpl(userRepository, mock(UserMapper.class),
                mock(PasswordEncoder.class), mock(ImageService.class));

        // Act
        User result = userService.getAuthorizedUser();

        // Assert
        assertNotNull(result);
        assertEquals(expectedUser, result);
    }


    @Test
    void testUpdatePasswordSuccessfully() {
        // Arrange
        String email = "test@example.com";
        String currentPassword = "oldPassword";
        String newPassword = "newPassword";

        User mockUser = new User();
        mockUser.setPassword("encodedPassword");

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(encoder.matches(currentPassword, "encodedPassword")).thenReturn(true);

        UserServiceImpl userService = new UserServiceImpl(userRepository, mock(UserMapper.class),
                encoder, mock(ImageService.class));

        // Act
        boolean result = userService.updatePassword(email, currentPassword, newPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void testUpdatePasswordFailsForIncorrectCurrentPassword() {
        // Arrange
        String email = "test@example.com";
        String currentPassword = "incorrectPassword";
        String newPassword = "newPassword";

        User mockUser = new User();
        mockUser.setPassword("encodedPassword");

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(encoder.matches(currentPassword, "encodedPassword")).thenReturn(false);

        UserServiceImpl userService = new UserServiceImpl(userRepository, mock(UserMapper.class),
                encoder, mock(ImageService.class));

        // Act
        boolean result = userService.updatePassword(email, currentPassword, newPassword);

        // Assert
        assertFalse(result);
    }

}
