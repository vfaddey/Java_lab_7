package server.commands;

import common.exceptions.UserAlreadyExistsException;
import common.requests.AddUserRequest;
import common.requests.RequestDTO;
import common.responses.*;

import java.io.IOException;
import java.sql.SQLException;

public class Register extends Command{
    public Register(String nameInConsole) {
        super(nameInConsole, "Регистрация нового пользователя", "Новый пользователь добавлен!");
    }

    @Override
    public Response execute(RequestDTO requestDTO) throws IOException {
        AddUserRequest request = (AddUserRequest) requestDTO.getRequest();
        String login = request.getUser().getLogin();
        String password = request.getUser().getPassword();

        try {
            this.collectionManager.getConnection().addUser(login, password);
            return new SuccessResponse(getNameInConsole(), successPhrase);
        } catch (SQLException e) {
            System.out.println(e.toString());
        } catch (UserAlreadyExistsException e) {
            return new ErrorResponse(e.toString());
        }
        return new EmptyResponse();
    }
}
