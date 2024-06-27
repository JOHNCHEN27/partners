package com.lncanswer.findingpartnersbackend.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/27 18:09
 */
@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void testRedisson(){
        //java-list 与 Redisson list对比
        List<String> list = new ArrayList<>();
        list.add("qweqw");
        System.out.println(list.get(0));
        list.remove(0);

        //获取一个List 指定key作为list的名字
        RList<String> list1 = redissonClient.getList("lnc");
        list1.add("asdf");
        System.out.println(list1.get(0));
    }
}

