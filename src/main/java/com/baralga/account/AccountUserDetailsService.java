package com.baralga.account;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

    private final @NonNull AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return User.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .tenantId(account.getTenantId())
                .build();
    }
}
