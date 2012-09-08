package org.rasterfun.core;

/**
 * A listener that is notified about a calculation progress.
 */
public interface CalculationListener {

    /**
     * @param calculationIndex index number of the calculation task.
     * @param completedLines the number of scanlines that the calculation task has completed.
     */
    void onCalculationProgress(int calculationIndex, int completedLines);

}
