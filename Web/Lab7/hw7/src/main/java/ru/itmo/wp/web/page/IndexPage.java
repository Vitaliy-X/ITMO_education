package ru.itmo.wp.web.page;

import com.google.common.base.Strings;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.annotation.Json;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.domain.Article;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** @noinspection unused*/
public class IndexPage {
    private final UserService userService = new UserService();
    protected final ArticleService articleService = new ArticleService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        putMessage(request, view);
    }

    private void putMessage(HttpServletRequest request, Map<String, Object> view) {
        String message = (String) request.getSession().getAttribute("message");
        if (!Strings.isNullOrEmpty(message)) {
            view.put("message", message);
            request.getSession().removeAttribute("message");
        }
    }

    @Json
    private void findAllArticles(HttpServletRequest request, Map<String, Object> view) {
        Map<Long, String> idToLogin = userService.findAll().stream()
                .collect(Collectors.toMap(User::getId, User::getLogin, (a, b) -> b));

        List<Article> articles = articleService.findAll().stream().filter(x -> !x.isHidden()).collect(Collectors.toList());

        view.put("idToLogin", idToLogin);
        view.put("articles", articles);
    }
}
