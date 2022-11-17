/*
 * @Author: starrysky9959 starrysky9651@outlook.com
 * @Date: 2022-11-10 16:29:45
 * @LastEditors: starrysky9959 starrysky9651@outlook.com
 * @LastEditTime: 2022-11-17 23:31:37
 * @Description:  
 */
package ticketingsystem;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.naming.spi.DirStateFactory.Result;

public class Test {
    enum Task {
        REFUND,
        BUY,
        INQUERY
    }

    final static int ROUTE_NUM = 3;
    final static int COACH_NUM = 5;
    final static int SEAT_NUM = 5;
    final static int STATION_NUM = 10;
    final static int PER_THREAD_TASK = 10000;

    final static int REFUND_RATIO = 10;
    final static int BUY_RATIO = 30;
    final static int INQUERY_RATIO = 60;
    final static int TOTAL_RATIO = REFUND_RATIO + BUY_RATIO + INQUERY_RATIO;

    final static int[] THREAD_NUM_TESTCASES = { 4, 8, 16, 32, 64 };

    final static Random rand = new Random();

    static TicketingDS tds;

    static Thread[] threads;
    static long[] refundLatency;
    static long[] inquiryLatency;
    static long[] buyLatency;
    static Writer writer;

    public static void main(String[] args) throws InterruptedException, IOException {
        writer = new OutputStreamWriter(new FileOutputStream("result.csv"), "UTF-8");
        writer.write("ThreadNum, RefundAvgLatency(ns), BuyAvgLatency(ns), InquiryAvgLatency(ns), Throughout(TPS)\n");

        System.out
                .println();
        for (int threadNum : THREAD_NUM_TESTCASES) {
            tds = new TicketingDS(ROUTE_NUM, COACH_NUM, ROUTE_NUM, STATION_NUM, threadNum);
            initTestCase(threadNum);
            test(threadNum);
            analyze(threadNum);
        }

        writer.close();
    }

    static void initFileWriter() {

    }

    static void initTestCase(int threadNum) {
        // threadNum = threadNum;
        threads = new Thread[threadNum];
        refundLatency = new long[threadNum];
        inquiryLatency = new long[threadNum];
        buyLatency = new long[threadNum];
        for (int i = 0; i < threadNum; ++i) {
            threads[i] = new MyThread(i);
        }
    }

    static void test(int threadNum) {
        for (int i = 0; i < threadNum; ++i) {
            threads[i].start();
        }
        for (int i = 0; i < threadNum; ++i) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    static void analyze(int threadNum) throws IOException {
        long refundTotalLatency = 0;
        long buyTotalLatency = 0;
        long inquiryTotalLatency = 0;
        for (int i = 0; i < threadNum; ++i) {
            refundTotalLatency += refundLatency[i];
            buyTotalLatency += buyLatency[i];
            inquiryTotalLatency += inquiryLatency[i];
        }

        long refundAvgLatency = refundTotalLatency / (PER_THREAD_TASK / TOTAL_RATIO * REFUND_RATIO * threadNum);
        long buyAvgLatency = buyTotalLatency / (PER_THREAD_TASK / TOTAL_RATIO * BUY_RATIO * threadNum);
        long inquiryAvgLatency = inquiryTotalLatency / (PER_THREAD_TASK / TOTAL_RATIO * INQUERY_RATIO * threadNum);

        // TPS
        double throughout = (PER_THREAD_TASK * threadNum) * 10e9
                / (refundTotalLatency + buyTotalLatency + inquiryTotalLatency);
        writer.write(
                String.format("%d, %d, %d, %d, %d\n", threadNum, refundAvgLatency, buyAvgLatency, inquiryAvgLatency,
                        (long) throughout));

    }

    public static String getPassengerName() {
        long uid = rand.nextInt(PER_THREAD_TASK);
        return "passenger" + uid;
    }

    static class MyThread extends Thread {
        List<Task> tasks;
        List<Ticket> soldTickets;
        int threadID;

        MyThread(int id) {
            threadID = id;
            tasks = new ArrayList<>(PER_THREAD_TASK);
            for (int i = 0; i < PER_THREAD_TASK / TOTAL_RATIO; ++i) {
                for (int j = 0; j < REFUND_RATIO; ++j)
                    tasks.add(Task.REFUND);
                for (int j = 0; j < BUY_RATIO; ++j)
                    tasks.add(Task.BUY);
                for (int j = 0; j < INQUERY_RATIO; ++j)
                    tasks.add(Task.INQUERY);
            }
            Collections.shuffle(tasks);
            soldTickets = new ArrayList<>();
        }

        @Override
        public void run() {
            String passenger;
            int route;
            int departure;
            int arrival;
            long start;
            long end;
            long costTime;

            for (Task task : tasks) {
                switch (task) {
                    case REFUND:
                        if (!soldTickets.isEmpty()) {
                            int size = soldTickets.size();
                            int index = rand.nextInt(size);
                            Ticket ticket = soldTickets.get(index);
                            start = System.nanoTime();
                            boolean result = tds.refundTicket(ticket);
                            end = System.nanoTime();
                            costTime = end - start;
                            refundLatency[threadID] += costTime;
                            assert result;
                            assert soldTickets.remove(index) != null;
                        }
                        break;
                    case BUY:
                        passenger = getPassengerName();
                        route = rand.nextInt(ROUTE_NUM) + 1;
                        departure = rand.nextInt(STATION_NUM - 1) + 1;
                        arrival = departure + rand.nextInt(STATION_NUM - departure) + 1;
                        start = System.nanoTime();
                        Ticket ticket = tds.buyTicket(passenger, route, departure, arrival);
                        end = System.nanoTime();
                        costTime = end - start;
                        buyLatency[threadID] += costTime;
                        if (ticket != null) {
                            soldTickets.add(ticket);
                        }
                        break;
                    case INQUERY:
                        route = rand.nextInt(ROUTE_NUM) + 1;
                        departure = rand.nextInt(STATION_NUM - 1) + 1;
                        arrival = departure + rand.nextInt(STATION_NUM - departure) + 1;
                        start = System.nanoTime();
                        tds.inquiry(route, departure, arrival);
                        end = System.nanoTime();
                        costTime = end - start;
                        inquiryLatency[threadID] += costTime;
                        break;
                }
            }
        }
    }
}
