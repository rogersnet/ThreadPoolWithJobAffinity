package com.apg.threadpool;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ThreadPoolWithJobAffinityExecutorESTest {

    private AtomicInteger sampleInteger = new AtomicInteger(0);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void checkAndTestThreadPoolCreation(){
        ThreadPoolWithJobAffinity threadPool = new ThreadPoolWithJobAffinityExecutorES();
        assertNotNull(threadPool);
    }

    @Test
    public void checkAndTestThreadPoolSizeForEmptyThreadPool(){
        ThreadPoolWithJobAffinity threadPool = new ThreadPoolWithJobAffinityExecutorES();
        assertEquals(0, threadPool.poolSize());
    }

    @Test
    public void runAndCheckForTwoJobs(){
        Runnable r1 = new Runnable() {
            public void run() {
                sampleInteger.getAndIncrement();
            }
        };

        Runnable r2 = new Runnable() {
            public void run() {
                sampleInteger.getAndIncrement();
            }
        };

        ThreadPoolWithJobAffinity threadPool = new ThreadPoolWithJobAffinityExecutorES();
        threadPool.submit("1",r1);
        threadPool.submit("2",r2);

        //check if the thread pool has the right size
        assertEquals(2, threadPool.poolSize());

        //call shutdown and wait for the jobs to finish
        threadPool.shutdown();
        assertEquals(2,sampleInteger.get());
    }

    @Test
    public void checkJobSubmissionFailsPostShutdown(){
        Runnable r1 = new Runnable() {
            public void run() {
                sampleInteger.getAndIncrement();
            }
        };

        Runnable r2 = new Runnable() {
            public void run() {
                sampleInteger.getAndIncrement();
            }
        };

        ThreadPoolWithJobAffinity threadPool = new ThreadPoolWithJobAffinityExecutorES();
        threadPool.submit("1", r1);
        threadPool.submit("2", r2);

        //call shutdown and wait for the jobs to finish
        threadPool.shutdown();

        Runnable r3 = new Runnable() {
            public void run() {
                sampleInteger.getAndIncrement();
            }
        };

        //trying to submit a job executed by the same thread pool should result in an exception
        exception.expect(IllegalStateException.class);
        threadPool.submit("3", r3);
    }

    @Test
    public void checkTaskForMultipleJobsWithSameId() {
        Runnable r1 = new Runnable() {
            public void run() {
                sampleInteger.getAndIncrement();
            }
        };

        Runnable r2 = new Runnable() {
            public void run() {
                sampleInteger.getAndIncrement();
            }
        };

        ThreadPoolWithJobAffinity threadPool = new ThreadPoolWithJobAffinityExecutorES();
        threadPool.submit("1",r1);
        threadPool.submit("1",r2);

        //check the thread pool size, should be one
        assertEquals(1,threadPool.poolSize());
    }
}