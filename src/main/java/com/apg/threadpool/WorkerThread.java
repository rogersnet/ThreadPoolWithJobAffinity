package com.apg.threadpool;

import java.util.Queue;

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
        this.active = true;
    }

    public Queue<Runnable> getTaskQueue() {
        return taskQueue;
    }

    public void setTaskQueue(Queue<Runnable> taskQueue) {
        this.taskQueue = taskQueue;
    }

    public boolean isActive(){
        return this.active;
    }

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
            }
        }
    }
}
