package com.apg.threadpool;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

import static org.junit.Assert.*;

public class WorkerThreadTest {

    private volatile int sampleInteger = 0;

    @Test
    public void createAndCheckThread(){
        WorkerThread wThread = new WorkerThread();
        assertNotNull(wThread);
    }

    @Test
    public void checkThreadIsActiveOnCreation(){
        WorkerThread wThread = new WorkerThread();
        assertEquals(true, wThread.isActive());
    }

    @Test
    public void checkTestTaskExecution(){
        WorkerThread wThread = new WorkerThread();

        Queue<Runnable> taskList = new LinkedList<Runnable>();
        taskList.add(new Runnable() {
            public void run() {
                sampleInteger++;
            }
        });

        wThread.setTaskQueue(taskList);
        wThread.start();
        wThread.shutdown();

        try {
            wThread.join();
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }

        assertEquals(1,sampleInteger);
    }

    @Test
    public void checkTestMultipleTaskExecution(){
        WorkerThread wThread = new WorkerThread();

        Queue<Runnable> taskList = new LinkedList<Runnable>();
        taskList.add(new Runnable() {
            public void run() {
                sampleInteger++;
            }
        });
        taskList.add(new Runnable() {
            public void run() {
                sampleInteger++;
            }
        });

        wThread.setTaskQueue(taskList);
        wThread.start();
        wThread.shutdown();

        try {
            wThread.join();
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }

        assertEquals(2,sampleInteger);
    }

    @Test
    public void checkTestMultipleTaskExecutionPostStart(){
        WorkerThread wThread = new WorkerThread();

        Queue<Runnable> taskList = new LinkedList<Runnable>();
        taskList.add(new Runnable() {
            public void run() {
                sampleInteger++;
            }
        });

        wThread.setTaskQueue(taskList);
        wThread.start();

        taskList.add(new Runnable() {
            public void run() {
                sampleInteger++;
            }
        });

        wThread.shutdown();
        try {
            wThread.join();
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }
        assertEquals(2,sampleInteger);
    }
}
