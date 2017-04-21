package org.learning.by.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ParallelTestApp {

    private final static Logger logger = LoggerFactory.getLogger(ParallelTestApp.class);

    int numElements;
    int numParallelTasks;


    ParallelTestApp(int numElements, int numParallelTasks) {
        this.numElements = numElements;
        this.numParallelTasks = numParallelTasks;
        sum.set(0);

        logger.info(getFieldAndValue(this.numElements));
        logger.info(getFieldAndValue(this.numParallelTasks));
    }

    static AtomicInteger sum = new AtomicInteger(0);

    Consumer<Integer> notBlockingOperation = (item) -> {
        logger.debug(getFieldName(this.notBlockingOperation));
    };

    Consumer<Integer> blockingOperation = (item) -> {
        sum.addAndGet(item);
        logger.debug(getFieldName(this.blockingOperation));
    };

    Function<Integer, IntStream> supplier = (numbers) -> IntStream.range(0, numbers);

    BiConsumer<IntStream, Consumer<Integer>> sequentialStream = (data, worker) -> {
        data.forEach(worker::accept);
    };

    BiConsumer<IntStream, Consumer<Integer>> parallelStream = (data, worker) -> {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(numParallelTasks));
        data.parallel().forEach(worker::accept);
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

    String getFieldAndValue(Object find) {
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

        System.out.println("please run: mvnw test -P slowTests");

    }
}
