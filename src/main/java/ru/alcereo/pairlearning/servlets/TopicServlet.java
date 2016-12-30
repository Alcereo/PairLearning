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

import static ru.alcereo.pairlearning.servlets.ServletUtil.respError;

@WebServlet("/topic/api")
public class TopicServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(TopicServlet.class);


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
                    respError(resp,"Параметр ID задан не верно", 400);
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

                    respError(resp,"Ошибка сервиса тем", 400);
                }

                break;
            default:
                respError(resp,"Не распознано действие action", 400);

        }

    }

}
