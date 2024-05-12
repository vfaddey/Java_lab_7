package server.commands;

import common.exceptions.ElementNotFoundException;
import common.network.GuestUser;
import common.network.User;
import common.requests.RemoveByIdRequest;
import common.requests.RequestDTO;
import common.responses.EmptyResponse;
import common.responses.ErrorResponse;
import common.responses.Response;
import common.responses.SuccessResponse;

import java.sql.SQLException;

public class RemoveById extends Command {
    public RemoveById(String consoleName) {
        super(consoleName, "<long id> Удаляет элемент коллеции по id", "Элемент коллекции удален!");
    }

    @Override
    public Response execute(RequestDTO requestDTO) {
        RemoveByIdRequest request = (RemoveByIdRequest) requestDTO.getRequest();
        User user = request.getUser();
        if (user instanceof GuestUser) {
            return new ErrorResponse("Войдите в аккаунт для удаления элементов!");
        }
        if (request != null) {
            try {
                collectionManager.removeById(request.getId(), user);
                return new SuccessResponse(getNameInConsole(), successPhrase);
            } catch (ElementNotFoundException e) {
                return new ErrorResponse(e.toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new EmptyResponse();
        }
    }
}
