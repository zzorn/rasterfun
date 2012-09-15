package org.rasterfun;

import org.rasterfun.effect.NoiseEffect;
import org.rasterfun.generator.CompositeGenerator;
import org.rasterfun.generator.SimpleGenerator;
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
        SimpleGenerator generator1 = createSimpleGenerator(128, 128, 6, "green");
        SimpleGenerator generator2 = createSimpleGenerator(64, 64, 10, "red");
        CompositeGenerator compositeGenerator = new CompositeGenerator();
        compositeGenerator.addGenerator(generator1);
        compositeGenerator.addGenerator(generator2);

        final PicturePreviewer previewer = compositeGenerator.createPreviewer();

        new SimpleFrame("RasterFun", previewer.getUiComponent());
    }

    private static SimpleGenerator createSimpleGenerator(final int width,
                                                         final int height,
                                                         final int count,
                                                         final String channel) {
        SimpleGenerator generator = new SimpleGenerator("testpic", width, height, count);

        NoiseEffect scaleNoise = generator.addEffect(new NoiseEffect(43, 10, 2f, 2f));

        NoiseEffect noise2 = generator.addEffect(new NoiseEffect(21421, 7, 0.4f, 1f));
        noise2.getScaleVar().setToVariable(scaleNoise.getOutput());

        final NoiseEffect alphaNoise = generator.addEffect(new NoiseEffect(445, 5, 10f, 1.5f));

        final NoiseEffect blueNoise = generator.addEffect(new NoiseEffect(42, 8, 0.5f, 2f));

        generator.getEffectContainer().setChannelVar(channel, noise2.getOutput());
        generator.getEffectContainer().setChannelVar("alpha", alphaNoise.getOutput());
        generator.getEffectContainer().setChannelVar("blue", blueNoise.getOutput());

        return generator;
    }


}
