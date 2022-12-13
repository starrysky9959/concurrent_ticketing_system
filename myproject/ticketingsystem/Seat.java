/*
 * @Author: starrysky9959 starrysky9651@outlook.com
 * @Date: 2022-11-11 10:39:22
 * @LastEditors: starrysky9959 starrysky9651@outlook.com
 * @LastEditTime: 2022-12-13 14:46:13
 * @Description:  
 */
package ticketingsystem;

import java.util.concurrent.atomic.AtomicLong;

public class Seat {
    private AtomicLong reservedStation;

    public Seat() {
        reservedStation = new AtomicLong(0);
    }

    public boolean buy(int departure, int arrival) {
        long section = (1 << arrival) - (1 << departure);
        while (true) {
            long oldValue = reservedStation.get();
            // seat available, try CAS opereation
            if ((oldValue & section) == 0) {
                long newValue = oldValue | section;
                if (reservedStation.compareAndSet(oldValue, newValue)) {
                    return true;
                }
            } else { // not available
                return false;
            }
        }
    }

    public boolean refund(int departure, int arrival) {
        long section = (1 << arrival) - (1 << departure);

        while (true) {
            long oldValue = reservedStation.get();
            // refund legal
            if ((oldValue & section) == section) {
                long newValue = oldValue & (~section);
                if (reservedStation.compareAndSet(oldValue, newValue)) {
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public boolean available(int departure, int arrival) {
        long section = (1 << arrival) - (1 << departure);
        return (reservedStation.get() & section) == 0;
    }
}