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
            System.out.println("7. Logout");

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
                    if (userBookingService.signUp(new User(name, UserServiceUtil.hashPassword(password), new ArrayList<>()))) {
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
                            System.out.println("------------------------------");
                            System.out.println("Ticket ID: " + ticket.getTicketId());
                            System.out.println("Train Name: " + ticket.getTrainName());
                            System.out.println("Source: " + ticket.getSource());
                            System.out.println("Destination: " + ticket.getDestination());
                            System.out.println("Date of Travel: " + ticket.getDateOfTravel());
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
                            System.out.println(train.getTrainName()+" ("+train.getTrainId()+")");
                            System.out.println("Arrival Time: "+trainService.getStationTime(source));
                            System.out.println("Departure Time: "+trainService.getStationTime(destination));
                            System.out.println("Seats Available: "+trainService.getTotalSeats(train.getTrainId()));
                            System.out.println("---------------------------------");
                        }
                    }
                    break;

                case 5:
                    if(!userBookingService.isLoggedIn()) {
                        System.out.println("No user logged in. Please login to book a ticket.");
                        break;
                    }
                    System.out.println("Enter train id: ");
                    String trainId = sc.next();
                    System.out.println("Enter source station: ");
                    String bookSource = sc.next();
                    System.out.println("Enter destination station: ");
                    String bookDestination = sc.next();
                    System.out.println("Enter number of Seats: ");
                    int totalSeatsRequired = sc.nextInt();

                    Ticket ticket = trainService.bookTicket(trainId, bookSource, bookDestination, totalSeatsRequired);
                    if(ticket == null) {
                        System.out.println("Train with id "+trainId+" not found!");
                        break;
                    }
                    if(totalSeatsRequired> trainService.getTotalSeats(trainId)) {
                        System.out.println("Not enough seats available!");
                        break;
                    }
                    if (ticket != null) {
                        System.out.println("Ticket booked successfully!");
                        System.out.println("------------------------------");
                        System.out.println("Ticket ID: " + ticket.getTicketId());
                        System.out.println("Train Name: " + ticket.getTrainName());
                        System.out.println("Source: " + ticket.getSource());
                        System.out.println("Destination: " + ticket.getDestination());
                        System.out.println("Date of Travel: " + ticket.getDateOfTravel());
                        System.out.println("-----------------------------");
                        userBookingService.addTrainTicketToUser(ticket);
                    } else {
                        System.out.println("Failed to book ticket.");
                    }
                    break;

                case 6:
                    if(!userBookingService.isLoggedIn()) {
                        System.out.println("No user logged in. Please login to cancel a ticket.");
                        break;
                    }
                    System.out.println("Enter Ticket ID to cancel booking: ");
                    String ticketId = sc.next();
                    userBookingService.cancelBooking(ticketId);
                    break;

                case 7:
                    if(!userBookingService.isLoggedIn()) {
                        System.out.println("User already logged out");
                        break;
                    }
                    System.out.println("Logging out...");
                    userBookingService.logout();
                    System.out.println("Logged out successfully!");
                    break;
            }
        }
    }
}