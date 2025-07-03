package org.ticketBooking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ticketBooking.entities.Ticket;
import org.ticketBooking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TrainService {

    ObjectMapper objectMapper = new ObjectMapper();

    private static final String TRAINS_PATH = "src/main/java/org/ticketBooking/localDB/trains.json";

    private List<Train> trainList;

    public TrainService() throws IOException {
        trainList = loadTrains();
    }

    private List<Train> loadTrains() throws IOException {
        File trainsFile = new File(TRAINS_PATH);
        return objectMapper.readValue(trainsFile, new TypeReference<List<Train>>() {});
    }

    public List<Train> searchTrains(String source, String destination) {
        return trainList.stream().filter(train -> train.getStations().contains(source) && train.getStations().contains(destination)).collect(Collectors.toList());
    }

    public String getStationTime(String station) {
        return trainList.stream()
                .flatMap(train -> train.getSchedule().entrySet().stream())
                .filter(entry -> entry.getKey().equals(station))
                .map(entry -> entry.getValue())
                .findFirst()
                .orElse("No time available for this station");
    }

    public int getTotalSeats(String trainId) {
        return trainList.stream()
                    .filter(train -> train.getTrainId().equals(trainId))
                    .map(train -> train.getSeats().stream()
                            .flatMap(List::stream)
                            .filter(seat -> seat == 0)
                            .count())
                    .findFirst()
                    .orElse(0L)
                    .intValue();
        }

    public Ticket bookTicket(String trainId, String source, String destination, int totalSeatsRequired) {
        Train train = trainList.stream().filter(train1 -> train1.getTrainId().equals(trainId)).findFirst().orElse(null);
        if(!train.getStations().contains(source) && !train.getStations().contains(destination)) {
            System.out.println("Train does not run at the given source or destination.");
            return null;
        }
        if (train == null) {
            return null;
        }
        try {
            updateSeatMap(totalSeatsRequired);
        } catch (IOException e) {
            System.out.println("Error updating seat map: " + e.getMessage());
            return null;
        }
        Ticket ticket = new Ticket();
        ticket.setTicketId(UUID.randomUUID().toString());
        ticket.setTrainName(train.getTrainName());
        ticket.setSource(source);
        ticket.setDestination(destination);
        ticket.setDateOfTravel(new Date());// Example date, replace with actual date logic
        return ticket;
    }

    private void updateSeatMap(int totalSeatsRequired) throws IOException {
        while(totalSeatsRequired>0) {
            for(Train train : trainList) {
                List<List<Integer>> seats = train.getSeats();
                for (int i = 0; i < seats.size(); i++) {
                    List<Integer> row = seats.get(i);
                    for (int j = 0; j < row.size(); j++) {
                        if (row.get(j) == 0) { // Assuming 0 means seat is available
                            row.set(j, 1); // Mark seat as booked
                            totalSeatsRequired--;
                            if (totalSeatsRequired == 0) {
                                // Save the updated trainList back to the JSON file
                                objectMapper.writeValue(new File(TRAINS_PATH), trainList);
                                return;

                            }
                        }
                    }
                }
            }
        }
    }
}
