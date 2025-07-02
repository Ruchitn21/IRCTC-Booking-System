package org.ticketBooking.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String name;

    private String password;

    private String hashedPassword;

    private List<Ticket> tickets;

}
