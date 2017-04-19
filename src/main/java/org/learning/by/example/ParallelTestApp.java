package org.learning.by.example;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ParallelTestApp {

    private final static Logger logger = Logger.getLogger(ParallelTestApp.class);

    private int numElements;
    private int numParallelTasks;


    ParallelTestApp(int numElements, int numParallelTasks) {
        this.numElements = numElements;
        this.numParallelTasks = numParallelTasks;

        logger.info(getFieldAndValue(this.numElements));
        logger.info(getFieldAndValue(this.numParallelTasks));
    }

    private static AtomicInteger sum = new AtomicInteger(0);

    Consumer<Integer> notBlockingOperation = (item) -> {
        logger.debug(getFieldName(this.notBlockingOperation));
    };

    Consumer<Integer> blockingOperation = (item) -> {
        sum.addAndGet(item);
        logger.debug(getFieldName(this.blockingOperation));
    };

    Function<Integer, IntStream> supplier = (numbers) -> IntStream.range(1, numbers);


    BiConsumer<IntStream, Consumer<Integer>> parallelStream = (data, worker) -> {
        ForkJoinPool pool = new ForkJoinPool(numParallelTasks);
        pool.execute(() -> data.parallel().forEach(worker::accept));
    };

    BiConsumer<IntStream, Consumer<Integer>> sequentialStream = (data, worker) -> {
        data.forEach(worker::accept);
    };

    BiConsumer<BiConsumer<IntStream, Consumer<Integer>>, Consumer<Integer>> perform = (processor, worker) -> {
        processor.accept(supplier.apply(numElements), worker);
    };

    String getFieldName(Object find) {
        for (Field field : ParallelTestApp.class.getDeclaredFields()) {

            try {
                if (field.get(this).equals(find)) {
                    return field.getName();
                }
            } catch (IllegalAccessException ignore) {
            }
        }
        return "";
    }

    private String getFieldAndValue(Object find) {
        return getFieldName(find) + "=" + find;
    }

    void execute(){
        Stream.of(this.blockingOperation, this.notBlockingOperation).forEach(worker -> {

            logger.info(this.getFieldName(worker) + " test");

            Stream.of(this.parallelStream, this.sequentialStream).forEach(processor -> {

                long startTime = System.currentTimeMillis();

                logger.info(this.getFieldName(processor) + " begin");
                this.perform.accept(processor, worker);

                long estimatedTime = System.currentTimeMillis() - startTime;

                logger.info(this.getFieldName(processor) + " end in " + estimatedTime + " seconds");
            });
        });
    }

    public static void main(String[] args) {

        System.out.println("please run the test");

    }
}
