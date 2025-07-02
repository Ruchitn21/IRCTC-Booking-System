package org.ticketBooking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ticketBooking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
}
