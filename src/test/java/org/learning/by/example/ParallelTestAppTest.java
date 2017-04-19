package org.learning.by.example;

import org.apache.log4j.Logger;
import org.junit.Test;

public class ParallelTestAppTest
{
    private final static int BIG_NUM_ELEMENTS = 10000000;
    private final static int BIG_NUM_PARALLEL_TASKS = 10000;
    private final static int SMALL_NUM_ELEMENTS = 100;
    private final static int SMALL_NUM_PARALLEL_TASKS = 4;

    private final static Logger logger = Logger.getLogger(ParallelTestApp.class);

    @Test
    public void bigElementsAndBigTasks(){

        logger.info("bigElementsAndBigTasks");

        ParallelTestApp app = new ParallelTestApp(BIG_NUM_ELEMENTS, BIG_NUM_PARALLEL_TASKS);

        app.execute();
    }

    @Test
    public void smallElementsAndSmallTasks(){

        logger.info("smallElementsAndSmallTasks");
        ParallelTestApp app = new ParallelTestApp(SMALL_NUM_ELEMENTS, SMALL_NUM_PARALLEL_TASKS);

        app.execute();
    }

    @Test
    public void smallElementsAndBigTasks(){

        logger.info("smallElementsAndBigTasks");
        ParallelTestApp app = new ParallelTestApp(SMALL_NUM_ELEMENTS, BIG_NUM_PARALLEL_TASKS);

        app.execute();
    }

    @Test
    public void bigElementsAndSmallTasks(){

        logger.info("bigElementsAndSmallTasks");
        ParallelTestApp app = new ParallelTestApp(BIG_NUM_ELEMENTS, SMALL_NUM_PARALLEL_TASKS);

        app.execute();
    }


}
