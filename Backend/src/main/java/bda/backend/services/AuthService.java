package bda.backend.services;

import bda.backend.config.LoginRequest;
import bda.backend.config.RegisterRequest;
import bda.backend.config.TokenResponse;
import bda.backend.entities.UsuarioEntity;
import bda.backend.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public void register(final RegisterRequest request) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point direccion = geometryFactory.createPoint(new Coordinate(request.longitude(), request.latitude()));
        direccion.setSRID(4326);

        var cliente = UsuarioEntity.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .location(direccion)
                .build();

        usuarioRepository.save(cliente);
    }

    public TokenResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        var cliente = usuarioRepository.findByUsername(request.username());
        var jwtToken = jwtService.generateToken(cliente);
        return new TokenResponse(jwtToken, cliente.getId());
    }

    public TokenResponse refreshToken( String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Refresh Token");
        }
        final String refreshToken = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail == null) {
            throw new IllegalArgumentException("Invalid Refresh Token");
        }
        final UsuarioEntity cliente = usuarioRepository.findByUsername(userEmail);
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid Refresh Token");
        }
        var jwtToken = jwtService.generateToken(cliente);
        return new TokenResponse(jwtToken, cliente.getId());
    }
}