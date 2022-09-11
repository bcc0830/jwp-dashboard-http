package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.Response;
import nextstep.jwp.model.User;
import nextstep.jwp.vo.LoginResult;
import nextstep.jwp.vo.ResponseStatus;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.util.Optional;
import java.util.UUID;

import static nextstep.jwp.vo.HeaderKey.*;

public class LoginService {

    private static final String SUCCESS_URL = "/index.html";
    private static final String FAIL_URL = "/401.html";

    private static final LoginService loginService = new LoginService();
    private static final String USER = "user";

    private LoginService() {
    }

    public static Response signIn(String account, String password) {
        return loginService.signInInternal(account, password);
    }

    private Response signInInternal(String account, String password) {
        LoginResult loginResult = generateResult(account, password);
        Response response = Response.from(ResponseStatus.FOUND);
        if (loginResult.getSession() != null) {
            response.addHeader(SET_COOKIE.getName(),
                    JSESSION_ID.getName() + "=" + loginResult.getSession().getId());

        }
        return response.addHeader(LOCATION.getName(), loginResult.getRedirectUrl())
                .addBlankLine();
    }

    private LoginResult generateResult(String account, String password) {
        if (account == null || password == null) {
            return new LoginResult(FAIL_URL);
        }
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty()) {
            return new LoginResult(FAIL_URL);
        }
        User user = optionalUser.get();
        if (!user.checkPassword(password)) {
            return new LoginResult(FAIL_URL);
        }
        return saveSession(user);
    }

    private LoginResult saveSession(User user) {
        SessionManager sessionManager = new SessionManager();
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(USER, user);
        sessionManager.add(session);
        return new LoginResult(SUCCESS_URL, session);
    }
}
