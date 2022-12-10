/*
 * @Author: starrysky9959 starrysky9651@outlook.com
 * @Date: 2022-11-11 10:39:22
 * @LastEditors: starrysky9959 starrysky9651@outlook.com
 * @LastEditTime: 2022-11-11 17:13:29
 * @Description:  
 */
package ticketingsystem;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

public class Seat {

    // final StampedLock lock;
    long readStamp;
    long writeStamp;
    // final Lock readLock;
    // final Lock writeLock;
    private AtomicLong reservedStation;

    public Seat() {
        reservedStation = new AtomicLong(0);
        // lock = new StampedLock();
        // readLock = lock.readLock();
        // writeLock = lock.writeLock();
    }

    // void lockRead() {
    //     readStamp = lock.readLock();
    // }

    // void unlockRead() {
    //     lock.unlock(readStamp);
    // }

    // void lockWrite() {
    //     writeStamp = lock.writeLock();
    // }

    // void unlockWrite() {
    //     lock.unlock(writeStamp);
    // }

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
        // lock.writeLock().lock();
        // Pair p = new Pair(ticket.departure, ticket.arrival);
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

class Pair {
    public int departure;
    public int arrival;

    @Override
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof Pair)) {
            Pair p = (Pair) obj;
            return departure == p.departure && arrival == p.arrival;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return departure ^ arrival;
    }

    public Pair(int departure, int arrival) {
        this.departure = departure;
        this.arrival = arrival;
    }
}