package org.rasterfun.core.listeners;

/**
 * A listener that is notified about calculation progress.
 */
public interface CalculationListener {

    /**
     * @param calculationIndex index number of the calculation task.
     * @param completedLines the number of scanlines that the calculation completed since the last update call.
     */
    void onCalculationProgress(int calculationIndex, int completedLines);

}
