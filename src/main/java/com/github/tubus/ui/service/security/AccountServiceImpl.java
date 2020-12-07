package com.github.tubus.ui.service.security;

import com.github.tubus.ui.data.dto.account.AccountRole;
import com.github.tubus.ui.data.dto.account.Role;
import com.github.tubus.ui.data.dto.account.UserAccount;
import com.github.tubus.ui.data.exception.DuplicateSuperAdministratorForbidden;
import com.github.tubus.ui.data.exception.FieldVerificationFailedException;
import com.github.tubus.ui.data.exception.WrongDbStateException;
import com.github.tubus.ui.data.repo.AccountRoleRepository;
import com.github.tubus.ui.data.repo.RoleRepository;
import com.github.tubus.ui.data.repo.UserAccountRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl {

    private final PasswordEncoder encoder;
    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;

    public AccountServiceImpl(PasswordEncoder encoder, final UserAccountRepository userAccountRepository,
                              final RoleRepository roleRepository,
                              final AccountRoleRepository accountRoleRepository) {
        this.encoder = encoder;
        this.userAccountRepository = userAccountRepository;
        this.roleRepository = roleRepository;
        this.accountRoleRepository = accountRoleRepository;
    }

    public UserAccount createSuperUser(@NotBlank String name, @NotBlank String unEncryptedPassword) {
        if (userAccountRepository.superAdminExists()) {
            throw new DuplicateSuperAdministratorForbidden();
        }
        UserAccount userAccount = new UserAccount();
        userAccount.setName(name);
        userAccount.setPassword(encoder.encode(unEncryptedPassword));

        Optional<Role> superAdminRole = roleRepository.findByName("SUPER_ADMINISTRATOR");
        if (superAdminRole.isEmpty()) {
            throw new WrongDbStateException("Нет роли супер-администратора");
        }
        AccountRole superAdminAccountRole = new AccountRole();
        superAdminAccountRole.setRoleId(superAdminRole.get().getId());
        superAdminAccountRole.setRole(superAdminRole.get());

        UserAccount savedAccount = userAccountRepository.save(userAccount);

        saveAccountRole(savedAccount, superAdminAccountRole);

        return savedAccount;
    }

    public UserAccount createAccount(@NotBlank String name, @NotNull List<Role> roles,
                                     @NotBlank String unEncryptedPassword) {
        UserAccount userAccount = new UserAccount();
        if (StringUtils.isBlank(name)) {
            throw new FieldVerificationFailedException("Выбрано пустое имя");
        }
        userAccount.setName(name);
        if (StringUtils.isBlank(unEncryptedPassword)) {
            throw new FieldVerificationFailedException("Задан пустой пароль");
        }
        userAccount.setPassword(encoder.encode(unEncryptedPassword));

        List<AccountRole> creatingRoles = prepareRoles(roles);

        UserAccount savedAccount = userAccountRepository.save(userAccount);

        creatingRoles.forEach(creatingRole -> saveAccountRole(savedAccount, creatingRole));

        return savedAccount;
    }

    private void saveAccountRole(UserAccount savedAccount, AccountRole creatingRole) {
        creatingRole.setAccount(savedAccount);
        creatingRole.setAccountId(savedAccount.getId());
        accountRoleRepository.save(creatingRole);
    }

    private List<AccountRole> prepareRoles(List<Role> roles) {
        List<AccountRole> creatingRoles = new ArrayList<>();
        for (Role role : roles) {
            if ("SUPER_ADMINISTRATOR".equals(role.getName())) {
                throw new DuplicateSuperAdministratorForbidden();
            }
            AccountRole accountRole = new AccountRole();
            accountRole.setRoleId(role.getId());
            accountRole.setRole(role);
        }
        return creatingRoles;
    }
}