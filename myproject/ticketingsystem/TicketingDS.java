/*
 * @Author: starrysky9959 starrysky9651@outlook.com
 * @Date: 2022-11-10 16:29:45
 * @LastEditors: starrysky9959 starrysky9651@outlook.com
 * @LastEditTime: 2022-11-11 15:12:13
 * @Description:  
 */
package ticketingsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/*
 * 为简单起见，假设每个车次的 coachnum、seatnum 和 stationnum 都相同。车票涉及的各项参数均从 1 开始计数，例如车厢从 1 到 8 编号，车站从 1 到 10 编号等。
 */
public class TicketingDS implements TicketingSystem {
	// ToDo

	private final int routeNum;
	private final int coachNum;
	private final int seatNum;
	private final int stationNum;
	private final int threadNum;

	Map<Long, Ticket> reservedTicketMap;
	AtomicLong nextTicketID;

	Seat[][][] seats;

	public TicketingDS(int routenum, int coachnum, int seatnum, int stationnum, int threadnum) {
		this.routeNum = routenum;
		this.coachNum = coachnum;
		this.seatNum = seatnum;
		this.stationNum = stationnum;
		this.threadNum = threadnum;
		seats = new Seat[routeNum][coachNum][seatNum];
		for (int i = 0; i < routeNum; ++i) {
			for (int j = 0; j < coachNum; ++j) {
				for (int k = 0; k < seatNum; ++k) {
					seats[i][j][k] = new Seat();
				}
			}
		}

		nextTicketID = new AtomicLong(0);
		reservedTicketMap = new HashMap<Long, Ticket>();
	}

	private boolean verify(int route, int departure, int arrival) {
		 // arrival is always greater than departure
		return ((route >= 0 && route < routeNum) &&
				(departure >= 0 && departure < stationNum) &&
				(arrival >= 0 && arrival < stationNum) &&
				(departure < arrival));
	}

	@Override
	public int inquiry(int route, int departure, int arrival) {

		if (!verify(route, departure, arrival)) {
			return 0;
		}

		int ans = 0;
		for (int coachIndex = 0; coachIndex < coachNum; ++coachIndex) {
			for (int seatIndex = 0; seatIndex < seatNum; ++seatIndex) {
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
		for (int coachIndex = 0; coachIndex < coachNum; ++coachIndex) {
			for (int seatIndex = 0; seatIndex < seatNum; ++seatIndex) {
				Seat s = seats[route][coachIndex][seatIndex];
				if (s.available(departure, arrival)) {
					ticket = new Ticket();
					ticket.tid = nextTicketID.getAndIncrement();
					ticket.passenger = passenger;
					ticket.route = route;
					ticket.coach = coachIndex;
					ticket.seat = seatIndex;
					ticket.departure = departure;
					ticket.arrival = arrival;
					s.buy(departure, arrival);
					reservedTicketMap.put(ticket.tid, ticket);
					return ticket;
				}
			}
		}

		return null;

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

	@Override
	public boolean refundTicket(Ticket ticket) {

		Seat seat = seats[ticket.route][ticket.coach][ticket.seat];
		return seat.refund(ticket);
		// return false;

	}
}
