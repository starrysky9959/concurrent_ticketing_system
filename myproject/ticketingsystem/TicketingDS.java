/*
 * @Author: starrysky9959 starrysky9651@outlook.com
 * @Date: 2022-11-10 16:29:45
 * @LastEditors: starrysky9959 starrysky9651@outlook.com
 * @LastEditTime: 2022-12-14 13:15:19
 * @Description:  
 */
package ticketingsystem;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/*
 * 为简单起见，假设每个车次的 coachnum、seatnum 和 stationnum 都相同。车票涉及的各项参数均从 1 开始计数，例如车厢从 1 到 8 编号，车站从 1 到 10 编号等。
 */
public class TicketingDS implements TicketingSystem {
    private final int routeNum;
    private final int coachNum;
    private final int seatNum;
    private final int stationNum;
    private final int threadNum;
    private int seatSize;
    ConcurrentHashMap<Long, Ticket> reservedTicketMap;
    AtomicLong nextTicketID;
    Seat[][][] seats;
    private Random seatBegin;
    private LongAdder[][][] inquiryCache;

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
        seatBegin = new Random();
        seatSize = coachnum * seatNum;
        nextTicketID = new AtomicLong(1);
        reservedTicketMap = new ConcurrentHashMap<Long, Ticket>();
        inquiryCache = new LongAdder[routeNum + 1][stationNum + 1][stationNum + 1];
        for (int i = 1; i <= routeNum; ++i) {
            for (int departure = 1; departure <= stationNum; ++departure) {
                for (int arrival = 1; arrival <= stationNum; ++arrival) {
                    inquiryCache[i][departure][arrival] = new LongAdder();
                    inquiryCache[i][departure][arrival].add(seatSize);
                }
            }
        }
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

        return inquiryCache[route][departure][arrival].intValue();

        // int ans = 0;
        // for (int coachIndex = 1; coachIndex <= coachNum; ++coachIndex) {
        // for (int seatIndex = 1; seatIndex <= seatNum; ++seatIndex) {
        // Seat s = seats[route][coachIndex][seatIndex];
        // if (s.available(departure, arrival)) {
        // ++ans;
        // }
        // }
        // }
        // return ans;
    }

    private void inquiryCacheDecrement(long oldValue, long newValue, int route, int departure, int arrival) {
        long section;
        // update the sections that will be influenced
        for (int i = 1; i <= arrival - 1; ++i) {
            for (int j = i + 1; j <= stationNum; ++j) {
                section = (1 << j) - (1 << i);
                // available before buying
                // unavailable after buying
                if (((section & oldValue) == 0) &&
                        ((section & newValue) != 0)) {
                    inquiryCache[route][i][j].decrement();
                }
            }
        }
    }

    private void inquiryCacheIncrement(long oldValue, long newValue, int route, int departure, int arrival) {
        long section;
        // update the sections that will be influenced
        for (int i = 1; i <= arrival - 1; ++i) {
            for (int j = i + 1; j <= stationNum; ++j) {
                section = (1 << j) - (1 << i);
                // unavailable before refunding
                // available after refunding
                if (((section & oldValue) != 0) &&
                        ((section & newValue) == 0)) {
                    inquiryCache[route][i][j].increment();
                }
            }
        }
    }

    @Override
    public Ticket buyTicket(String passenger, int route, int departure, int arrival) {
        if (!verify(route, departure, arrival)) {
            return null;
        }

        if (inquiryCache[route][departure][arrival].intValue() == 0) {
            return null;
        }

        Ticket ticket = null;

        int begin = seatBegin.nextInt(seatSize);
        int index;
        int coachIndex;
        int seatIndex;
        for (int i = 0; i < seatSize; ++i) {
            index = (begin + i) % seatSize;
            coachIndex = index / seatNum + 1;
            seatIndex = index % seatNum + 1;
            Seat s = seats[route][coachIndex][seatIndex];
            Result result = s.buy(departure, arrival);
            if (result.success) {
                ticket = new Ticket();
                ticket.tid = nextTicketID.getAndIncrement();
                ticket.passenger = passenger;
                ticket.route = route;
                ticket.coach = coachIndex;
                ticket.seat = seatIndex;
                ticket.departure = departure;
                ticket.arrival = arrival;
                reservedTicketMap.put(ticket.tid, ticket);
                inquiryCacheDecrement(result.oldValue, result.newValue, route, departure,
                arrival);
                return ticket;
            }
        }

        // for (int coachIndex = 1; coachIndex <= coachNum; ++coachIndex) {
        //     for (int seatIndex = 1; seatIndex <= seatNum; ++seatIndex) {
        //         Seat s = seats[route][coachIndex][seatIndex];
        //         Result result = s.buy(departure, arrival);
        //         if (result.success) {
        //             ticket = new Ticket();
        //             ticket.tid = nextTicketID.getAndIncrement();
        //             ticket.passenger = passenger;
        //             ticket.route = route;
        //             ticket.coach = coachIndex;
        //             ticket.seat = seatIndex;
        //             ticket.departure = departure;
        //             ticket.arrival = arrival;
        //             reservedTicketMap.put(ticket.tid, ticket);
        //             inquiryCacheDecrement(result.oldValue, result.newValue, route, departure,
        //                     arrival);
        //             return ticket;
        //         }
        //     }
        // }

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
                Result result = s.refund(ticket.departure, ticket.arrival);
                if (result.success) {
                    reservedTicketMap.remove(ticket.tid);
                    inquiryCacheIncrement(result.oldValue, result.newValue, ticket.route, ticket.departure,
                            ticket.arrival);
                }
                return result.success;
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
