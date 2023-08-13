
package com.bluehabit.eureka.common;

import com.bluehabit.eureka.exception.UnAuthorizedException;
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

public abstract class AbstractBaseService {
    @Autowired
    private ValidationUtil validation;
    @Autowired
    private ResourceBundleMessageSource i81n;

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
        final Authentication user = SecurityContextHolder.getContext().getAuthentication();

        final List<String> authority = user
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        if (new HashSet<>(authority).containsAll(required)) {
            if (user.getPrincipal() instanceof UserDetails) {
                return onCheck.apply((UserDetails) user.getPrincipal());
            }
            return onCheck.apply(null);
        } else {
            throw new UnAuthorizedException(HttpStatus.UNAUTHORIZED.value(), translate("default.permission.denied"));
        }
    }
}
