package common.requests;

import common.network.User;

import java.io.Serializable;

public abstract class Request implements Serializable {
    protected String commandName;
    protected String message = null;
    protected User user;

    public Request(String commandName) {
        this.commandName = commandName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Request{" +
                "commandName='" + commandName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
