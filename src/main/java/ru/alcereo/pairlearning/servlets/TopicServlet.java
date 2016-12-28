package ru.alcereo.pairlearning.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.TopicService;
import ru.alcereo.pairlearning.Service.exeptions.TopicServiceException;
import ru.alcereo.pairlearning.Service.models.TopicPredicateSide;
import ru.alcereo.pairlearning.Service.models.UserFront;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/topic/api")
public class TopicServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(TopicServlet.class);

//    private final SessionService sessionService = new SessionService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        log.debug("Запрос с параметрами: {}", req.getParameterMap());

        String action = req.getParameter("action");
        UserFront user = (UserFront) req.getAttribute("user");

        switch (action) {
            case "concrete":

                TopicPredicateSide side = null;

                switch (req.getParameter("value")) {
                    case "learn":
                        side = TopicPredicateSide.LEARN;
                        break;
                    case "teach":
                        side = TopicPredicateSide.TEACH;
                }

                boolean predicate = "true".equals(req.getParameter("predicate"));

                Long id = null;

                try {
                    id = new Long(req.getParameter("id"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    resp.setStatus(400);
                }

                try {
                    TopicService.setTopicRow(
                            user,
                            id,
                            side,
                            predicate
                    );

                    resp.setStatus(200);
                } catch (TopicServiceException e) {
                    try {
                        resp.setCharacterEncoding("utf-16");
                        resp.getWriter().write("Ошибка сервиса тем ");
                        resp.setStatus(400);
                    } catch (IOException e1) {
                        log.warn(e1.getLocalizedMessage());
                        resp.setStatus(400);
                    }
                }

                break;
            default:
                try {
                    resp.setCharacterEncoding("utf-16");
                    resp.getWriter().write("Не распознано действие action");
                    resp.setStatus(400);
                } catch (IOException e) {
                    log.warn(e.getLocalizedMessage());
                    resp.setStatus(400);
                }
        }

    }

}
