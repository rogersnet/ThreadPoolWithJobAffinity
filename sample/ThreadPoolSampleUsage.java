import com.apg.threadpool.ThreadPoolWithJobAffinity;
import com.apg.threadpool.ThreadPoolWithJobAffinityExecutor;

public class ThreadPoolSampleUsage {
    public static void main(String[] args){
        //create a new thread pool
        ThreadPoolWithJobAffinity tpool = new ThreadPoolWithJobAffinityExecutor();

        tpool.submit("1", new Runnable() {
            @Override
            public void run() {
                System.out.println("Job 1 --- Task 1");
            }
        });

        tpool.submit("1", new Runnable() {
            @Override
            public void run() {
                System.out.println("Job 1 --- Task 2");
            }
        });

        tpool.submit("1", new Runnable() {
            @Override
            public void run() {
                System.out.println("Job 1 --- Task 3");
            }
        });

        tpool.submit("2", new Runnable() {
            @Override
            public void run() {
                System.out.println("Job 2 --- Task 1");
            }
        });

        tpool.submit("3", new Runnable() {
            @Override
            public void run() {
                System.out.println("Job 3 --- Task 1");
            }
        });

        tpool.submit("3", new Runnable() {
            @Override
            public void run() {
                System.out.println("Job 3 --- Task 2");
            }
        });

        tpool.submit("2", new Runnable() {
            @Override
            public void run() {
                System.out.println("Job 2 --- Task 2");
            }
        });

        tpool.shutdown();
    }
}