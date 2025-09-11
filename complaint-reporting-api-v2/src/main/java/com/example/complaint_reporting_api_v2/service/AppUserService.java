package com.example.complaint_reporting_api_v2.service;

import com.example.complaint_reporting_api_v2.entity.AppUser;
import com.example.complaint_reporting_api_v2.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AppUserService implements UserDetailsService{

    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository){
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new User(
                user.getUsername(),
                user.getPasswordHash(),
                user.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet())
        );
    }

}
