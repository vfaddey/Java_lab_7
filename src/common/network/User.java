package common.network;

import client.managers.ConsoleHandler;

import java.io.Serializable;

public abstract class User implements Serializable {
    protected String login;
    protected String password;

    public abstract void setLogin(String login);
    public abstract String getLogin();
    public abstract void setPassword(String password);
    public abstract String getPassword();
}
