package ru.itmo.wp.form;

import javax.validation.constraints.*;

public class UserRegisterCredentials extends UserCredentials {

    @NotBlank
    @NotNull
    @Size(min=1, max=255)
    private String name;

    public String getName() {
        return name;
    }

}