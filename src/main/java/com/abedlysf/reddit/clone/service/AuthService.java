package com.abedlysf.reddit.clone.service;

import com.abedlysf.reddit.clone.dto.AuthenticationResponse;
import com.abedlysf.reddit.clone.dto.LoginRequest;
import com.abedlysf.reddit.clone.dto.RegisterRequest;
import com.abedlysf.reddit.clone.exceptions.SpringRedditException;
import com.abedlysf.reddit.clone.model.NotificationEmail;
import com.abedlysf.reddit.clone.model.User;
import com.abedlysf.reddit.clone.model.VerificationToken;
import com.abedlysf.reddit.clone.repository.UserRepository;
import com.abedlysf.reddit.clone.repository.VerificationTokenRepository;
import com.abedlysf.reddit.clone.security.JwtProvider;
import lombok.AllArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthService {

    // to do Autowiring by construction (it's much better than field mapping )
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private  final MailService mailService;
    private  final AuthenticationManager authenticationManager;
    private  final JwtProvider jwtProvider;

    @Transactional

    public void signup(RegisterRequest registerRequest){
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);
        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("please Activate your Account"
                ,user.getEmail()
                ,"Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));

    }
    private String generateVerificationToken(User user){
        // generate a random token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }


    public void verifyAccount(String token) {
        Optional<VerificationToken> byToken = verificationTokenRepository.findByToken(token);
        byToken.orElseThrow(()-> new SpringRedditException("invalid token"));
        fetchUserAndEnable(byToken.get());
    }

    @Transactional
    public  void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found with name - " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {

        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));
        // storing the authentication object inside the security context

        SecurityContextHolder.getContext().setAuthentication(authenticate); // further we can just look in the context to see whether the user is login or not
         // generating the token
        String token = jwtProvider.generateToken(authenticate);

        return new AuthenticationResponse(token,loginRequest.getUsername());


    }
}
