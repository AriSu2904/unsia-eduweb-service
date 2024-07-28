package com.unsia.edu.services.implementation;

import com.unsia.edu.entities.EntityCredential;
import com.unsia.edu.entities.constant.ERole;
import com.unsia.edu.models.request.AuthenticationRequest;
import com.unsia.edu.models.request.RegisterRequest;
import com.unsia.edu.models.response.AuthenticationResponse;
import com.unsia.edu.models.response.RegisterResponse;
import com.unsia.edu.repositories.EntityCredentialRepository;
import com.unsia.edu.services.AuthService;
import com.unsia.edu.services.EntityCredentialService;
import com.unsia.edu.services.JwtService;
import com.unsia.edu.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final EntityCredentialService entityCredentialService;
    private final UserService userService;
    private final EntityCredentialRepository entityCredentialRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Override
    public RegisterResponse registerUser(RegisterRequest request) {
        EntityCredential userCredential = entityCredentialService
                .createCredential(request.getEmail(), request.getPassword(), ERole.ROLE_USER);

        userService.createUser(request.getFirstName(), request.getLastName(),
                userCredential.getEmail(), request.getPhoneNumber());

        return RegisterResponse.builder()
                .email(request.getEmail())
                .build();
    }

    @Override
    public RegisterResponse registerAdmin(RegisterRequest request) {
        EntityCredential userCredential = entityCredentialService
                .createCredential(request.getEmail(), request.getPassword(), ERole.ROLE_ADMIN);

        userService.createUser(request.getFirstName(), request.getLastName(),
                userCredential.getEmail(), request.getPhoneNumber());

        return RegisterResponse.builder()
                .email(request.getEmail())
                .build();
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {

        try {
            Authentication authenticated = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authenticated);

            EntityCredential entityCredential = entityCredentialRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials!"));

            String userToken = jwtService.generateToken(entityCredential);
            return AuthenticationResponse.builder()
                    .email(entityCredential.getEmail())
                    .role(entityCredential.getRole().name())
                    .token(userToken)
                    .build();
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect Email or Password!");
        }
    }
}
