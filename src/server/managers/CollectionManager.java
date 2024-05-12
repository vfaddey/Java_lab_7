package server.managers;

import client.managers.Validator;
import common.exceptions.ElementNotFoundException;
import common.network.User;
import server.db.DatabaseConnection;
import server.interfaces.FileManager;
import common.model.*;


import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Class, that manage the collection, makes requests to user
 */
public class CollectionManager{
    private LinkedList<Organization> collection;
    private String collectionFilename;
    private String information;
    private LocalDate lastUpdateDate;
    private FileManager fileManager;
    private DatabaseConnection connection;

    public CollectionManager(FileManager fileManager, String fileName) {
        this.fileManager = fileManager;
        lastUpdateDate = LocalDate.now();
//        loadCollectionFromCSV(fileName);
//        updateInformation();
    }


    public void loadCollectionFromCSV(String fileName) {
        this.collection = fileManager.read(fileName);
        this.collectionFilename = fileName;
        if (collection != null) {
            Collections.sort(collection);
        }
    }

    public void loadCollectionFromDB() throws SQLException {
        this.collection = this.connection.getAllOrganizations();
        updateInformation();
    }

    private void updateInformation() {
        information = "Тип коллекции: " + LinkedList.class.getName() + "\n"
                + "Хранит объекты типа: " + Organization.class.getName() + "\n"
                + "Количество элементов коллекции: " + collection.size() + "\n"
                + "Последнее обновление коллекции: " + lastUpdateDate;
    }

    public void addNewElement(Organization organization) {
        try {
            long id = connection.addOrganization(organization.getName(),
                    organization.getCoordinates(),
                    organization.getCreationDate(),
                    organization.getAnnualTurnover(),
                    organization.getEmployeesCount(),
                    organization.getType(),
                    organization.getOfficialAddress(),
                    "papa");
            organization.setId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        collection.add(organization);
        this.lastUpdateDate = LocalDate.now();
        updateInformation();
    }

    public void removeById(long id, User user) throws ElementNotFoundException, SQLException {
        int len = this.collection.size();
        collection.removeIf(org -> (org.getId() == id && user.getLogin().equals(org.getOwnerLogin())));
        this.connection.removeById(id, user);
        if (len < this.collection.size()) {
            lastUpdateDate = LocalDate.now();
        } else {
            throw new ElementNotFoundException("Элемент с таким id не найден.");
        }

    }

    public LinkedList<Organization> getCollection() {
        return collection;
    }
    public void clearCollection(User user) throws SQLException {
        this.connection.clearCollectionForUser(user);
        this.collection.clear();
    }

    public String getCollectionFilename() {
        return collectionFilename;
    }

    public Organization getElementById(long id) throws ElementNotFoundException {
        for (Organization organization : collection) {
            if (organization.getId() == id) return organization;
        }
        throw new ElementNotFoundException("Элемента с таким id не существует");
    }

    public LinkedList<Organization> getElementsByName(String substring) {
        return collection.stream()
                .filter(organization -> Validator.isSubstring(substring, organization.getName()))
                .collect(Collectors.toCollection(LinkedList::new));
    }


    public Organization[] getElementsLessThanAnnualTurnover(long annualTurnover) {
        List<Organization> elements = new ArrayList<>();
        for (Organization organization : collection) {
            if (organization.getAnnualTurnover() < annualTurnover) {
                elements.add(organization);
            }
        }
        return elements.toArray(new Organization[0]);
    }

    public void shuffleCollection() {
        Collections.shuffle(collection);
        lastUpdateDate = LocalDate.now();
    }

    public Organization[] getElementsByAnnualTurnover(long annualTurnover) {
        List<Organization> elements = new ArrayList<>();
        for (Organization organization : collection) {
            if (organization.getAnnualTurnover() == annualTurnover) {
                elements.add(organization);
            }
        }
        return elements.toArray(new Organization[0]);
    }

    public void setElementById(long id, Organization element) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getId() == id) {
                collection.set(i, element);
                break;
            }
        }
    }

    public DatabaseConnection getConnection() {
        return connection;
    }

    public void setConnection(DatabaseConnection connection) {
        this.connection = connection;
    }

    public String getInformation() {
        return information;
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }
}
