package org.ticketBooking;

import org.ticketBooking.entities.Ticket;
import org.ticketBooking.entities.Train;
import org.ticketBooking.entities.User;
import org.ticketBooking.service.TrainService;
import org.ticketBooking.service.UserBookingService;
import org.ticketBooking.util.UserServiceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {

        System.out.println("Welcome to the Ticket Booking System!");

        Scanner sc = new Scanner(System.in);

        int option = 0;

        UserBookingService userBookingService;

        TrainService trainService;

        try {
            userBookingService = new UserBookingService();
            trainService = new TrainService();

        } catch (IOException e) {
            System.out.println("Error initializing user booking service: " + e.getMessage());
            return;
        }

        while(option!=7) {
            System.out.println("Choose Option");
            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit the App");

            option = sc.nextInt();
            switch (option) {
                case 1:
                    System.out.println("Enter your name:");
                    String name = sc.next();
                    System.out.println("Enter your password:");
                    String password = sc.next();
                    System.out.println("Re-enter your password");
                    String reEnterredPassword = sc.next();
                    if(!password.equals(reEnterredPassword)) {
                        System.out.println("Passwords do not match!");
                        break;
                    }
                    if (userBookingService.signUp(new User(UUID.randomUUID().toString(),name, password, UserServiceUtil.hashPassword(password), new ArrayList<>()))) {
                        System.out.println("Sign Up Successful!");
                    } else {
                        System.out.println("Sign Up Failed!");
                    }
                    break;
                case 2:
                    System.out.println("Enter your name:");
                    String loginName = sc.next();
                    System.out.println("Enter your password:");
                    String loginPassword = sc.next();
//                    User user = new User(UUID.randomUUID().toString(), loginName, loginPassword, UserServiceUtil.hashPassword(loginPassword), new ArrayList<>());
                    if (userBookingService.login(loginName, loginPassword)) {
                        User user = userBookingService.getUserByUsername(loginName)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                        try {
                            userBookingService = new UserBookingService(user);
                            System.out.println("Login Successful!");
                        } catch (IOException e) {
                            System.out.println("IO Exception occurred while logging in: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Login Failed!");
                    }
                    break;
                case 3:
                    System.out.println("Fetching Bookings...");
                    // Assuming user has logged in and we have a user object
                    List<Ticket> tickets;
                    try {
                        tickets = userBookingService.fetchBooking();
                    } catch (NullPointerException e) {
                        System.out.println("No user logged in. Please login to fetch bookings.");
                        break;
                    }
                    if(tickets.isEmpty()) {
                        System.out.println("No bookings found for the user.");
                        break;
                    }
                    System.out.println("Your Bookings:");
                    for (Ticket ticket : tickets) {
                            System.out.println("Ticket ID: " + ticket.getTicketId());
                            System.out.println("Train Name: " + ticket.getTrain().getTrainName());
                            System.out.println("Source: " + ticket.getSource());
                            System.out.println("Destination: " + ticket.getDestination());
                            System.out.println("-----------------------------");
                        }
                    break;

                case 4:
                    System.out.println("Enter source station:");
                    String source = sc.next();
                    System.out.println("Enter destination station:");
                    String destination = sc.next();
                    List<Train> availableTrains = trainService.searchTrains(source, destination);
                    if (availableTrains.isEmpty()) {
                        System.out.println("No trains available for the given route.");
                    } else {
                        System.out.println("Total trains found: "+ availableTrains.size());
                        System.out.println(source +" -------------------------> "+ destination);
                        for (Train train : availableTrains) {
                            System.out.println(train.getTrainName()+" ("+train.getTrainNo()+")");
                            System.out.println("Arrival Time: "+trainService.getStationTime(source));
                            System.out.println("Departure Time: "+trainService.getStationTime(destination));
//                            System.out.println("Seats Available: "+);
                        }
                    }
                    break;
                case 7:
                    break;
            }
        }
    }
}