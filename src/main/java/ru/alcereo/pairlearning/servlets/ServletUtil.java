package ru.alcereo.pairlearning.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ServletUtil {

    private static final Logger log = LoggerFactory.getLogger(ServletUtil.class);

    static void respError(HttpServletResponse response, String message, int status){
        try {
            log.warn(message);
            response.setCharacterEncoding("utf-16");
            response.getWriter().write(
                    message
            );
            response.setStatus(status);
        } catch (IOException e) {
            log.warn(e.getLocalizedMessage());
            response.setStatus(400);
        }
    }

}
