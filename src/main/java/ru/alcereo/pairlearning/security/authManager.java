package ru.alcereo.pairlearning.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import ru.alcereo.exoption.Option;
import ru.alcereo.pairlearning.Service.models.User;
import ru.alcereo.pairlearning.Service.SessionService;
import ru.alcereo.pairlearning.Service.models.AuthorizationData;

import java.util.ArrayList;
import java.util.List;

public class authManager  implements AuthenticationProvider{

    private static final Logger log = LoggerFactory.getLogger(authManager.class);

    private SessionService sessionService;

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Authentication result = null;

        log.debug("Авторизирую пользователя");

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        String sessionId = ((WebAuthenticationDetails)authentication.getDetails()).getSessionId();

        Option<User,?> authResultOpt = sessionService.userAuthorization(new AuthorizationData(
                name,
                password,
                sessionId
        ));

        if (!authResultOpt.isException()){

            if (!authResultOpt.isNone()){

                List<GrantedAuthority> grantedAuths = new ArrayList<>();
                grantedAuths.add(new SimpleGrantedAuthority("FULLY"));
                grantedAuths.add(new SimpleGrantedAuthority("ANONYMOUS"));
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        authResultOpt.getOrElse(null),
                        password,
                        grantedAuths);
                result = auth;
            }
        }

        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
