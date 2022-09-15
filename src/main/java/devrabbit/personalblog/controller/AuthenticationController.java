package devrabbit.personalblog.controller;

import devrabbit.personalblog.dto.AuthenticationRequest;
import devrabbit.personalblog.dto.AuthenticationResponse;
import devrabbit.personalblog.dto.SignUpRequestDto;
import devrabbit.personalblog.dto.SignUpResponseDto;
import devrabbit.personalblog.helper.JWTHelper;
import devrabbit.personalblog.security.UserDetail;
import devrabbit.personalblog.validator.AuthenticationRequestValidator;
import devrabbit.personalblog.validator.SignUpRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationRequestValidator authenticationRequestValidator
            = new AuthenticationRequestValidator();
    private final SignUpRequestValidator signUpRequestValidator = new SignUpRequestValidator();
    private final AuthenticationManager authenticationManager;
    private final JWTHelper jwtHelper;

    @PostMapping(path = "/sign-in")
    public ResponseEntity<?> signIn(@RequestBody AuthenticationRequest authenticationRequest) {
        authenticationRequestValidator.validate(authenticationRequest);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.username(), authenticationRequest.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        String token = jwtHelper.generate(authenticationRequest.username());
        return ResponseEntity.ok(new AuthenticationResponse(token,
                userDetail
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet())));
    }

    @PostMapping(path = "/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        signUpRequestValidator.validate(signUpRequestDto);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signUpRequestDto.email(), signUpRequestDto.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        String token = jwtHelper.generate(signUpRequestDto.username());
        return ResponseEntity.ok(new SignUpResponseDto(token,
                userDetail
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet())));
    }
}