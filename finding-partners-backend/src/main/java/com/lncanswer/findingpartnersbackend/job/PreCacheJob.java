package com.lncanswer.findingpartnersbackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lncanswer.findingpartnersbackend.model.domain.User;
import com.lncanswer.findingpartnersbackend.service.UserService;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import io.lettuce.core.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.lncanswer.findingpartnersbackend.constant.RedisConstant.PARTNERS_RECOMMEND_KEY;
import static com.lncanswer.findingpartnersbackend.constant.RedisConstant.PARTNERS_RECOMMEND_KEY_TTL;
import static com.lncanswer.findingpartnersbackend.constant.RedissonConstant.MAIN_RECOMMEND_DOCACHE_KEY;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/27 16:18
 * 缓存定时任务
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;
    //暂时缓存重点用户
    private List<Long> mainUserList = Arrays.asList(8L);

    //cron表达式 自定义定时任务执行时间（网上查阅在线计算）
    @Scheduled(cron = "0 2 20 * * * ") //20点2分
    // 分0秒执行一次
    public void doCacheRecommendUser(){
        //利用Redisson获取到锁
        RLock lock = redissonClient.getLock(MAIN_RECOMMEND_DOCACHE_KEY);
       try {
           //await设置为0 保证每个线程只获取一次锁
           if (lock.tryLock(0,-1, TimeUnit.MILLISECONDS)){
               //对每个用户做缓存
               for (Long userId : mainUserList) {
                   QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                   Page<User> page = userService.page(new Page<>(1,20),queryWrapper);
                   if (page == null){
                       return;
                   }
                   String redisKey = PARTNERS_RECOMMEND_KEY + userId;
                   //写入缓存 设置过期时间
                   redisTemplate.opsForValue().set(redisKey,page,30000,TimeUnit.MILLISECONDS);
               }
           }
       } catch (Exception e){
           log.error("doCacheRecommendUser error", e);
       } finally {
           if (lock.isHeldByCurrentThread()){
               //判断是当前线程获取的锁，保证只释放自己的锁
               log.info("unLock: " + Thread.currentThread().getId());
               lock.unlock();
           }
       }

    }

}
