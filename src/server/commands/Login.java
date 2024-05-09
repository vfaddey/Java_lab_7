package server.commands;

import common.exceptions.WrongPasswordException;
import common.requests.AuthenticateRequest;
import common.requests.RequestDTO;
import common.responses.AuthenticateResponse;
import common.responses.EmptyResponse;
import common.responses.ErrorResponse;
import common.responses.Response;

import java.io.IOException;
import java.sql.SQLException;

public class Login extends Command{
    public Login(String nameInConsole) {
        super(nameInConsole, "Авторизация пользователя", "Пользователь авторизован!");
    }

    @Override
    public Response execute(RequestDTO requestDTO) throws IOException {
        AuthenticateRequest request = (AuthenticateRequest) requestDTO.getRequest();
        String login = request.getLogin();
        String password = request.getPassword();

        try {
            if (this.collectionManager.getConnection().authenticateUser(login, password)) {
                return new AuthenticateResponse(request.getCommandName(), successPhrase, true);
            }
        } catch (SQLException e) {
            return new AuthenticateResponse(request.getCommandName(), "Не удалось войти(", false);
        } catch (WrongPasswordException e) {
            return new ErrorResponse(e.getMessage());
        }
        return new EmptyResponse();
    }
}
