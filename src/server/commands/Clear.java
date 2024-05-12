package server.commands;

import common.network.GuestUser;
import common.network.User;
import common.requests.ClearRequest;
import common.requests.RequestDTO;
import common.responses.ErrorResponse;
import common.responses.Response;
import common.responses.SuccessResponse;
import server.interfaces.CommandWithoutParameters;

import java.sql.SQLException;

public class Clear extends Command implements CommandWithoutParameters {
    public Clear(String consoleName) {
        super(consoleName, "<Без параметров> Очистить коллекцию", "Коллекция очищена!");
    }

    @Override
    public Response execute(RequestDTO requestDTO) {
        ClearRequest request = (ClearRequest) requestDTO.getRequest();
        User user = request.getUser();
        if (user instanceof GuestUser) {
            return new ErrorResponse("Войдите в аккаунт для удаления элементов!");
        }
        try {
            collectionManager.clearCollection(user);
            return new SuccessResponse(getNameInConsole(), successPhrase);
        } catch (SQLException e) {
            return new ErrorResponse("Не удалось удалить элементы.");
        }

    }
}
