package server;

import server.commands.*;
import server.db.Database;
import server.db.DatabaseConnection;
import server.db.PostgressDatabase;
import server.interfaces.FileManager;
import server.managers.*;
import server.network.TCPServer;

import java.io.IOException;
import java.sql.SQLException;

public class Server {
    public static void main(String[] args) throws IOException {
        FileManager csvHandler = new CSVHandler();
        CollectionManager collectionManager = new CollectionManager(csvHandler);
        CommandManager commandManager = new CommandManager(csvHandler);
        commandManager.setCollectionManager(collectionManager);
        commandManager.addCommands(
                new Add("add"),
                new Clear("clear"),
                new Show("show"),
                new Help("help"),
                new Exit("exit"),
                new RemoveById("remove_by_id"),
                new Update("update"),
                new Shuffle("shuffle"),
                new FilterContainsName("filter_contains_name"),
                new FilterLessThanAnnualTurnover("filter_less_than_annual_turnover"),
                new RemoveAnyByAnnualTurnover("remove_any_by_annual_turnover"),
                new Info("info"),
                new RemoveGreater("remove_greater"),
                new RemoveLower("remove_lower"),
                new Login("login"),
                new Register("register"));
        RequestHandler requestHandler = new RequestHandler(commandManager);
        TCPServer server = new TCPServer(requestHandler, new Logger("logs.log"));
        Database db = new PostgressDatabase("jdbc:postgresql://localhost:5432/studs", "postgres", "123");
        try {
            DatabaseConnection connection = db.createConnection();
            collectionManager.setConnection(connection);
            collectionManager.loadCollectionFromDB();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }

        try {
            server.openConnection();
            server.run();
        } finally {
            server.close();
        }
    }
}
