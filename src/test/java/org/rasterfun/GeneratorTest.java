package org.rasterfun;

import org.junit.Before;
import org.junit.Test;
import org.rasterfun.core.PictureCalculations;
import org.rasterfun.core.listeners.PictureCalculationsListener;
import org.rasterfun.effect.NoiseEffect;
import org.rasterfun.effect.container.CompositeEffect;
import org.rasterfun.effect.variable.InputVariable;
import org.rasterfun.generator.Generator;
import org.rasterfun.generator.GeneratorListener;
import org.rasterfun.generator.SimpleGenerator;
import org.rasterfun.picture.Picture;
import org.rasterfun.ui.preview.PicturePreviewer;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test PictureGenerators
 */
public class GeneratorTest {

    private SimpleGenerator generator;
    private TestListener testListener;

    @Before
    public void setUp() throws Exception {

        generator = new SimpleGenerator();

        testListener = new TestListener();
        generator.addListener(testListener);
    }

    @Test
    public void testPictureGenerator() {

        final float[] p = {0f};
        final boolean[] readyCalled = {false};
        final boolean[] allReadyCalled = {false};
        final boolean[] previewReadyCalled = {false};
        final PictureCalculations calculation = generator.generatePictures(
                new PictureCalculationsListener() {
                    @Override
                    public void onProgress(int calculationIndex, float progress) {
                        if (progress > p[0]) p[0] = progress;
                    }

                    @Override
                    public void onError(int calculationIndex, String shortDescription, String longDescription, Throwable cause) {
                        fail("We should not get any errors, but got the error: " + longDescription);
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
        final PicturePreviewer previewer = generator.createPreviewer();

        assertNotNull("A previewer should be returned", previewer);

        final JComponent ui1 = previewer.getUiComponent();
        assertNotNull("The previewer should create a UI", ui1);

        final JComponent ui2 = previewer.getUiComponent();
        assertTrue("The ui should be the same each time", ui1 == ui2);

        final PicturePreviewer previewer2 = generator.createPreviewer();
        assertTrue("The previewer should be a new one each time", previewer != previewer2);
    }

    @Test
    public void testListener() throws Exception {
        int calls = 0;
        assertListenerCallCount(calls++);

        generator.setHeight(200);
        assertListenerCallCount(calls++);

        generator.setHeight(300);
        assertListenerCallCount(calls++);

        generator.setWidth(200);
        assertListenerCallCount(calls++);

        generator.setCount(42);
        assertListenerCallCount(calls++);

        generator.setSize(10, 20);
        assertListenerCallCount(calls++);

        generator.setChannels(channelsList("burgundy", "mustard", "turquoise"));
        assertListenerCallCount(calls++);

        generator.setName("ohaioTestPic");
        assertListenerCallCount(calls++);
    }

    @Test
    public void testChangingToSameValueShouldNotTriggerListenerEvent() throws Exception {
        generator.setCount(3);
        assertListenerCallCount(1);

        generator.setCount(3);
        assertListenerCallCount(1);

        generator.setSize(13, 14);
        assertListenerCallCount(2);

        generator.setSize(13, 14);
        assertListenerCallCount(2);

        generator.setChannels(channelsList("height", "luminosity", "specularity"));
        assertListenerCallCount(3);

        generator.setChannels(channelsList("height", "luminosity", "specularity"));
        assertListenerCallCount(3);
    }

    @Test
    public void testChangedListenerShouldBeCorrect() throws Exception {
        generator.setHeight(199);
        assertEquals("The correct generator should be reported in the listener",
                     generator, testListener.getChangedGenerator());
    }

    @Test
    public void testGenerateManyPictures() throws Exception {
        generator.setCount(3);
        generator.setSize(100, 100);
        generator.setChannels(channelsList("red", "blue"));

        final PictureCalculations calculations = generator.generatePictures();
        final List<Picture> pictures = calculations.getPicturesAndWait();

        assertEquals("We should ge the expected number of pictures", 3, pictures.size());

        for (Picture picture : pictures) {
            assertEquals("Width should be as expected", 100, picture.getWidth());
            assertEquals("Picture should have expected channels",
                         channelsList("red", "blue"),
                         picture.getChannelNames());
        }
    }

    @Test
    public void testListeners() throws Exception {
        assertListenerCallCount(0);

        final NoiseEffect effect = generator.addEffect(new NoiseEffect(1));
        assertListenerCallCount(1);

        effect.getScaleVar().setValue(5);
        assertListenerCallCount(2);

        generator.removeEffect(effect);
        assertListenerCallCount(3);

        effect.getScaleVar().setValue(8);
        assertListenerCallCount(3);
    }

    @Test
    public void testCopyEffect() throws Exception {
        final NoiseEffect effect = new NoiseEffect(1);
        effect.getScaleVar().setValue(33);

        final NoiseEffect copy = (NoiseEffect) effect.copy();
        for (InputVariable inputVariable : copy.getInputVariables()) {
            System.out.println("inputVariable.getName() = " + inputVariable.getName());
            System.out.println("inputVariable.getValue() = " + inputVariable.getValue());
        }

        assertEquals(33, copy.getScaleVar().getValue());
    }

    @Test
    public void testCopy() throws Exception {
        final NoiseEffect effect = generator.addEffect(new NoiseEffect(1));
        effect.getScaleVar().setValue(5);
        assertEquals("value should be correct", 5, effect.getScaleVar().getValue());

        final SimpleGenerator generatorCopy = (SimpleGenerator) generator.copy();
        assertEquals("The copy should have one effect", 1, generatorCopy.getEffects().size());

        final InputVariable copyScaleVar =
                ((NoiseEffect)(generatorCopy.getEffects().get(0))).getScaleVar();
        assertEquals("The copy should have the correct value in the copied variable", 5, copyScaleVar.getValue());

        effect.getScaleVar().setValue(9);
        assertEquals("The copy should not be modified if the original is modified", 5, copyScaleVar.getValue());
    }

    @Test
    public void testCompositeEffect() throws Exception {

        // Test listening on composites
        int calls = 0;
        CompositeEffect compositeEffect = new CompositeEffect();
        assertListenerCallCount(calls++);

        generator.addEffect(compositeEffect);
        assertListenerCallCount(calls++);

        CompositeEffect innerComposite = new CompositeEffect();
        compositeEffect.addEffect(innerComposite);
        assertListenerCallCount(calls++);

        NoiseEffect noiseEffect = new NoiseEffect();
        innerComposite.addEffect(noiseEffect);
        assertListenerCallCount(calls++);

        NoiseEffect noiseEffect2 = new NoiseEffect();
        innerComposite.addEffect(noiseEffect2);
        assertListenerCallCount(calls++);

        // Bind noise 2 scale to noise 1 output
        noiseEffect2.getScaleVar().setToVariable(noiseEffect.getOutput());
        assertListenerCallCount(calls++);

        noiseEffect.getAmplitudeVar().setValue(99);
        assertListenerCallCount(calls++);

        // Test generate
        final PictureCalculations calculations = generator.generatePictures();
        final List<Picture> pictures = calculations.getPicturesAndWait();
        assertNotNull("We should have some pictures", pictures);
        assertTrue("We should have some pictures", !pictures.isEmpty());

        // Test copy
        CompositeEffect compositeEffectCopy = (CompositeEffect) compositeEffect.copy();
        CompositeEffect innerCompositeCopy = (CompositeEffect) compositeEffectCopy.getEffects().get(0);
        NoiseEffect noiseEffectCopy = (NoiseEffect) innerCompositeCopy.getEffects().get(0);
        NoiseEffect noiseEffect2Copy = (NoiseEffect) innerCompositeCopy.getEffects().get(1);
        assertEquals("Copy should have copied values", 99, noiseEffectCopy.getAmplitudeVar().getValue());
        assertEquals("Copy should have copied variable references", noiseEffectCopy.getOutput(), noiseEffect2Copy.getScaleVar().getSourceVariable());

        noiseEffect.getAmplitudeVar().setValue(88);

        assertEquals("Copy should not be modified by changes to the original", 99, noiseEffectCopy.getAmplitudeVar().getValue());
    }

    private void assertListenerCallCount(int expected) {
        assertEquals("Listener should have been notified about changes the correct number of times", expected, testListener.getChangeCount());
    }


    private final static class TestListener implements GeneratorListener {
        private int changeCount = 0;
        private Generator generator;

        @Override
        public void onGeneratorChanged(Generator generator) {
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

        public Generator getChangedGenerator() {
            return generator;
        }
    }

    private List<String> channelsList(String ... names) {
        return Arrays.asList(names);
    }

}
