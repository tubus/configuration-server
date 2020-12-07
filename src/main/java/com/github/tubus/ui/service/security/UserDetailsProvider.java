package com.github.tubus.ui.service.security;

import com.github.tubus.ui.data.dto.account.AccountRole;
import com.github.tubus.ui.data.dto.account.UserAccount;
import com.github.tubus.ui.data.repo.AccountRoleRepository;
import com.github.tubus.ui.data.repo.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsProvider implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;
    private final AccountRoleRepository accountRoleRepository;

    public UserDetailsProvider(final UserAccountRepository userAccountRepository,
                               final AccountRoleRepository accountRoleRepository) {
        this.userAccountRepository = userAccountRepository;
        this.accountRoleRepository = accountRoleRepository;
    }

    @Override
    public @Nullable UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAccount> userAccount = userAccountRepository.findOneByName(username);
        return userAccount.map(this::createUserDetails).orElse(null);
    }

    private UserDetails createUserDetails(UserAccount account) {
        List<AccountRole> roles = accountRoleRepository.findAllByAccount(account);
        return User.withUsername(account.getName())
                        .password(account.getPassword())
                        .roles(roles.stream().map(role -> role.getRole().getName()).toArray(String[]::new))
                        .build();
    }
}