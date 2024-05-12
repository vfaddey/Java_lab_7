package common.responses;

import common.network.User;

public class AuthenticateResponse extends Response {
    private User user;
    private boolean result;
    public AuthenticateResponse(String commandName, String message, User user, boolean result) {
        super(commandName, message);
        this.user = user;
        this.result = result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
