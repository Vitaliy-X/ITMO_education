package ru.itmo.wp.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;

public class NoticeForm {
    @NotNull
    @NotBlank
    @Size(max = 1024, message = "too long content")
    private String content;
    private List<String> paragraphs;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.paragraphs = Arrays.asList(content.split("\n"));
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }
}
