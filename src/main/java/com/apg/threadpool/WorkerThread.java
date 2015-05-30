package com.apg.threadpool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WorkerThread extends Thread {
    /**
     * Task Queue of the Thread
     */
    private Queue<Runnable> taskQueue;

    /**
     * flag to indicate a shutdown, should not accept any more tasks
     */
    private boolean active;

    public WorkerThread(){
        this.active    = true;
        this.taskQueue = new ConcurrentLinkedQueue<Runnable>();
    }

    public Queue<Runnable> getTaskQueue() {
        return taskQueue;
    }

    /**
     * Add a new runnable task to the queue.
     * @param task - a runnable task
     */
    public void addTask(Runnable task){
        this.getTaskQueue().add(task);
    }

    /**
     * Returns true or false if the thread is active or not
     * @return
     */
    public boolean isActive(){
        return this.active;
    }

    /**
     * Sets the thread to inactive.
     */
    public void shutdown(){
        this.active = false;
    }

    /**
     * Pick a task from the task queue and execute it
     */
    public void run(){
        Runnable task;
        while(true){
            task = this.getTaskQueue().poll();
            if(task != null){
                try{
                    task.run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                if(!isActive())
                    break;
                else{
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
