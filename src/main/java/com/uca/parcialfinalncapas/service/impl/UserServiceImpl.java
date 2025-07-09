package com.uca.parcialfinalncapas.service.impl;

import com.uca.parcialfinalncapas.configuration.JwtUtils;
import com.uca.parcialfinalncapas.dto.request.RegisterRequest;
import com.uca.parcialfinalncapas.dto.request.UserCreateRequest;
import com.uca.parcialfinalncapas.dto.request.UserUpdateRequest;
import com.uca.parcialfinalncapas.dto.request.LoginRequest;
import com.uca.parcialfinalncapas.dto.response.LoginResponse;
import com.uca.parcialfinalncapas.dto.response.UserResponse;
import com.uca.parcialfinalncapas.entities.User;
import com.uca.parcialfinalncapas.exceptions.UserNotFoundException;
import com.uca.parcialfinalncapas.repository.UserRepository;
import com.uca.parcialfinalncapas.service.UserService;
import com.uca.parcialfinalncapas.utils.mappers.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public UserResponse findByCorreo(String correo) {
        return UserMapper.toDTO(userRepository.findByCorreo(correo)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con correo: " + correo)));
    }

    @Override
    public UserResponse save(UserCreateRequest user) {

        if (userRepository.findByCorreo(user.getCorreo()).isPresent()) {
            throw new UserNotFoundException("Ya existe un usuario con el correo: " + user.getCorreo());
        }

        return UserMapper.toDTO(userRepository.save(UserMapper.toEntityCreate(user)));
    }

    @Override
    public UserResponse update(UserUpdateRequest user) {
        if (userRepository.findById(user.getId()).isEmpty()) {
            throw new UserNotFoundException("No se encontró un usuario con el ID: " + user.getId());
        }

        return UserMapper.toDTO(userRepository.save(UserMapper.toEntityUpdate(user)));
    }

    @Override
    public void delete(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException("No se encontró un usuario con el ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<UserResponse> findAll() {
        return UserMapper.toDTOList(userRepository.findAll());
    }

    public User register(RegisterRequest registerRequest) {
        try {
            User user = new User();
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getCorreo(),
                            loginRequest.getPassword()
                    )
            );
            User user = userRepository
                    .findByCorreo(loginRequest.getCorreo())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            String jwt = jwtUtils.generateToken((UserDetails) user);
            String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), (UserDetails) user);
            return new LoginResponse(jwt, refreshToken, user.getCorreo(), user.getNombreRol());
        } catch (Exception e) {
            throw new RuntimeException("Credenciales inválidas");
        }
    }
}
