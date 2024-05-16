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
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;


/**
 * Class, that manage the collection, makes requests to user
 */
public class CollectionManager{
    private ConcurrentLinkedDeque<Organization> collection;
    private String collectionFilename;
    private String information;
    private LocalDate lastUpdateDate;
    private FileManager fileManager;
    private DatabaseConnection connection;

    public CollectionManager(FileManager fileManager, String fileName) {
        this.fileManager = fileManager;
        lastUpdateDate = LocalDate.now();
    }


//    public void loadCollectionFromCSV(String fileName) {
//        this.collection = fileManager.read(fileName);
//        this.collectionFilename = fileName;
//        if (collection != null) {
//            Collections.sort(collection);
//        }
//    }

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
        boolean result = collection.removeIf(org -> (org.getId() == id && user.getLogin().equals(org.getOwnerLogin())));
        if (result) {
            this.connection.removeById(id, user);
            lastUpdateDate = LocalDate.now();
        } else {
            throw new ElementNotFoundException("Элемент с таким id не найден или Вы не являетесь его создателем.");
        }
    }

    public ConcurrentLinkedDeque<Organization> getCollection() {
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
        shuffleDeque(collection);
        lastUpdateDate = LocalDate.now();
    }

    private <T> void shuffleDeque(ConcurrentLinkedDeque<T> deque) {
        synchronized (deque) {
            List<T> list = new ArrayList<>(deque);
            deque.clear();
            Collections.shuffle(list);
            deque.addAll(list);
        }
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
            if (getElementAt(i).getId() == id) {
                setElementAt(i, element);
                break;
            }
        }
    }

    public Organization getElementAt(int index) {
        int currentIndex = 0;
        for (Organization element : collection) {
            if (currentIndex == index) {
                return element;
            }
            currentIndex++;
        }
        throw new IndexOutOfBoundsException("Индекс: " + index + ", Размер: " + currentIndex);
    }

    public void setElementAt(int index, Organization newValue) {
        int currentIndex = 0;
        Iterator<Organization> iterator = this.collection.iterator();
        while (iterator.hasNext()) {
            Organization element = iterator.next();
            if (currentIndex == index) {
                synchronized (collection) {
                    if (collection.contains(element)) {
                        iterator.remove();
                        collection.addFirst(newValue);
                        for (int i = 0; i < currentIndex; i++) {
                            collection.addLast(collection.pollFirst());
                        }
                    }
                }
                return;
            }
            currentIndex++;
        }
        throw new IndexOutOfBoundsException("Индекс: " + index + ", Размер: " + currentIndex);
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
