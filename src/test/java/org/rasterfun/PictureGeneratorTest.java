package org.rasterfun;

import org.junit.Test;
import org.rasterfun.core.PictureCalculation;
import org.rasterfun.core.listeners.ProgressListener;
import org.rasterfun.generator.SinglePictureGenerator;
import org.rasterfun.picture.Picture;

import static org.junit.Assert.*;

/**
 * Test PictureGenerators
 */
public class PictureGeneratorTest {

    @Test
    public void testPictureGenerator() {
        SinglePictureGenerator pictureGenerator = new SinglePictureGenerator();

        final float[] p = {0f};
        final float[] previweProgress = {0f};
        final boolean[] readyCalled = {false};
        final boolean[] previewReadyCalled = {false};
        final PictureCalculation calculation = pictureGenerator.generatePicture(
                new ProgressListener() {
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

                    @Override
                    public void onReady() {
                        readyCalled[0] = true;
                    }
                },
                new ProgressListener() {
                    @Override
                    public void onProgress(float progress) {
                        if (progress > previweProgress[0]) previweProgress[0] = progress;
                    }

                    @Override
                    public void onStatusChanged(String description) {
                    }

                    @Override
                    public void onError(String description, Throwable cause) {
                        fail("We should not get any errors, but got the error: " + description);
                    }

                    @Override
                    public void onReady() {
                        previewReadyCalled[0] = true;
                    }
                });
        assertNotNull("A calculation should have been created", calculation);

        // Wait for it to finish
        final Picture picture = calculation.getPictureAndWait();

        // Test picture
        assertNotNull("A picture should have been created", picture);
        assertEquals("Number of channels should be correct", 4, picture.getChannelCount());
        assertEquals("Progress should be complete", 1.0f, p[0], 0.0001);
        assertTrue("onReady should have been called", readyCalled[0]);

        // Test preview picture
        assertNotNull("A preview picture should have been created", calculation.getPreview());
        assertEquals("Number of channels should be correct", 4, calculation.getPreview().getChannelCount());
        assertTrue("Preview size should be smaller than main picture size", calculation.getPreview().getWidth() < picture.getWidth());
        assertTrue("Preview size should not be zero", calculation.getPreview().getWidth() > 0);
        assertEquals("Progress should be complete on the preview picture", 1.0f, previweProgress[0], 0.0001);
        assertTrue("onReady should have been called for the preview picture", previewReadyCalled[0]);
    }
}
