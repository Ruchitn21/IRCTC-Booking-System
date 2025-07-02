package org.ticketBooking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ticketBooking.entities.Ticket;
import org.ticketBooking.entities.Train;
import org.ticketBooking.entities.User;
import org.ticketBooking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException e) {
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public List<Ticket> fetchBooking() throws NullPointerException {

        return user.getTickets();
    }

    public boolean cancelBooking(String ticketId) {
        try {
            List<Ticket> tickets = user.getTickets();
            tickets.removeIf(ticket -> ticket.getTicketId().equals(ticketId));
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException e) {
            return Boolean.FALSE;
        }
    }

    public Optional<User> getUserByUsername(String loginName) {
        return userList.stream().filter(user1 -> user1.getName().equals(loginName))
                .findFirst();
    }
}
