package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.vo.Response;
import nextstep.jwp.vo.ResponseStatus;

public class RegisterService {

    private static final String SUCCESS_URL = "/index.html";
    private static final String FAIL_URL = "/401.html";

    private static final RegisterService registerService = new RegisterService();
    private static final String LOCATION = "Location";

    private RegisterService() {
    }

    public static String signUp(String account, String password, String email) {
        return registerService.signUpInternal(account, password, email);
    }

    public String signUpInternal(String account, String password, String email) {
        Response response = Response.from(ResponseStatus.FOUND);
        try {
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            response.addHeader(LOCATION, SUCCESS_URL)
                    .addBlankLine();
            return response.getResponse();
        }
        catch (IllegalArgumentException e) {
            response.addHeader(LOCATION, FAIL_URL)
                    .addBlankLine();
            return response.getResponse();
        }
    }
}
