package com.chen;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

public class Ticket12306 implements Runnable{
    private int tickets = 10;   //票数
    private InterProcessMutex interProcessMutex = null;

    public Ticket12306(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000,10);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("192.168.35.129:2181")
                .sessionTimeoutMs(60*1000)
                .connectionTimeoutMs(15*1000)
                .retryPolicy(retryPolicy)
                .namespace("itheima")
                .build();
        client.start();
        interProcessMutex = new InterProcessMutex(client,"/lock");
    }
    @Override
    public void run() {
        while(true){
            if(tickets > 0){
                try{
                    interProcessMutex.acquire(2, TimeUnit.SECONDS);
                    if(tickets > 0){
                        System.out.println(Thread.currentThread().getName() + tickets);
                        tickets--;
                    }
                }catch (Exception e){

                }finally {
                    try {
                        interProcessMutex.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
