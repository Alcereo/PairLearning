package ru.alcereo.pairlearning.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alcereo.pairlearning.Service.SessionService;
import ru.alcereo.pairlearning.Service.TopicPredicateSide;
import ru.alcereo.pairlearning.Service.TopicService;
import ru.alcereo.pairlearning.Service.UserFront;

import javax.servlet.ServletException;
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

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

                if (TopicService.setTopicRow(
                        user,
                        id,
                        side,
                        predicate
                ))
                    resp.setStatus(200);
                break;
            default:
                resp.setStatus(400);
        }

    }

}
