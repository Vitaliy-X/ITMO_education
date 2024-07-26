package ru.itmo.web.hw4.util;

import ru.itmo.web.hw4.model.User;
import ru.itmo.web.hw4.model.Post;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DataUtil {
    private static final List<User> USERS = Arrays.asList(
            new User(1, "MikeMirzayanov", "Mike Mirzayanov"),
            new User(6, "pashka", "Pavel Mavrin"),
            new User(9, "geranazavr555", "Georgiy Nazarov"),
            new User(11, "tourist", "Gennady Korotkevich")
    );

    public static final List<Post> POSTS = Arrays.asList(
            new Post(1, "Codeforces Round 903 (Div. 3) Разбор", "1881A - Не пытайтесь посчитать!\n" +
                    "\n" +
                    "Заметим, что ответ всегда не превышает 5. При \uD835\uDC5B=1, \uD835\uDC5A=25 ответом является либо 5, либо −1, легко понять, что ответ не бывает больше.\n" +
                    "Это позволяет нам просто перебрать количество операций, каждый раз проверяя, встретилась ли \uD835\uDC60 в \uD835\uDC65. Асимптотика такого решения \uD835\uDC42(25⋅\uD835\uDC5B⋅\uD835\uDC5A).",
                    1),
            new Post(2, "Getting Started with Python", "Python is a popular programming language known for its simplicity and readability. It is widely used in web development, data analysis, artificial intelligence, and many other areas. To get started with Python, you can install the Python interpreter on your computer and use an integrated development environment (IDE) such as PyCharm or Visual Studio Code. Additionally, Python has a large and active community, which means there are plenty of resources available for learning and getting help. You can find tutorials, documentation, and code examples on websites like Python.org and Stack Overflow. Once you have a basic understanding of Python syntax and concepts, you can start building your own projects and exploring more advanced topics like object-oriented programming, web frameworks, and machine learning.",
                    9),
            new Post(16, "Introduction to Machine Learning", "Machine learning is a field of study that focuses on the development of algorithms and statistical models that enable computers to learn from and make predictions or decisions without being explicitly programmed. It is a subset of artificial intelligence and has applications in various domains such as image recognition, natural language processing, and data analysis.", 6),
            new Post(3, "Codeforces Round #902 (Div. 1)", "Hello everybody!", 1));

    public static void addData(HttpServletRequest request, Map<String, Object> data) {
        data.put("users", USERS);
        data.put("posts", POSTS);

        data.put("currentUrl", request.getRequestURI());

        for (User user : USERS) {
            if (Long.toString(user.getId()).equals(request.getParameter("logged_user_id"))) {
                data.put("user", user);
            }
        }
    }
}
