package nextstep.jwp.vo;

import nextstep.jwp.model.FormData;

public class RequestBody {

    private static final String EACH_QUERY_STRING_DELIMITER = "&";

    private final FormData bodies;

    private RequestBody(FormData bodies) {
        this.bodies = bodies;
    }

    public static RequestBody from(String body) {
        FormData formData = FormData.from(body.split(EACH_QUERY_STRING_DELIMITER));
        return new RequestBody(formData);
    }

    public FormData getBodies() {
        return bodies;
    }
}
