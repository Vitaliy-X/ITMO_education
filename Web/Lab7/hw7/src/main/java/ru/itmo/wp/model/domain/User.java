package ru.itmo.wp.model.domain;

import java.io.Serializable;

public class User extends Base implements Serializable {
    private String login;
    private boolean admin;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
