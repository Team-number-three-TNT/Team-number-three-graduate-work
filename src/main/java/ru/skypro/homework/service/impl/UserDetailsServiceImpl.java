package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.skypro.homework.dto.UserPrincipalDTO;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.entity.UserPrincipal;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        UserPrincipalDTO userDTO = userMapper.toUserPrincipalDTO(user);
        if (userDTO == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserPrincipal(userDTO);
    }
}
