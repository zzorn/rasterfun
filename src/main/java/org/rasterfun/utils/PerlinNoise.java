package org.rasterfun.utils;

import java.util.Random;

import static java.lang.Math.*;
import static org.rasterfun.utils.MathTools.Tau;
import static org.rasterfun.utils.MathTools.fastFloor;

/**
 * Tiling Perlin noise.
 * For reference, see e.g. http://gamedev.stackexchange.com/a/23705
 */
public final class PerlinNoise {

    public static final int MAX_OCTAVES = 16;

    private static final int NUM = 256;
    private static final int PERMUTATIONS_SEED = 42;
    private static final int[] permutations = createPermutations(PERMUTATIONS_SEED);
    private static final double[][] directions = createDirections();

    private static int[] createPermutations(long seed) {
        Random random = new Random(seed);
        int[] perm = new int[NUM * 2];

        // Initialize first half of int array
        for (int i = 0; i < NUM; i++) {
            perm[i] = i;
        }

        // Shuffle start of array
        shuffleIntArray(perm, 0, NUM, random);

        // Copy start of array to end of array
        System.arraycopy(perm, 0, perm, NUM, NUM);

        return perm;
    }

    private static void shuffleIntArray(int[] arr, int start, int end, Random random) {
        for (int i = end; i > start + 1; i--) {
            int j = random.nextInt(i);
            int k = i - 1;

            // Swap elements at j and k;
            int t = arr[k];
            arr[k] = arr[j];
            arr[j] = t;
        }
    }

    private static double[][] createDirections() {
        double[][] dirs = new double[NUM][2];

        // Create lookup tables for sin and cos, for the range 0..(NUM-1).
        for (int i = 0; i < NUM; i++) {
            dirs[i][0] = cos(Tau * i / NUM);
            dirs[i][1] = sin(Tau * i / NUM);
        }

        return dirs;
    }

    /**
     * @return a noise value at the specific position, with the specified seed.
     */
    public static double noise(double x, double y, int seed) {
        // Get closest grid center to the upper left
        int intX = fastFloor(x);
        int intY = fastFloor(y);

        // Add together the values of the surflets of the four closest integer grid centers at this point
        return surflet(x, y, intX    , intY    , seed) +
               surflet(x, y, intX + 1, intY    , seed) +
               surflet(x, y, intX    , intY + 1, seed) +
               surflet(x, y, intX + 1, intY + 1, seed);

    }

    /**
     * @return a tiling noise value at the specific position, with the specified tiling.
     */
    public static double tilingNoise(double x, double y,
                                     double edgeX1, double edgeY1,
                                     double edgeX2, double edgeY2,
                                     int fillSeed,
                                     int edgeSeed) {
        // Get closest grid center to the upper left
        int intX = fastFloor(x);
        int intY = fastFloor(y);

        // Get edge grid positions
        double offs = 0;
        int edgeGridX1 = fastFloor(edgeX1 + offs);
        int edgeGridY1 = fastFloor(edgeY1 + offs);
        int edgeGridX2 = fastFloor(edgeX2 + offs);
        int edgeGridY2 = fastFloor(edgeY2 + offs);

        // Add together the values of the surflets of the four closest integer grid centers at this point
        return tilingSurflet(x, y, intX,     intY,     edgeGridX1, edgeGridY1, edgeGridX2, edgeGridY2, fillSeed, edgeSeed) +
               tilingSurflet(x, y, intX + 1, intY,     edgeGridX1, edgeGridY1, edgeGridX2, edgeGridY2, fillSeed, edgeSeed) +
               tilingSurflet(x, y, intX    , intY + 1, edgeGridX1, edgeGridY1, edgeGridX2, edgeGridY2, fillSeed, edgeSeed) +
               tilingSurflet(x, y, intX + 1, intY + 1, edgeGridX1, edgeGridY1, edgeGridX2, edgeGridY2, fillSeed, edgeSeed);

    }

    /**
     * Calculates a noise consisting of many octaves.
     */
    public static double octaveNoise(double x, double y, double octaves, int seed) {
        double sum = 0;
        double octave = min(octaves, MAX_OCTAVES);
        double amplitude = 1;
        double scale = 1;
        double strength = 1;

        int octaveSeed = seed % NUM;
        if (octaveSeed < 0) octaveSeed += NUM;

        while (octave > 0 ) {
            // Support for fractional octaves, the fractional octave is just partially added in.
            if (octave < 1) strength = octave;

            // Add octave,using unique random seed for each octave, and permutations table to randomize it.
            sum += strength * amplitude * noise(x * scale, y * scale, permutations[octaveSeed++]);

            // Scale amplitude down and noise density up.
            amplitude *= 0.5;
            scale *= 2;

            // Move to next (more detailed) octave
            octave -= 1;
        }

        return sum;
    }

