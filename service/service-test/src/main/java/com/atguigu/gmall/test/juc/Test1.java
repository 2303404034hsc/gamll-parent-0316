package com.atguigu.gmall.test.juc;

import com.atguigu.gmall.model.product.SkuInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author ccc
 * @create 2020-08-26 15:25
 */
public class Test1 {
    public static void main(String[] args) throws Exception {

        Double price = 0d;

        Map<String,Object> map = new HashMap<>();

        //查询skuInfo
        CompletableFuture<SkuInfo> completableFutureSkuInfo = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo = new SkuInfo();

                skuInfo.setSkuName("测试商品");
                skuInfo.setCategory3Id(61L);
                map.put("skuInfo",skuInfo);
                return skuInfo;
            }
        });

        //查询skuPrice
        CompletableFuture completableFutureSkuPrice = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                map.put("price",100d);
            }
        });
        //查询分类数据
        CompletableFuture<Void> completableFutureCategory = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                map.put("category3Id",skuInfo.getCategory3Id());
            }
        });

        //异常处理
        CompletableFuture exceptionally = completableFutureSkuPrice.exceptionally(new Function() {
            @Override
            public Object apply(Object o) {
                return null;
            }
        });

        //组合执行
        CompletableFuture.anyOf(completableFutureSkuInfo,completableFutureSkuPrice,completableFutureCategory,exceptionally).join();

        System.out.println(map);

        //级联写法
        //getDoubleCompletableFuture();
        //自由变量
        //completableFutureFree();
    }

    private static void completableFutureFree() throws InterruptedException, ExecutionException {
        CompletableFuture<Double> completableFuture = CompletableFuture.supplyAsync(new Supplier<Double>() {
            @Override
            public Double get() {
                System.out.println("CompletableFuture");
                int i = 1 / 0;
                return 100d;
            }
        });

        CompletableFuture<Double> exceptionally = completableFuture.exceptionally(new Function<Throwable, Double>() {
            @Override
            public Double apply(Throwable throwable) {
                System.out.println("exception" + throwable);
                return 0d;
            }
        });

        CompletableFuture<Double> completableFuture1 = completableFuture.whenComplete(new BiConsumer<Double, Throwable>() {
            @Override
            public void accept(Double aDouble, Throwable throwable) {
                System.out.println("Complete");
                System.out.println("aDouble = " + aDouble);
                System.out.println("throwable = " + throwable);
            }
        });

        Double aDouble =completableFuture1.get();
        System.out.println("aDouble = " + aDouble);
    }

    //级联写法
    private static void getDoubleCompletableFuture() throws Exception{

        CompletableFuture<Double> completableFuture = CompletableFuture.supplyAsync(new Supplier<Double>() {
            @Override
            public Double get() {
                System.out.println("CompletableFuture");
                int  i = 1/0;
                return 100d;
            }
        }).exceptionally(new Function<Throwable, Double>() {
            @Override
            public Double apply(Throwable throwable) {
                System.out.println("exception" + throwable);
                return 0d;
            }
        }).whenComplete(new BiConsumer<Double, Throwable>() {
            @Override
            public void accept(Double aDouble, Throwable throwable) {
                System.out.println("Complete");
                System.out.println("aDouble = " + aDouble);
                System.out.println("throwable = " + throwable);
            }
        });


        Double aDouble =completableFuture.get();
        System.out.println("aDouble = " + aDouble);
    }
}
