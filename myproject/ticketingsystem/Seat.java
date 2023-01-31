/*
 * @Author: starrysky9959 starrysky9651@outlook.com
 * @Date: 2022-11-11 10:39:22
 * @LastEditors: starrysky9959 starrysky9651@outlook.com
 * @LastEditTime: 2022-12-17 00:23:19
 * @Description:  
 */
package ticketingsystem;

import java.util.concurrent.atomic.AtomicLong;

public class Seat {
    private AtomicLong reservedStation;

    public Seat() {
        reservedStation = new AtomicLong(0);
    }

    public Result buy(int departure, int arrival) {
        long section = (1 << arrival) - (1 << departure);
        int i = 0;
        while (true) {
            long oldValue = reservedStation.get();
            // seat available, try CAS opereation
            if ((oldValue & section) == 0) {
                long newValue = oldValue | section;
                if (reservedStation.compareAndSet(oldValue, newValue)) {
                    return new Result(true, oldValue, newValue);
                } else {
                    i++;
                    if (i >= 5) {
                        i = 0;
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {

                        }
                    }
                }
            } else { // not available
                return new Result();
            }
        }
    }

    public Result refund(int departure, int arrival) {
        long section = (1 << arrival) - (1 << departure);
        int i = 0;
        while (true) {
            long oldValue = reservedStation.get();
            // refund legal
            if ((oldValue & section) == section) {
                long newValue = oldValue & (~section);
                if (reservedStation.compareAndSet(oldValue, newValue)) {
                    return new Result(true, oldValue, newValue);
                } else {
                    i++;
                    if (i >= 5) {
                        i = 0;
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {

                        }
                    }
                }
            } else { // not available
                return new Result();
            }
        }
    }

    public boolean available(int departure, int arrival) {
        long section = (1 << arrival) - (1 << departure);
        return (reservedStation.get() & section) == 0;
    }
}

class Result {
    public boolean success;
    public long oldValue;
    public long newValue;

    public Result() {
        this.success = false;
    }

    public Result(boolean success, long oldValue, long newValue) {
        this.success = success;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}