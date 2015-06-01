# ThreadPoolWithJobAffinity
  
An implementation of thread pool with focus on job affinity. Each task submitted to the pool should be identified with a distinct job id. Based on the id, either a new worker thread is spawned or an existing thread is used and the job is queued. The thread pool guarantees that jobs having the same id are executed by the same worker thread. 
The distribution of jobs to the workers is done through a very basic scheme. 
      Bin Partition = (Hash Code of Job Id ) % (Maximum Thread Pool Size)
  
## Two implementations are provided :-
  
* First implementation uses a hash map to map partition ids to worker thread, thereby ensuring that each partition is assigned a unique worker thread. Each worker thread maintains unbounded task queue. 
* Second implementation uses a hash map to map partition ids to SingleThreadedExecutors. Each executor maintains its own unbounded queue. 
    
## Assumptions:-
* The thread pool creation and job submission are executed inside a single parent thread.
* The tasks waiting in a queue for a thread do not have any timeout. 
* There is a good distribution of jobs across the worker threads. 
    
## Usage Examples:-
  Please check out the unit tests.
  
  
