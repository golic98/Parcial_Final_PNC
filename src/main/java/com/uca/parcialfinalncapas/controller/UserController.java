package com.uca.parcialfinalncapas.controller;

import com.uca.parcialfinalncapas.dto.request.LoginRequest;
import com.uca.parcialfinalncapas.dto.request.RegisterRequest;
import com.uca.parcialfinalncapas.dto.request.UserCreateRequest;
import com.uca.parcialfinalncapas.dto.request.UserUpdateRequest;
import com.uca.parcialfinalncapas.dto.response.GeneralResponse;
import com.uca.parcialfinalncapas.dto.response.LoginResponse;
import com.uca.parcialfinalncapas.dto.response.UserResponse;
import com.uca.parcialfinalncapas.entities.User;
import com.uca.parcialfinalncapas.service.UserService;
import com.uca.parcialfinalncapas.service.impl.UserServiceImpl;
import com.uca.parcialfinalncapas.utils.ResponseBuilderUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @GetMapping("/all")
    public ResponseEntity<GeneralResponse> getAllUsers() {
        List<UserResponse> users = userService.findAll();

        return ResponseBuilderUtil.buildResponse(
                "Usuarios obtenidos correctamente",
                users.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK,
                users
        );
    }

    @GetMapping("/{correo}")
    public ResponseEntity<GeneralResponse> getUserByCorreo(@PathVariable String correo) {
        UserResponse user = userService.findByCorreo(correo);
        return ResponseBuilderUtil.buildResponse("Usuario encontrado", HttpStatus.OK, user);
    }

    @PostMapping
    public ResponseEntity<GeneralResponse> createUser(@Valid @RequestBody UserCreateRequest user) {
        UserResponse createdUser = userService.save(user);
        return ResponseBuilderUtil.buildResponse("Usuario creado correctamente", HttpStatus.CREATED, createdUser);
    }

    @PutMapping
    public ResponseEntity<GeneralResponse> updateUser(@Valid @RequestBody UserUpdateRequest user) {
        UserResponse updatedUser = userService.update(user);
        return ResponseBuilderUtil.buildResponse("Usuario actualizado correctamente", HttpStatus.OK, updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseBuilderUtil.buildResponse("Usuario eliminado correctamente", HttpStatus.OK, null);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> addUser(@RequestBody RegisterRequest registerRequest) {
        try {
            User userSave = userServiceImpl.register(registerRequest);
            return ResponseEntity.ok(userSave);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo agregar al usuario" + e.getMessage());
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> userLogin(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = userServiceImpl.login(loginRequest);
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
