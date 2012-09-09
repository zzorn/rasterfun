package org.rasterfun;

import junit.framework.TestListener;
import org.junit.Before;
import org.junit.Test;
import org.rasterfun.core.PictureCalculation;
import org.rasterfun.core.listeners.ProgressListener;
import org.rasterfun.generator.GeneratorListener;
import org.rasterfun.generator.PictureGenerator;
import org.rasterfun.generator.SinglePictureGenerator;
import org.rasterfun.picture.Picture;
import org.rasterfun.ui.preview.PicturePreviewer;

import javax.swing.*;

import static org.junit.Assert.*;

/**
 * Test PictureGenerators
 */
public class PictureGeneratorTest {

    private SinglePictureGenerator pictureGenerator;
    private TestListener testListener;

    @Before
    public void setUp() throws Exception {

        pictureGenerator = new SinglePictureGenerator();

        testListener = new TestListener();
        pictureGenerator.addListener(testListener);
    }

    @Test
    public void testPictureGenerator() {

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

    @Test
    public void testPreviewUI() throws Exception {
        final PicturePreviewer previewer = pictureGenerator.getPreviewer();

        assertNotNull("A previewer should be returned", previewer);

        final JComponent ui1 = previewer.getUiComponent();
        assertNotNull("The previewer should create a UI", ui1);

        final JComponent ui2 = previewer.getUiComponent();
        assertTrue("The ui should be the same each time", ui1 == ui2);

        final PicturePreviewer previewer2 = pictureGenerator.getPreviewer();
        assertTrue("The previewer should be a new one each time", previewer != previewer2);
    }

    @Test
    public void testListener() throws Exception {
        assertListenerCallCount(0);

        pictureGenerator.getParameters().set("foo", 2);
        assertListenerCallCount(1);

        pictureGenerator.getParameters().set("foo", 3);
        assertListenerCallCount(2);

        pictureGenerator.getParameters().set("bar", 2);
        assertListenerCallCount(3);
    }

    @Test
    public void testChangingToSameValueShouldNotTriggerListenerEvent() throws Exception {
        pictureGenerator.getParameters().set("foo", 1);
        assertListenerCallCount(1);

        pictureGenerator.getParameters().set("foo", 1);
        assertListenerCallCount(1);
    }

    @Test
    public void testChangedListenerShouldBeCorrect() throws Exception {
        pictureGenerator.getParameters().set("foo", 1);
        assertEquals("The correct generator should be reported in the listener", pictureGenerator, testListener.getChangedGenerator());
    }

    private void assertListenerCallCount(int expected) {
        assertEquals("Listener should have been notified about changes the correct number of times", expected, testListener.getChangeCount());
    }


    private final static class TestListener implements GeneratorListener {
        private int changeCount = 0;
        private PictureGenerator generator;

        @Override
        public void onChanged(PictureGenerator generator) {
            this.generator = generator;
            changeCount++;
        }

        public void reset() {
            changeCount = 0;
        }

        public boolean wasCalled() {
            return changeCount > 0;
        }

        public int getChangeCount() {
            return changeCount;
        }

        public PictureGenerator getChangedGenerator() {
            return generator;
        }
    }
}
