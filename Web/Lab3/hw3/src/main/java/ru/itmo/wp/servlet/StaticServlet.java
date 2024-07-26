package ru.itmo.wp.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class StaticServlet extends HttpServlet {
    private static final Map<String, String> contentTypes = new HashMap<String, String>() {{
        put(".html", "text/html");
        put(".css", "text/css");
        put(".js", "application/javascript");
        put(".png", "image/png");
        put(".jpg", "image/jpeg");
    }};

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] uris = request.getRequestURI().split("\\+");
        File[] files = new File[uris.length];

        for (int i = 0; i < uris.length; i++) {
            files[i] = getFile(uris[i]);
            if (!files[i].isFile()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        for (int i = 0; i < files.length; i++) {
            if (response.getContentType() == null) {
                response.setContentType(getContentTypeFromName(uris[i]));
            }
            Files.copy(files[i].toPath(), response.getOutputStream());
        }
    }

    private File getFile(String uri) {
        File file = new File("src/main/webapp/static/", uri);
        return file.isFile() ? file : new File(getServletContext().getRealPath("/static/" + uri));
    }

    private String getContentTypeFromName(String name) {
        String extension = name.substring(name.lastIndexOf('.')).toLowerCase();
        String contentType = contentTypes.get(extension);
        if (contentType == null) {
            throw new IllegalArgumentException("Сontent type does not exist for '" + name + "'.");
        }
        return contentType;
    }
}