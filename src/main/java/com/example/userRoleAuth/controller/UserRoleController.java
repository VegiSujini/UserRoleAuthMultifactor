package com.example.userRoleAuth.controller;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.userRoleAuth.entity.JwtRequest;
import com.example.userRoleAuth.entity.JwtResponse;
import com.example.userRoleAuth.entity.Role;
import com.example.userRoleAuth.entity.User;
import com.example.userRoleAuth.repository.RoleRepository;
import com.example.userRoleAuth.repository.UserRepository;
import com.example.userRoleAuth.service.JwtUserDetailsService;
import com.example.userRoleAuth.service.OtpService;
import com.example.userRoleAuth.util.JwtTokenUtil;

@RestController
@CrossOrigin
public class UserRoleController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    @PostMapping("/authenticate")
    public JwtResponse createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        String otp = generateOtp();
        otpService.sendOtp(authenticationRequest.getUsername(), otp);

        return new JwtResponse(token, otp);
    }

    @PostMapping("/verifyOtp")
    public OtpResponse verifyOtp(@RequestBody OtpRequest otpRequest) {
        boolean isValid = jwtTokenUtil.validateOtpToken(otpRequest.getOtpToken(), otpRequest.getOtp());
        return new OtpResponse(isValid ? "OTP Verified" : "Invalid OTP");
    }

    @PostMapping("/register")
    public User saveUser(@RequestBody User user) throws Exception {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findById(1L).orElseThrow(() -> new Exception("Role not found")));
        user.setRoles(roles);
        return userRepository.save(user);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
