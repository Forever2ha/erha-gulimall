package com.atguigu.gulimall.product.web;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Controller
@Slf4j
public class IndexController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        // 查出所有一级分类
        model.addAttribute("categorys",
                categoryService.getLevel1Category()
                );
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/json/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        Map<String, List<Catalog2Vo>> map = categoryService.getCatelogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public R hello() throws InterruptedException {
        RLock lock = redissonClient.getLock("mylock");
        lock.lock(10, TimeUnit.SECONDS);

        try {
            log.error("加锁成功,开始执行业务："+Thread.currentThread().getName());
            Thread.sleep(20000);
        }finally {
            lock.unlock();
            log.error("解锁成功：执行完毕"+Thread.currentThread().getName());
        }

        return R.ok("你好");
    }

    @ResponseBody
    @GetMapping("/write")
    public R write(){
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("rwLock");
        RLock rLock = rwLock.writeLock();
        try {

            rLock.lock();
            String s = UUID.randomUUID().toString();
            log.error("开始写入数据:{}",s);
            redisTemplate.opsForValue().set("value",s);
            Thread.sleep(5000);
            return R.ok("写入完毕："+s);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            rLock.unlock();
        }
    }
    @ResponseBody
    @GetMapping("/read")
    public R read(){
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("rwLock");
        RLock rLock = rwLock.readLock();
        try {
            rLock.lock();
            String s = redisTemplate.opsForValue().get("value");
            log.error("读取数据:{}",s);
            Thread.sleep(2000);

            return R.ok("读取完毕："+s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            rLock.unlock();
        }

    }

    @ResponseBody
    @GetMapping("/park")
    public R park() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.acquire();
        return R.ok("占到位置了！");
    }

    @ResponseBody
    @GetMapping("/go")
    public R go() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release();
        return R.ok("释放车位！");
    }

}
