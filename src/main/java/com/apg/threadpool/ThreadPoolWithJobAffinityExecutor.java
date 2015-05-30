package com.apg.threadpool;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadPoolWithJobAffinityExecutor implements ThreadPoolWithJobAffinity {
    /**
     * Logger for logging progress messages
     */
    private static final Logger logger = Logger.getLogger(ThreadPoolWithJobAffinityExecutorES.class.getName());

    /**
     * Default size of the thread pool
     */
    public static final int DEFAULT_POOL_SIZE = 10;

    /**
     * A Concurrent HashMap to keep track of job partitions and workers working on it.
     * Each jobId will have only one worker with a distinct queue which will be picked up by a thread for processing
     */
    private Map<Integer,WorkerThread> partitionWorkers;

    /**
     * Size of the Thread Pool. This is equivalent to the number of current worker threads created.
     */
    private int maxPoolSize;

    /**
     * Flag to indicate a shutdown is triggered and no further jobs will be submitted
     */
    private boolean shutdown;

    /**
     * Constructor to initialize the thread pool with some specific number
     * @param poolSize
     */
    public ThreadPoolWithJobAffinityExecutor(int poolSize){
        this.maxPoolSize      = poolSize;
        this.partitionWorkers = new HashMap<Integer, WorkerThread>(this.maxPoolSize);
    }

    /**
     * Constructor to initialize the thread pool with the default pool size
     */
    public ThreadPoolWithJobAffinityExecutor(){
        this.maxPoolSize      = DEFAULT_POOL_SIZE;
        this.partitionWorkers = new HashMap<Integer, WorkerThread>(this.maxPoolSize);
    }

    /**
     * Get the current pool size - that is the number of worker threads
     * @return
     */
    public int poolSize() {
        return partitionWorkers.size();
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
            logger.log(Level.SEVERE,"Job submitted while shutting down",job);
            throw new IllegalStateException("Thread Pool inactive");
        }

        int threadPartition = this.getPartitionId(jobId);
        logger.log(Level.INFO,"Got a new Job - Assigning to worker partition(" + threadPartition + ")", job);

        //check if the partition id already present, if yes, add it to the queue
        //else create a new worker thread and assign it to the partition
        if(this.partitionWorkers.containsKey((Integer)threadPartition)){
            logger.log(Level.INFO,"Adding job with id " + jobId + " to worker on partition(" + threadPartition + ")");
            this.getPartitionWorker((Integer)threadPartition).addTask(job);
        }else{
            logger.log(Level.INFO, "Adding a new worker for partition(" + threadPartition + ")");
            this.addWorker((Integer)threadPartition,job);
        }
    }

    /**
     * Send a shutdown signal to all the worker threads, wait for the threads to complete processing
     */
    public void shutdown() {
        logger.log(Level.INFO,"Shutting down thread pool");

        //set shutdown flag
        shutdown = true;

        //send shutdown signal to all worker threads
        for(Integer partId: this.partitionWorkers.keySet()){
            this.getPartitionWorker(partId).shutdown();
        }

        //wait for the worker threads to finish
        try {
            for(Integer partId: this.partitionWorkers.keySet()){
                this.getPartitionWorker(partId).join();
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Adds a new worker to a fresh partiion list
     */
    private void addWorker(Integer partitionId,Runnable job){
        //create a new worker thread
        WorkerThread wThread = new WorkerThread();

        //assign the partition to the newly worker thread
        this.partitionWorkers.put(partitionId,wThread);

        //assign the job to the worker and start
        wThread.addTask(job);
        wThread.start();
    }

    /**
     * Get partition id from job_id
     */
    private int getPartitionId(String jobId){
        int jobHash = jobId.hashCode();
        return ((jobHash) % this.maxPoolSize);
    }

    /**
     * Get get partition worker
     */
    private WorkerThread getPartitionWorker(Integer partitionId){
        return this.partitionWorkers.get(partitionId);
    }
}