    /**
     * Calculates a noise consisting of many octaves, that uses one seed for edges and another elsewhere.
     *
     * @param x
     * @param y
     * @param octaves
     * @param edgeX1
     * @param edgeY1
     * @param edgeX2
     * @param edgeY2
     * @param fillSeed
     * @param edgeSeed
     * @return
     */
    public static double tilingOctaveNoise(double x, double y,
                                           double octaves,
                                           double edgeX1, double edgeY1,
                                           double edgeX2, double edgeY2,
                                           int fillSeed,
                                           int edgeSeed) {
        double sum = 0;
        double octave = min(octaves, MAX_OCTAVES);
        double amplitude = 1;
        double scale = 1;
        double strength = 1;

        int octaveFillSeed = fillSeed % NUM;
        int octaveEdgeSeed = edgeSeed % NUM;
        if (octaveFillSeed < 0) octaveFillSeed += NUM;
        if (octaveEdgeSeed < 0) octaveEdgeSeed += NUM;

        while (octave > 0 ) {
            // Support for fractional octaves, the fractional octave is just partially added in.
            if (octave < 1) strength = octave;

            // Add octave, with a unique seed for each octave (use the permutations table to scramble the seeds)
            sum += strength * amplitude * tilingNoise(
                    x * scale,
                    y * scale,
                    edgeX1 * scale,
                    edgeY1 * scale,
                    edgeX2 * scale,
                    edgeY2 * scale,
                    permutations[octaveFillSeed++],
                    permutations[octaveEdgeSeed++]);

            // Scale amplitude down and noise density up.
            amplitude *= 0.5;
            scale *= 2;

            // Move to next (more detailed) octave
            octave -= 1;
        }

        return sum;
    }

    /**
     * Calculates the value of one surflet in the noise, which is basically a gradient in a random direction located at
     * an integer grid corner, damped by a polynomial falloff function that fades it to zero before the next grid corners.
     */
    private static double surflet(double x, double y, int gridX, int gridY, int seed) {
        // Get the distance of this point from an integer grid center.
        double distX = abs(x - gridX);
        double distY = abs(y - gridY);

        // Calculate distX and distY to the power of 3, 4 and 5.
        double distX3 = distX * distX * distX;
        double distX4 = distX3 * distX;
        double distX5 = distX4 * distX;

        double distY3 = distY * distY * distY;
        double distY4 = distY3 * distY;
        double distY5 = distY4 * distY;

        // Calculate the fade-off polynomial
        double polyX = 1.0 - 6.0 * distX5 + 15.0 * distX4 - 10.0 * distX3;
        double polyY = 1.0 - 6.0 * distY5 + 15.0 * distY4 - 10.0 * distY3;

        // Calculate the random seeds for this surflet.
        int seedX = (gridX + seed) % NUM;
        int seedY = (gridY + seed) % NUM;
        if (seedX < 0) seedX += NUM;
        if (seedY < 0) seedY += NUM;

        // Get random number between 0 and NUM-1 based on the seeds.
        int hashed = permutations[permutations[seedX] + seedY];

        // Calculate the gradient based on the random number used to look up an angle.
        double grad = (x - gridX) * directions[hashed][0] +
                (y - gridY) * directions[hashed][1];

        // Fade the gradient with the polynomial
        return polyX * polyY * grad;
    }

    /**
     * Calculates the value of one surflet in the noise, which is basically a gradient in a random direction located at
     * an integer grid corner, damped by a polynomial falloff function that fades it to zero before the next grid corners.
     */
    private static double tilingSurflet(double x, double y, int gridX, int gridY,
                                        int edgeGridX1, int edgeGridY1,
                                        int edgeGridX2, int edgeGridY2,
                                        int fillSeed, int edgeSeed) {
        // Get the distance of this point from an integer grid center.
        double distX = abs(x - gridX);
        double distY = abs(y - gridY);

        // Calculate distX and distY to the power of 3, 4 and 5.
        double distX3 = distX * distX * distX;
        double distX4 = distX3 * distX;
        double distX5 = distX4 * distX;

        double distY3 = distY * distY * distY;
        double distY4 = distY3 * distY;
        double distY5 = distY4 * distY;

        // Calculate the fade-off polynomial
        double polyX = 1.0 - 6.0 * distX5 + 15.0 * distX4 - 10.0 * distX3;
        double polyY = 1.0 - 6.0 * distY5 + 15.0 * distY4 - 10.0 * distY3;

        // Determine this surflet lies on an edge or not, and adjust the base seed based on that
        final boolean onEdge = edgeGridX1 == gridX ||
                edgeGridX2 == gridX ||
                edgeGridY1 == gridY ||
                edgeGridY2 == gridY;
        final int baseSeed = onEdge ? edgeSeed : fillSeed;

        // Calculate the random seeds for this surflet, ensuring that parallel edges get the same seeds.
        // Map right edge to left and bottom to top if we are tiling
        int seedX = ((gridX == edgeGridX2 ? edgeGridX1 : gridX) + baseSeed) % NUM;
        int seedY = ((gridY == edgeGridY2 ? edgeGridY1 : gridY) + baseSeed) % NUM;
        if (seedX < 0) seedX += NUM;
        if (seedY < 0) seedY += NUM;

        // Get random number between 0 and NUM-1 based on the seeds.
        int hashed = permutations[permutations[seedX] + seedY];

        // Calculate the gradient based on the random number used to look up an angle.
        double grad = (x - gridX) * directions[hashed][0] +
                (y - gridY) * directions[hashed][1];

        // Fade the gradient with the polynomial
        return polyX * polyY * grad;
    }



}
