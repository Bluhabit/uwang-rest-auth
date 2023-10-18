
package com.bluehabit.blu.common;

import com.bluehabit.blu.component.data.UserCredential;
import com.bluehabit.blu.component.data.UserCredentialRepository;
import com.bluehabit.blu.exception.UnAuthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractBaseService {
    @Autowired
    private ValidationUtil validation;
    @Autowired
    private ResourceBundleMessageSource i81n;

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    protected void validate(Object obj) {
        validation.validate(obj);
    }

    protected String translate(
        String key
    ) {
        return translate(key, "");
    }

    protected String translate(
        String key,
        String... params
    ) {
        try {
            return i81n.getMessage(key, params, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException noSuchMessageException) {
            return "<tidak dapat menampilkan pesan>";
        }
    }

    protected <T> T checkAccess(List<String> required, Function<UserDetails, T> onCheck) {
        return getAuthenticated(authentication -> {
            final List<String> authority = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            if (new HashSet<>(authority).containsAll(required)) {
                if (authentication.getPrincipal() instanceof UserDetails) {
                    return onCheck.apply((UserDetails) authentication.getPrincipal());
                }
                return onCheck.apply(null);
            } else {
                throw new UnAuthorizedException(HttpStatus.UNAUTHORIZED.value(), translate("default.permission.denied"));
            }
        });
    }

    protected <T> T getAuthenticatedUser(Function<UserCredential, T> userExist) {
        return getAuthenticatedUser(userExist, () -> {
            throw new UnAuthorizedException(HttpStatus.UNAUTHORIZED.value(), translate(""));
        });
    }

    protected <T> T getAuthenticatedUser(Function<UserCredential, T> userExist, Supplier<T> userNotFound) {
        return getAuthenticated(authentication -> {
            final UserDetails userDetails = ((UserDetails) authentication.getPrincipal());
            return userCredentialRepository
                .findByEmail(userDetails.getUsername())
                .map(userExist)
                .orElseGet(userNotFound);
        });
    }

    private <T> T getAuthenticated(Function<Authentication, T> callback) {
        final Authentication user = SecurityContextHolder.getContext().getAuthentication();
        return callback.apply(user);
    }
}
