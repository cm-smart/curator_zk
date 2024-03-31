package com.chen;

public class LockTest {
    public static void main(String[] args) throws InterruptedException {
        Ticket12306 ticket12306 = new Ticket12306();

        Thread t1 = new Thread(ticket12306,"飞猪");
        Thread t2 = new Thread(ticket12306,"携程");

        t1.start();
        t2.start();
    }
}
