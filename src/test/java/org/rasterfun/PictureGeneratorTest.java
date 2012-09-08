package org.rasterfun;

import org.junit.Test;
import org.rasterfun.core.PictureCalculation;
import org.rasterfun.core.ProgressListener;
import org.rasterfun.generator.SinglePictureGenerator;
import org.rasterfun.picture.Picture;

import static org.junit.Assert.*;

/**
 *
 */
public class PictureGeneratorTest {

    @Test
    public void testPictureGenerator() {
        SinglePictureGenerator pictureGenerator = new SinglePictureGenerator();

        final float[] p = {0f};
        final PictureCalculation calculation = pictureGenerator.generatePicture(new ProgressListener() {
            @Override
            public void onProgress(float progress) {
                //System.out.println("progress = " + progress);
                if (progress > p[0]) p[0] = progress;
            }

            @Override
            public void onStatusChanged(String description) {
                //System.out.println("description = " + description);
            }

            @Override
            public void onError(String description, Throwable cause) {
                fail("We should not get any errors, but got the error: " + description);
            }
        });
        assertNotNull("A calculation should have been created", calculation);

        // Wait for it to finish
        final Picture picture = calculation.getPictureAndWait();
        assertNotNull("A picture should have been created", picture);
        assertEquals("Number of channels should be correct", 4, picture.getChannelCount());

        assertEquals("Progress should be complete", 1.0f, p[0], 0.0001);
    }
}
