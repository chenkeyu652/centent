package com.centent.cache.memory;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class MemoryCacheTest {

    @Test
    void lock() {
        // 起10个异步线程执行synchronizedTest
        for (int i = 0; i < 10; i++) {
            // 生成1-3的随机值
            String s = String.valueOf((int) (Math.random() * 3 + 1));
            log.info("Thread start: " + Thread.currentThread().getName());
            new Thread(() -> {
                synchronizedTest(s);
            }).start();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void synchronizedTest(String str) {
        // intern保证能锁住同一个字符串
        synchronized (str.intern()) {
            log.info("Thread locked str: {}, thread: {}", str, Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    void get() {
        MemoryCache memoryCache = new MemoryCache();
        Integer o = memoryCache.get("key", () -> 22);
        System.out.println(o);
        Integer o1 = memoryCache.get("key", () -> 33);
        System.out.println(o1);
        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Integer o2 = memoryCache.get("key");
        System.out.println(o2);
    }
}