package com.chen;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class CuratorTest {

    private CuratorFramework client;

    @Before
    public void testConnect(){
        //第一种方式
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000,10);
        //CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.35.129:2181",60*1000,15*1000,retryPolicy);
        //开启连接
        //client.start();
        //第二种方式
        client = CuratorFrameworkFactory.builder().connectString("192.168.35.129:2181")
                .sessionTimeoutMs(60*1000)
                .connectionTimeoutMs(15*1000)
                .retryPolicy(retryPolicy)
                .namespace("itheima")
                .build();
        client.start();
    }

    @After
    public void close(){
        if(client != null){
            client.close();
        }
    }

    /**
     * 创建节点：持久，零时，顺序
     * 1.基本创建
     * 2.创建节点带有数据
     * 3.设置节点类型
     * 4.创建多级节点 ,/app1/p1
     */
    @Test
    public void testCreate() throws Exception {
        //1.基本创建
        String path = client.create().forPath("/app1");
        System.out.println(path);
    }
    @Test
    public void testCreate2() throws Exception {
        //1.创建节点带有数据
        String path = client.create().forPath("/app2","hello".getBytes());
        System.out.println(path);
    }
    @Test
    public void testCreate3() throws Exception {
        //1.设置节点类型
        String path = client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/app3");
        System.out.println(path);
    }
    @Test
    public void testCreate4() throws Exception{
        //设置多级节点
        String path = client.create().creatingParentsIfNeeded().forPath("/app4/p1");
        System.out.println(path);
    }

    /**
     * 查询
     *  1.查询数据：get
     *  2.查询子节点：ls
     *  3.查询节点状态信息：ls -s
     * @throws Exception
     */
    @Test
    public void testGet() throws Exception{
        byte[] data = client.getData().forPath("/app1");
        System.out.println(new String(data));
    }
    @Test
    public void testGet2() throws Exception{
        List<String> paths = client.getChildren().forPath("/app4");
        System.out.println(paths);
    }
    @Test
    public void testGet3() throws Exception{
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath("/app1");
        System.out.println(stat);
    }

    /**
     * 修改
     *  1.修改数据
     *  2.根据版本修改
     */
    @Test
    public void testSet() throws Exception{
        client.setData().forPath("/app1","陈敏好学".getBytes());

    }
    @Test
    public void testSetForVersion() throws Exception{
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath("/app1");
        int version = stat.getVersion();
        System.out.println(version);
        client.setData()
                .withVersion(version)
                .forPath("/app1","chenmin".getBytes());
    }

    /**
     * 删除
     * delete
     * deleteall
     * 必须成功的删除
     * 回调
     */
    @Test
    public void testDelete() throws Exception{
        client.delete().forPath("/app1");
    }
    @Test
    public void testDelete2() throws Exception{
        client.delete().deletingChildrenIfNeeded().forPath("/app4");
    }
    @Test
    public void testDelete3() throws Exception{
        client.delete().guaranteed().forPath("/app4");
    }
    @Test
    public void testDelete4() throws Exception{
        //回调
        client.delete().guaranteed().inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println("我被删除了");
                System.out.println(curatorEvent);
            }
        }).forPath("/app2");
    }
}
