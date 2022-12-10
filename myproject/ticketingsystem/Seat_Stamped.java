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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

public class Seat_Stamped {

    final StampedLock lock;
    long readStamp;
    long writeStamp;
    // final Lock readLock;
    // final Lock writeLock;
    private Set<Pair> reservedStation;

    public Seat_Stamped() {
        reservedStation = new HashSet<Pair>();
        lock = new StampedLock();
        // readLock = lock.readLock();
        // writeLock = lock.writeLock();
    }

    void lockRead() {
        readStamp = lock.readLock();
    }

    void unlockRead() {
        lock.unlock(readStamp);
    }

    void lockWrite() {
        writeStamp = lock.writeLock();
    }

    void unlockWrite() {
        lock.unlock(writeStamp);
    }

    public boolean buy(int departure, int arrival) {
        // lock.writeLock().lock();
        if (!available(departure, arrival)) {

            return false;
        }

        boolean result = reservedStation.add(new Pair(departure, arrival));
        return result;
        // if (result) {
        // System.out.println("add failed");
        // for (Pair r : reservedStation) {
        // System.out.println(r.departure + ", " + r.arrival);
        // }
        // System.out.println("-------------");
        // System.out.println(departure + ", " + arrival);
        // System.out.println("-------------");
        // }
        // lock.writeLock().unlock();

        // return true;

    }

    public boolean refund(Ticket ticket) {
        // lock.writeLock().lock();
        Pair p = new Pair(ticket.departure, ticket.arrival);

        // System.out.println(reservedStation.size());
        boolean result = reservedStation.remove(p);
        // if (!result) {
        // for (Pair r : reservedStation) {
        // System.out.println(r.departure + ", " + r.arrival);
        // }
        // System.out.println("-------------");
        // }
        // System.out.println(reservedStation.size());
        // lock.writeLock().unlock();

        return result;
    }

    public boolean available(int departure, int arrival) {
        // lock.readLock().lock();
        for (Pair p : reservedStation) {
            if (departure >= p.arrival || arrival <= p.departure) {
                continue;
            }
            // lock.readLock().unlock();
            return false;
        }
        // lock.readLock().unlock();
        return true;

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