package org.ticketBooking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ticketBooking.entities.User;
import org.ticketBooking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserBookingService {

    private User user;

    private ObjectMapper objectMapper = new ObjectMapper();

    private List<User> userList;

    private static final String USERS_PATH = "../localDB/users.json";

    public UserBookingService(User user) throws IOException {
        this.user = user;
        File users = new File(USERS_PATH);
        userList = objectMapper.readValue(users, new TypeReference<List<User>>() {});
    }

    public boolean login() {
        Optional<User> userFound = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && ;
                   UserServiceUtil.checkPassword(user.getPassword(), user1.getPassword());
        }).findFirst();

        return userFound.isPresent();
    }
}
