package com.apg.threadpool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkerThread extends Thread {
    /**
     * Logger for logging progress messages
     */
    private static final Logger logger = Logger.getLogger(WorkerThread.class.getName());

    /**
     * Task Queue of the Thread
     */
    private Queue<TaskStructure> taskQueue;

    /**
     * flag to indicate a shutdown, should not accept any more tasks
     */
    private boolean active;

    public WorkerThread(){
        this.active    = true;
        this.taskQueue = new ConcurrentLinkedQueue<TaskStructure>();
    }

    public Queue<TaskStructure> getTaskQueue() {
        return taskQueue;
    }

    /**
     * Add a new runnable task to the queue.
     * @param task - a runnable task
     */
    public void addTask(String jobId, Runnable task){
        this.getTaskQueue().add(new TaskStructure(jobId,task));
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
        TaskStructure task;
        while(true){
            task = this.getTaskQueue().poll();
            if(task != null){
                try{
                    logger.log(Level.INFO,this.getId() + ": Started Executing new task from the task queue with jobId:" + task.getJobId());
                    task.getTask().run();
                    logger.log(Level.INFO,this.getId() + ": Finished Executing new task from the task queue with jobId:" + task.getJobId());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                if(!isActive()) {
                    logger.log(Level.INFO,this.getId() + ": Triggering shutdown of thread.");
                    break;
                }
                else{
                    try{
                        logger.log(Level.INFO, this.getId() + ": No tasks in queue, sleeping for 1s");
                        Thread.sleep(1000);
                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * An inner class to hold the task structure
     */
    private final class TaskStructure{
        private String jobId;
        private Runnable task;

        TaskStructure(String jobId,Runnable task){
            this.jobId = jobId;
            this.task  = task;
        }

        public String getJobId(){
            return this.jobId;
        }

        public Runnable getTask(){
            return this.task;
        }
    }
}
