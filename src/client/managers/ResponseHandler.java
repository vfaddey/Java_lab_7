package client.managers;

import common.exceptions.AuthenticationFailedException;
import common.responses.*;

public class ResponseHandler {
    public String handleResponse(Response response) {
        if (response instanceof ErrorResponse) {
            return "Ошибка: " + response;
        } else if (response instanceof EmptyResponse) {
            return null;
        } else if (response instanceof SuccessResponse) {
            return "Успешно: " + response;
        } else if (response instanceof ExitResponse) {
            System.exit(0);
        } else if (response instanceof AuthenticateResponse) {
            if (((AuthenticateResponse) response).getResult()) {
                return "Авторизация прошла успешно!";
            } else {
                throw new AuthenticationFailedException("Авторизация не удалась. Возможно неверный пароль!");
            }
        }
        return response.toString();
    }
}
