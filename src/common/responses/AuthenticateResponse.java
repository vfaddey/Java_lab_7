package common.responses;

public class AuthenticateResponse extends Response {
    private boolean result;
    public AuthenticateResponse(String commandName, String message, boolean result) {
        super(commandName, message);
        this.result = result;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
