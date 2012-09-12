package org.rasterfun;

import org.rasterfun.effect.NoiseEffect;
import org.rasterfun.generator.SimplePictureGenerator;
import org.rasterfun.ui.preview.PicturePreviewer;
import org.rasterfun.utils.SimpleFrame;

import java.util.concurrent.*;

/**
 *
 */
public class RasterfunApplication {

    private static final int MAXIMUM_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 4;

    private static final ExecutorService executor = new ThreadPoolExecutor(
            0, // Initial thread count
            MAXIMUM_THREAD_POOL_SIZE, // Limit maximum threads, no much point in having much more threads than cores that can calculate them.
            60L, TimeUnit.SECONDS, // Timeout until idle thread cleared
            new LinkedBlockingQueue<Runnable>()); // Queue tasks when there are no free threads for them

    /**
     * @return executor that can be used to run tasks in background threads.
     */
    public static ExecutorService getExecutor() {
        return executor;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /*
        List<Future<String>> futures = new ArrayList<Future<String>>();
        for (int i = 0; i < 100000; i++) {
            futures.add(executor.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    Thread.sleep(1);
                    return "UGF";
                }
            }));
        }

        int i = 0;
        for (Future<String> future : futures) {
            System.out.println("future "+(i++)+" get() = " + future.get());
        }
        System.out.println("and thats it");
        */

        // Read command line commands
        // TODO: Allow e.g. building a specified project from the command line, without need to open the UI.

        // Load options

        // Create main UI

        // Load default library and any other configured libraries

        // Load most recent project, or create a new empty / example one if none specified.
        SimplePictureGenerator generator = new SimplePictureGenerator();
        generator.getParameters().set(SimplePictureGenerator.NUMBER, 66);
        generator.addEffect(new NoiseEffect("red",   142, 7, 0.4f, 1f));
        generator.addEffect(new NoiseEffect("green", 253, 3, 0.4f, 1f));
        generator.addEffect(new NoiseEffect("blue",  344, 0.9f, 0.3f, 1.3f));
        generator.addEffect(new NoiseEffect("alpha", 445,  5, 0.9f, 1.5f));

        final PicturePreviewer previewer = generator.getPreviewer();

        new SimpleFrame("RasterFun", previewer.getUiComponent());
    }



}
