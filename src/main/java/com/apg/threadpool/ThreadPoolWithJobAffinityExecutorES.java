package com.apg.threadpool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Fixed Size thread pool with job affinity. Underlying implementation uses SingleThreadedExecutor
 * for each jobId or collection of jobId's mapping to a same bin partition
 */
public class ThreadPoolWithJobAffinityExecutorES implements ThreadPoolWithJobAffinity {
    /**
     * Logger for logging progress messages
     */
    private static final Logger logger = Logger.getLogger(ThreadPoolWithJobAffinityExecutorES.class.getName());

    /**
     * Default size of the thread pool
     */
    public static final int DEFAULT_POOL_SIZE = 10;

    /**
     * A HashMap to keep track of job partitions and executors working on it.
     * Each jobId will have only one executor with a distinct queue which will be picked up by a thread for processing
     */
    private Map<Integer,ExecutorService> partitionExecutors;

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
    public ThreadPoolWithJobAffinityExecutorES(int poolSize){
        this.maxPoolSize        = poolSize;
        this.partitionExecutors = new HashMap<Integer, ExecutorService>(this.maxPoolSize);
    }

    /**
     * Constructor to initialize the thread pool with the default pool size
     */
    public ThreadPoolWithJobAffinityExecutorES(){
        this.maxPoolSize        = DEFAULT_POOL_SIZE;
        this.partitionExecutors = new HashMap<Integer, ExecutorService>(this.maxPoolSize);
    }

    /**
     * Get the current pool size - that is the number of worker threads
     * @return
     */
    public int poolSize() {
        return partitionExecutors.size();
    }

    public void submit(String jobId, Runnable job) {
        if(isShutdown()) {
            logger.log(Level.SEVERE,"Job submitted while shutting down",job);
            throw new IllegalStateException("Thread Pool Inactive");
        }

        int binPartition = (Integer)getPartitionId(jobId);
        logger.log(Level.INFO,"Got a new Job - Assigning to worker partition(" + binPartition + ")", job);

        if(!this.partitionExecutors.containsKey(binPartition)){
            logger.log(Level.INFO, "Adding a new executor for partition(" + binPartition + ")");

            this.partitionExecutors.put(binPartition,Executors.newSingleThreadExecutor());
        }

        logger.log(Level.INFO,"Adding job with id " + jobId + " to executor on partition(" + binPartition + ")");
        this.partitionExecutors.get(binPartition).submit(job);
    }

    public void shutdown() {

        logger.log(Level.INFO,"Shutting down the thread pool");

        //set the shutdown flag
        shutdown = true;

        //wait for all the executors to stop gracefully
        for(Integer partId: this.partitionExecutors.keySet()){
            this.partitionExecutors.get(partId).shutdown();
        }
    }

    /**
     * @return True/False depending on the shutdown flag
     */
    private boolean isShutdown(){
        return shutdown;
    }

    /**
     * Get partition id from job_id
     */
    private int getPartitionId(String jobId){
        int jobHash = jobId.hashCode();
        return ((jobHash) % this.maxPoolSize);
    }
}
