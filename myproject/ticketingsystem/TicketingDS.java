/*
 * @Author: starrysky9959 starrysky9651@outlook.com
 * @Date: 2022-11-10 16:29:45
 * @LastEditors: starrysky9959 starrysky9651@outlook.com
 * @LastEditTime: 2022-12-13 15:17:16
 * @Description:  
 */
package ticketingsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/*
 * 为简单起见，假设每个车次的 coachnum、seatnum 和 stationnum 都相同。车票涉及的各项参数均从 1 开始计数，例如车厢从 1 到 8 编号，车站从 1 到 10 编号等。
 */
public class TicketingDS implements TicketingSystem {
    private final int routeNum;
    private final int coachNum;
    private final int seatNum;
    private final int stationNum;
    private final int threadNum;
    // private int seatSize;
    ConcurrentHashMap<Long, Ticket> reservedTicketMap;
    AtomicLong nextTicketID;
    Seat[][][] seats;
    // private Random seatBegin;

    public TicketingDS(int routenum, int coachnum, int seatnum, int stationnum, int threadnum) {
        this.routeNum = routenum;
        this.coachNum = coachnum;
        this.seatNum = seatnum;
        this.stationNum = stationnum;
        this.threadNum = threadnum;
        seats = new Seat[routeNum + 1][coachNum + 1][seatNum + 1];
        for (int i = 1; i <= routeNum; ++i) {
            for (int j = 1; j <= coachNum; ++j) {
                for (int k = 1; k <= seatNum; ++k) {
                    seats[i][j][k] = new Seat();
                }
            }
        }
        // seatBegin = new Random();
        // seatSize = coachnum * seatNum;
        nextTicketID = new AtomicLong(1);
        reservedTicketMap = new ConcurrentHashMap<Long, Ticket>();
    }

    private boolean verify(int route, int departure, int arrival) {
        // arrival is always greater than departure
        return ((1 <= route && route <= routeNum) &&
                (1 <= departure && departure < arrival && arrival <= stationNum));
    }

    @Override
    public int inquiry(int route, int departure, int arrival) {

        if (!verify(route, departure, arrival)) {
            return 0;
        }

        int ans = 0;
        for (int coachIndex = 1; coachIndex <= coachNum; ++coachIndex) {
            for (int seatIndex = 1; seatIndex <= seatNum; ++seatIndex) {
                Seat s = seats[route][coachIndex][seatIndex];
                if (s.available(departure, arrival)) {
                    ++ans;
                }
            }
        }

        return ans;
    }

    @Override
    public Ticket buyTicket(String passenger, int route, int departure, int arrival) {
        if (!verify(route, departure, arrival)) {
            return null;
        }

        Ticket ticket = null;

        // int begin = seatBegin.nextInt(seatSize);
        // int index;
        // int coachIndex;
        // int seatIndex;
        // for (int i = 0; i < seatSize; ++i) {
        // index = (begin + i) % seatSize;
        // coachIndex = index / seatNum + 1;
        // seatIndex = index % seatNum + 1;
        // Seat s = seats[route][coachIndex][seatIndex];

        // if (s.buy(departure, arrival)) {
        // ticket = new Ticket();
        // ticket.tid = nextTicketID.getAndIncrement();
        // ticket.passenger = passenger;
        // ticket.route = route;
        // ticket.coach = coachIndex;
        // ticket.seat = seatIndex;
        // ticket.departure = departure;
        // ticket.arrival = arrival;
        // reservedTicketMap.put(ticket.tid, ticket);
        // return ticket;
        // }
        // }

        for (int coachIndex = 1; coachIndex <= coachNum; ++coachIndex) {
            for (int seatIndex = 1; seatIndex <= seatNum; ++seatIndex) {
                Seat s = seats[route][coachIndex][seatIndex];
                if (s.buy(departure, arrival)) {
                    ticket = new Ticket();
                    ticket.tid = nextTicketID.getAndIncrement();
                    ticket.passenger = passenger;
                    ticket.route = route;
                    ticket.coach = coachIndex;
                    ticket.seat = seatIndex;
                    ticket.departure = departure;
                    ticket.arrival = arrival;
                    reservedTicketMap.put(ticket.tid, ticket);
                    return ticket;
                }
            }
        }

        return null;
    }

    @Override
    public boolean refundTicket(Ticket ticket) {
        Ticket real = reservedTicketMap.get(ticket.tid);
        if (real != null) {
            if ((real.tid == ticket.tid) && (real.passenger.equals(ticket.passenger)) &&
                    (real.route == ticket.route) && (real.coach == ticket.coach)
                    && (real.seat == ticket.seat) && (real.departure == ticket.departure)
                    && (real.arrival == ticket.arrival)) {
                // printTicket(ticket);
                Seat s = seats[ticket.route][ticket.coach][ticket.seat];
                boolean result = s.refund(ticket.departure, ticket.arrival);
                if (result) {
                    reservedTicketMap.remove(ticket.tid);
                }
                return result;
            }
        }
        return false;
    }

    public void printTicket(Ticket ticket) {
        System.err.println("-------------------\ntid: " + ticket.tid +
                "\npassenger: " + ticket.passenger +
                "\nroute: " + ticket.route +
                "\ncoach: " + ticket.coach +
                "\nseat: " + ticket.seat +
                "\ndeparture: " + ticket.departure +
                "\narrival: " + ticket.arrival);
    }

    @Override
    public boolean refundTicketReplay(Ticket ticket) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean buyTicketReplay(Ticket ticket) {
        // TODO Auto-generated method stub
        return false;
    }
}
