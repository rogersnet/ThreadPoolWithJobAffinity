# Sample Thread pool with usage

Inorder to execute the sample file, please follow the following steps.

* Run `mvn install` from the project root to create the jar file.

* Run `javac -cp "../target/ThreadPoolWithAffinity-1.0.jar:." ThreadPoolSampleUsage.java` inorder to compile the file

* Run `java -cp "../target/ThreadPoolWithAffinity-1.0.jar:." ThreadPoolSampleUsage` inorder to execute the main method

## NOTE:
 A sample output is provided in output.txt file.
 
### Explanation of the output
Each thread is assigned a unique partition/bin number. This partition number reflects when a new job is submitted to the pool as is illustrated in the sample below - 
   `INFO: Adding a new worker for partition(9)`
   `INFO: Adding job with id 1 to worker on partition(9)`

When a job is submitted to the thread queue, it awaits there to be picked up for execution. When the job is picked and executed, the output looks like the following - 
   `INFO: 9: Started Executing new task from the task queue with jobId:1`
  This is followed by the task execution which prints the following - 
   `Job 1 --- Task 1`

All jobs with the same job id are executed by the same thread and executed in order of submission (FIFO). This is illustrated below
 `INFO: Adding job with id 1 to worker on partition(9)`
 `INFO: 9: Started Executing new task from the task queue with jobId:1`
 `Job 1 --- Task 1`
 `INFO: Adding job with id 1 to worker on partition(9)`
 `INFO: Adding job with id 1 to worker on partition(9)`
 `INFO: 9: Started Executing new task from the task queue with jobId:1`
 `Job 1 --- Task 2`
 `INFO: 9: Started Executing new task from the task queue with jobId:1`
 `Job 1 --- Task 3`
 
 


   
   
 

 
 
