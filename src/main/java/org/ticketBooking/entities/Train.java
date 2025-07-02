package org.ticketBooking.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Train {

    private String trainId;

    private String trainNo;

    private String trainName;

    private List<List<Integer>> seats;

    private Map<String, String> schedule;

    private List<String> stations;

    @Override
    public String toString() {
        return "Train{" +
                "trainId='" + trainId + '\'' +
                ", trainNo='" + trainNo + '\'' +
                ", seats=" + seats +
                ", schedule=" + schedule +
                ", stations=" + stations +
                '}';
    }
}
