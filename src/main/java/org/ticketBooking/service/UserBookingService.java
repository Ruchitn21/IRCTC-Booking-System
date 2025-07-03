package org.ticketBooking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ticketBooking.entities.Ticket;
import org.ticketBooking.entities.User;
import org.ticketBooking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserBookingService {

    private User user;

    private ObjectMapper objectMapper = new ObjectMapper();

    private List<User> userList;

    private static final String USERS_PATH = "src/main/java/org/ticketBooking/localDB/users.json";

    public UserBookingService(User user) throws IOException {
        this.user = user;
        userList = loadUsers();
    }

    public UserBookingService() throws IOException {
        userList = loadUsers();
    }

    public List<User> loadUsers() throws IOException {
        File users = new File(USERS_PATH);
        return objectMapper.readValue(users, new TypeReference<List<User>>() {});
    }

    public boolean login(String userName, String password) {
        Optional<User> userFound = userList.stream().filter(user1 -> user1.getName().equals(userName) &&
               UserServiceUtil.checkPassword(password, user1.getHashedPassword())).findFirst();

        return userFound.isPresent();
    }

    public boolean signUp(User user1) {
        try {
            if (getUserByUsername(user1.getName()).isPresent()) {
                System.out.println("User already exists with this username");
                return Boolean.FALSE;
            }
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException e) {
            return Boolean.FALSE;
        }
    }

    public boolean isLoggedIn() {
        return this.user!=null;
    }

    public void logout() {
        this.user=null;
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public List<Ticket> fetchBooking() throws NullPointerException {
        try {
            loadUsers();
        } catch (IOException e) {
            System.out.println("Error loading user data: " + e.getMessage());
        }
        return user.getTickets();
    }

    public void cancelBooking(String ticketId) {
        try {
            List<Ticket> tickets = userList.stream().filter(x->x.getName().equals(user.getName())).findFirst().get().getTickets();
            if(tickets.isEmpty()) {
                System.out.println("No bookings found for the user.");
                return;
            }
            for(Ticket ticket : tickets) {
                if(ticket.getTicketId().equals(ticketId)) {
                    System.out.println("Cancelling ticket with ID: " + ticketId);
                    tickets.remove(ticket);
                    File usersFile = new File(USERS_PATH);
                    objectMapper.writeValue(usersFile, userList);
                    System.out.println("Ticket cancelled successfull!");
                    return;
                }
            };

        } catch (NullPointerException e) {
            System.out.println("User is not logged in. Please login to cancel bookings.");
        }
        catch (IOException e) {

        }
    }

    public Optional<User> getUserByUsername(String loginName) {
        return userList.stream().filter(user1 -> user1.getName().equals(loginName))
                .findFirst();
    }

    public void addTrainTicketToUser(Ticket ticket) {
        try {
            List<Ticket> ticketsArray = userList.stream().filter(x->x.getName().equals(user.getName())).findFirst().get().getTickets();
            ticketsArray.add(ticket);
            File usersFile = new File(USERS_PATH);
            objectMapper.writeValue(usersFile, userList);

        } catch (NullPointerException e) {
            System.out.println("User is not logged in. Please login to add tickets.");
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }
}
