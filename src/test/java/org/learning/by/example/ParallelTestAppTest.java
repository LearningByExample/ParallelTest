package org.learning.by.example;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Category(FastTests.class)
public class ParallelTestAppTest {

    private final static int SMALL_NUM_ELEMENTS = 5;
    private final static int SMALL_NUM_PARALLEL_TASKS = 4;

    private final static Logger logger = LoggerFactory.getLogger(ParallelTestAppTest.class);

    @Test
    public void ParallelTestApp() {
        logger.info("ParallelTestApp");
        ParallelTestApp app = new ParallelTestApp(SMALL_NUM_ELEMENTS, SMALL_NUM_PARALLEL_TASKS);
    }

    @Test
    public void getFieldName() {
        logger.info("getFieldName");
        ParallelTestApp app = new ParallelTestApp(SMALL_NUM_ELEMENTS, SMALL_NUM_PARALLEL_TASKS);

        assertThat(app.getFieldName(app.numElements), is("numElements"));
    }

    @Test
    public void getFieldAndValue() {
        logger.info("getFieldAndValue");
        ParallelTestApp app = new ParallelTestApp(SMALL_NUM_ELEMENTS, SMALL_NUM_PARALLEL_TASKS);

        assertThat(app.getFieldAndValue(app.numElements), is("numElements=" + SMALL_NUM_ELEMENTS));
    }

    @Test
    public void supplier() {
        logger.info("supplier");
        ParallelTestApp app = new ParallelTestApp(SMALL_NUM_ELEMENTS, SMALL_NUM_PARALLEL_TASKS);

        IntStream intStream = app.supplier.apply(SMALL_NUM_ELEMENTS);

        assertThat((int) intStream.count(), is(SMALL_NUM_ELEMENTS));

        assertThat(app.supplier.apply(5).sum(), is(4 + 3 + 2 + 1 + 0));
    }

    @Test
    public void blockingOperation() {
        logger.info("blockingOperation");
        ParallelTestApp app = new ParallelTestApp(SMALL_NUM_ELEMENTS, SMALL_NUM_PARALLEL_TASKS);

        app.blockingOperation.accept(2);
        app.blockingOperation.accept(2);

        assertThat(ParallelTestApp.sum.get(), is(4));
    }

    @Test
    public void sequentialStream() {
        logger.info("sequentialStream");
        ParallelTestApp app = new ParallelTestApp(SMALL_NUM_ELEMENTS, SMALL_NUM_PARALLEL_TASKS);

        app.sequentialStream.accept(app.supplier.apply(5), app.blockingOperation);
        assertThat(ParallelTestApp.sum.get(), is(4 + 3 + 2 + 1 + 0));
    }

    @Test
    public void sequentialStreamLambda() {
        logger.info("sequentialStreamLambda");
        ParallelTestApp app = new ParallelTestApp(SMALL_NUM_ELEMENTS, SMALL_NUM_PARALLEL_TASKS);

        app.sequentialStream.accept(app.supplier.apply(5), (item) -> {
            ParallelTestApp.sum.set(ParallelTestApp.sum.get() - item);
        });

        assertThat(ParallelTestApp.sum.get(), is(0 - 1 - 2 - 3 - 4));
    }

    @Test
    public void parallelStream() {
        logger.info("parallelStream");
        ParallelTestApp app = new ParallelTestApp(SMALL_NUM_ELEMENTS, SMALL_NUM_PARALLEL_TASKS);

        app.parallelStream.accept(app.supplier.apply(5), app.blockingOperation);
        assertThat(ParallelTestApp.sum.get(), is(4 + 3 + 2 + 1 + 0));
    }

    @Test
    public void sequentialPerform() {
        logger.info("sequentialPerform");
        ParallelTestApp app = new ParallelTestApp(SMALL_NUM_ELEMENTS, SMALL_NUM_PARALLEL_TASKS);

        app.perform.accept(app.sequentialStream, app.blockingOperation);

        assertThat(ParallelTestApp.sum.get(), is(4 + 3 + 2 + 1 + 0));
    }

    @Test
    public void parallelPerform() {
        logger.info("sequentialPerform");
        ParallelTestApp app = new ParallelTestApp(SMALL_NUM_ELEMENTS, SMALL_NUM_PARALLEL_TASKS);

        app.perform.accept(app.parallelStream, app.blockingOperation);

        assertThat(ParallelTestApp.sum.get(), is(4 + 3 + 2 + 1 + 0));
    }

    @Test
    public void sequentialPerformLambda() {
        logger.info("sequentialPerformLambda");
        ParallelTestApp app = new ParallelTestApp(SMALL_NUM_ELEMENTS, SMALL_NUM_PARALLEL_TASKS);

        app.perform.accept((data, worker) -> {
            data.filter( number -> number > 2 ).forEach(worker::accept);
        }, (item)->{
            ParallelTestApp.sum.set(ParallelTestApp.sum.get() - item);
        });

        assertThat(ParallelTestApp.sum.get(), is(0 -4 - 3 ));
    }

    @Test
    public void execute() {
        logger.info("execute");
        ParallelTestApp app = new ParallelTestApp(SMALL_NUM_ELEMENTS, SMALL_NUM_PARALLEL_TASKS);

        app.execute();
    }

}
