/*
 * @Author: starrysky9959 starrysky9651@outlook.com
 * @Date: 2022-11-11 10:39:22
 * @LastEditors: starrysky9959 starrysky9651@outlook.com
 * @LastEditTime: 2022-11-11 13:53:52
 * @Description:  
 */
package ticketingsystem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Seat {

    class Pair {
        public int departure;
        public int arrival;

        @Override
        public int hashCode() {
            // return Objects.hash(departure, arrival);
            // TODO Auto-generated method stub
            return super.hashCode();
        }

        public Pair(int departure, int arrival) {
            this.departure = departure;
            this.arrival = arrival;
        }
    }

    private Set<Pair> reservedStation;

    public Seat() {
        reservedStation = new HashSet<Pair>();
    }

    public boolean buy(int departure, int arrival) {
        if (!available(departure, arrival)) {
            return false;
        }
        reservedStation.add(new Pair(departure, arrival));
        return true;

    }

    public boolean refund(Ticket ticket) {
        Pair p = new Pair(ticket.departure, ticket.arrival);
        return reservedStation.remove(p);
    }

    public boolean available(int departure, int arrival) {
        // if (!departureUsed[departure]) {
        // return false;
        // }
        // if (!departureUsed[departure]) {
        // return false;
        // }

       
        for (Pair p : reservedStation) {
            if ((departure >= p.departure && departure < p.arrival) ||
                    (arrival > p.departure && departure <= p.arrival)) {
                return false;
            }
        }
        return true;
    }
}
