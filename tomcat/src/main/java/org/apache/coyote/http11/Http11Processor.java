package org.apache.coyote.http11;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.RequestControllerMapper;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.Request;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static nextstep.jwp.vo.HeaderKey.CONTENT_LENGTH;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CONTENT_LENGTH_DELIMITER = ": ";
    private static final String EMPTY_BODY = "";
    private static final int EMPTY_BODY_LENGTH = -1;

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        log.info(Thread.currentThread().getName());
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            Request request = saveRequest(bufferedReader);

            Controller controller = RequestControllerMapper.mapByRequest(request);

            String response = controller.respond(request).getResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Request saveRequest(BufferedReader bufferedReader) throws IOException {
        List<String> requestStrings = new ArrayList<>();
        String now;
        while (!(now = bufferedReader.readLine()).isEmpty()) {
            requestStrings.add(now.trim());
        }
        int bodyLength = requestStrings.stream()
                .filter(each -> each.startsWith(CONTENT_LENGTH.getName()))
                .map(each -> Integer.parseInt(each.split(CONTENT_LENGTH_DELIMITER)[1]))
                .findFirst()
                .orElse(EMPTY_BODY_LENGTH);
        if (bodyLength > 0) {
            char[] buffer = new char[bodyLength];
            bufferedReader.read(buffer, 0, bodyLength);
            return Request.of(requestStrings, new String(buffer).trim());
        }
        return Request.of(requestStrings, EMPTY_BODY);
    }
}
