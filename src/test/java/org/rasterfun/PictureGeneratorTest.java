package org.rasterfun;

import org.junit.Before;
import org.junit.Test;
import org.rasterfun.core.PictureCalculations;
import org.rasterfun.core.listeners.PictureCalculationsListener;
import org.rasterfun.generator.GeneratorListener;
import org.rasterfun.generator.PictureGenerator;
import org.rasterfun.generator.SinglePictureGenerator;
import org.rasterfun.picture.Picture;
import org.rasterfun.ui.preview.PicturePreviewer;

import javax.swing.*;
import java.util.List;

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
        final boolean[] readyCalled = {false};
        final boolean[] allReadyCalled = {false};
        final boolean[] previewReadyCalled = {false};
        final PictureCalculations calculation = pictureGenerator.generatePictures(
                new PictureCalculationsListener() {
                    @Override
                    public void onProgress(int calculationIndex, float progress) {
                        if (progress > p[0]) p[0] = progress;
                    }

                    @Override
                    public void onError(int calculationIndex, String description, Throwable cause) {
                        fail("We should not get any errors, but got the error: " + description);
                    }

                    @Override
                    public void onPreviewReady(int calculationIndex, int pictureIndex, Picture preview) {
                        previewReadyCalled[0] = true;
                    }

                    @Override
                    public void onPictureReady(int calculationIndex, int pictureIndex, Picture picture) {
                        readyCalled[0] = true;
                    }

                    @Override
                    public void onReady(int calculationIndex, List<Picture> pictures) {
                        allReadyCalled[0] = true;
                    }
                });
        assertNotNull("A calculation should have been created", calculation);

        // Wait for it to finish
        final List<Picture> pictures = calculation.getPicturesAndWait();

        assertEquals("A picture should have been created", 1, pictures.size());
        assertEquals("Picture should also be available from the calculation", 1, calculation.getPictures().size());
        assertEquals("A preview picture should have been created", 1, calculation.getPreviews().size());

        final Picture picture = pictures.get(0);
        final Picture preview = calculation.getPreviews().get(0);

        // Test picture
        assertEquals("Number of channels should be correct", 4, picture.getChannelCount());
        assertEquals("Progress should be complete", 1.0f, p[0], 0.0001);
        assertTrue("ready should have been called for the picture", readyCalled[0]);
        assertTrue("all ready should have been called", allReadyCalled[0]);

        // Test preview picture
        assertEquals("Number of channels should be correct", 4, preview.getChannelCount());
        assertTrue("Preview size should be smaller than main picture size", preview.getWidth() < picture.getWidth());
        assertTrue("Preview size should not be zero", preview.getWidth() > 0);
        assertTrue("preview ready should have been called", previewReadyCalled[0]);
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
        public void onGeneratorChanged(PictureGenerator generator) {
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
