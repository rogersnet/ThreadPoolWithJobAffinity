package com.apg.threadpool;

import java.util.*;
import java.util.concurrent.*;

public class ThreadPoolWithJobAffinityExecutor implements ThreadPoolWithJobAffinity {
    /**
     * Default size of the thread pool
     */
    public static final int DEFAULT_POOL_SIZE = 10;

    /**
     * A Concurrent HashMap to keep track of job queue. Each jobId will have a
     * distinct queue which will be picked up by a thread for processing
     */
    private ConcurrentHashMap<Integer,Queue<Runnable>> jobQueue;

    /**
     * A Synchronized HashMap to keep track of partition id and worker thread. Each job submitted
     * with a specific job id will be executed by one of the worker threads.
     */
    private List<WorkerThread> workers;

    /**
     * Size of the Thread Pool. This is equivalent to the number of current worker threads created.
     */
    private int maxPoolSize;

    /**
     * Flag to indicate a shutdown is triggered and no further jobs will be submitted
     */
    private volatile boolean shutdown;

    /**
     * Constructor to initialize the thread pool with some specific number
     * @param poolSize
     */
    public ThreadPoolWithJobAffinityExecutor(int poolSize){
        this.maxPoolSize  = poolSize;
        this.jobQueue     = new ConcurrentHashMap<Integer, Queue<Runnable>>(this.maxPoolSize);
        this.workers      = Collections.synchronizedList(new ArrayList<WorkerThread>(this.maxPoolSize));

    }

    /**
     * Constructor to initialize the thread pool with the default pool size
     */
    public ThreadPoolWithJobAffinityExecutor(){
        this.maxPoolSize  = DEFAULT_POOL_SIZE;
        this.jobQueue     = new ConcurrentHashMap<Integer, Queue<Runnable>>(this.maxPoolSize);
        this.workers      = Collections.synchronizedList(new ArrayList<WorkerThread>(this.maxPoolSize));
    }

    /**
     * Get the current pool size - that is the number of worker threads
     * @return
     */
    public int poolSize() {
        return jobQueue.size();
    }

    /**
     * return the shutdown flag of the queue
     */
    public boolean isShutdown(){
        return shutdown;
    }

    /**
     * When a new task with jobId is submitted, we first obtain the partition id by taking the
     * modulus of the hashcode with the maximum pool size. This will ensure each job is executed
     * by a specific thread and ensures proper distribution of tasks/jobs in the pool
     * @param jobId a string containing job id.
     * @param job a Runnable representing the job to be executed.
     */
    public void submit(String jobId, Runnable job) {

        if(isShutdown()){
            throw new IllegalStateException("Thread Pool inactive");
        }

        //get hash code for the job id
        int jobHash = jobId.hashCode();

        //get partition id based on the hash code
        int threadPartition = ((jobHash) % this.maxPoolSize);

        //check if the partition id already present, if yes, add it to the queue
        //else create a new worker thread and assign it to the partition
        if(this.jobQueue.containsKey((Integer)threadPartition)){
            this.jobQueue.get((Integer)threadPartition).add(job);
        }else{
            //create a new job partition
            Queue<Runnable> taskQueue = new LinkedList<Runnable>();
            this.jobQueue.put((Integer)threadPartition,taskQueue);
            taskQueue.add(job);

            //create a new worker thread and assign the task queue to this thread
            WorkerThread wThread = new WorkerThread();
            wThread.setTaskQueue(this.jobQueue.get((Integer) threadPartition));
            this.workers.add(wThread);
            wThread.start();
        }
    }

    /**
     * Send a shutdown signal to all the worker threads, wait for the threads to complete processing
     */
    public void shutdown() {
        //set shutdown flag
        shutdown = true;

        //send shutdown signal to all worker threads
        for(WorkerThread wThreads: this.workers){
            wThreads.shutdown();
        }

        //wait for the worker threads to finish
        try {
            for(WorkerThread wThreads: this.workers){
                wThreads.join();
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }
}
