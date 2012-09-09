package org.rasterfun;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class RasterfunApplication {

    private static final int MAXIMUM_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 8;

    private static final ExecutorService executor = new ThreadPoolExecutor(
            0, // Initial thread count
            MAXIMUM_THREAD_POOL_SIZE,
            60L, TimeUnit.SECONDS, // Timeout until idle thread cleared
            new SynchronousQueue<Runnable>());

    /**
     * @return executor that can be used to run tasks in background threads.
     */
    public static ExecutorService getExecutor() {
        return executor;
    }

    public static void main(String[] args) {

        // Read command line commands
        // TODO: Allow e.g. building a specified project from the command line, without need to open the UI.

        // Load options

        // Create main UI

        // Load default library and any other configured libraries

        // Load most recent project, or create a new empty / example one if none specified.



    }


}
