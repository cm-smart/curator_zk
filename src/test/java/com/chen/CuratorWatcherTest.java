package com.chen;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class CuratorWatcherTest {

    private CuratorFramework client;

    @Before
    public void testConnect(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000,10);
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
     * 给一个节点注册监听器
     * @throws Exception
     */
    @Test
    public void testNodeCache() throws Exception {
        //1.创建nodeCache
        NodeCache nodeCache = new NodeCache(client,"/app1");
        //2.注册监听
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("节点变化");
                //获取节点后的数据
                byte[] data = nodeCache.getCurrentData().getData();
                System.out.println(new java.lang.String());
            }
        });
        //3.开启监听 如果设置为true,则开启监听器，加载缓冲数据
        nodeCache.start(true);

        while (true){
            Thread.sleep(1000);
        }
    }

    @Test
    public void testPathChildrenCache() throws Exception{
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client,"/app2",true);
        //注册监听器
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                System.out.println("子节点变化");
                System.out.println(pathChildrenCacheEvent);

                PathChildrenCacheEvent.Type type = pathChildrenCacheEvent.getType();
                //判断类型是否是update
                if(type.equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
                    byte[] data = pathChildrenCacheEvent.getData().getData();
                    System.out.println(new String(data));
                }
            }
        });
        //开启监听
        pathChildrenCache.start();

        while (true){
            Thread.sleep(1000);
        }
    }
    @Test
    public void testTreeCache() throws Exception {
        TreeCache treeCache = new TreeCache(client,"/app2");
        //注册监听器
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                System.out.println("节点变化了");
                System.out.println(treeCacheEvent);
            }
        });
        //开启监听
        treeCache.start();
        while (true){
            Thread.sleep(1000);
        }
    }
}
