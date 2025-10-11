package com.example.financialapp.infrastructure.web;

import com.example.financialapp.infrastructure.persistence.jpa.UserEntity;
import com.example.financialapp.infrastructure.persistence.jpa.UserRepository;
import com.example.financialapp.infrastructure.security.JwtService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.oauth2.login.UserInfoEndpointDsl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository usersRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    public AuthController(UserRepository users,
                          PasswordEncoder encoder,
                          JwtService jwt){
        this.usersRepo = users; this.encoder = encoder; this.jwt = jwt;
    }
    public record RegisterReq(@NotBlank String username,@NotBlank String password){}
    public record TokenRes(String token){}
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req){
        if(usersRepo.findByUsername(req.username).isPresent())
            return ResponseEntity.status(409).build();
        var u = new UserEntity();
        u.setId(UUID.randomUUID());
        u.setUsername(req.username);
        u.setPasswordHash(encoder.encode(req.password));
        u.setRoles(Set.of("ROLE_EMPLOYEE"));
        usersRepo.save(u);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/login")
    public ResponseEntity<TokenRes> login(@RequestBody RegisterReq req){
        var u = usersRepo.findByUsername(req.username).orElse(null);
        if(u == null || !encoder.matches(req.password, u.getPasswordHash()))
            return ResponseEntity.status(401).build();
        var token = jwt.issue(u.getUsername(), Map.of("uid", u.getId().toString(),
                "roles", u.getRoles()));
        return ResponseEntity.ok(new TokenRes(token));
    }

}
