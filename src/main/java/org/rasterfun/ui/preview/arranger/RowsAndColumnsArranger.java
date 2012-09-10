package org.rasterfun.ui.preview.arranger;

import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.picture.Picture;
import org.rasterfun.picture.PictureDrawer;
import org.rasterfun.utils.FastImage;

import java.awt.*;

import static java.lang.Math.*;

/**
 * Arranges pictures in rows and columns to fit the available space well.
 */
public class RowsAndColumnsArranger extends ArrangerBase {

    private static final int MIN_PADDING_X = 16;
    private static final int MIN_PADDING_Y = 16;
    private static final int DEFAULT_TEXT_AREA_H = 24;
    private static final double TEXT_PADDING_SIZE_COMPARED_TO_NORMAL_PADDING = 0.5;
    private static final int MIN_TEXT_PADDING_Y = (int) (MIN_PADDING_Y * TEXT_PADDING_SIZE_COMPARED_TO_NORMAL_PADDING);

    private Color backgroundColor = new Color(0.5f, 0.5f, 0.5f);

    private int rows = 1;
    private int columns = 1;
    private int maxPicW = 0;
    private int maxPicH = 0;

    private int paddingX = MIN_PADDING_X;
    private int paddingY = MIN_PADDING_Y;
    private int textAreaHeight = DEFAULT_TEXT_AREA_H;
    private int textPaddingY = MIN_TEXT_PADDING_Y;

    private Font labelFont;

    public RowsAndColumnsArranger() {
        labelFont = Font.getFont("Dialog 12");
    }

    @Override
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    protected void renderImage(FastImage target, double centerX, double centerY, double scale) {
        // Fill background
        target.clearToColor(backgroundColor);

        // TODO: Render the labels to separate buffered images, and blend in them with the graphics?
        /* Can't create a Graphics for the image in a FastImage...
        // Get graphics to draw labels to
        final Graphics2D g2 = (Graphics2D) target.getImage().getGraphics();
        g2.setFont(labelFont);
        final int fontHeight = g2.getFontMetrics().getHeight();
        */

        // Calculate some layout values
        int scaledPaddingX = (int) (paddingX * scale);
        int scaledPaddingY = (int) (paddingY * scale);
        int scaledTextPaddingY = (int) (textPaddingY * scale);
        int scaledTextAreaHeight = (int) (textAreaHeight * scale);
        int scaledMaxPicW  = (int) (maxPicW * scale);
        int scaledMaxPicH  = (int) (maxPicH * scale);

        int scaledColumnW = 2 * scaledPaddingX + scaledMaxPicW;
        int scaledRowH    = 2 * scaledPaddingX + scaledMaxPicH + scaledTextPaddingY + scaledTextAreaHeight;

        int scaledTotalH = rows * scaledRowH;
        int scaledTotalW = columns * scaledColumnW;
        int scaledX1 = (int)( -0.5 * scaledTotalW - centerX * scale + 0.5 * getViewWidth());
        int scaledY1 = (int)( -0.5 * scaledTotalH - centerY * scale + 0.5 * getViewHeight());

        // Only draw the labels if they fit
        boolean drawLabels = false; /* fontHeight <= scaledTextAreaHeight; */

        // Get the drawer to draw pictures with
        final PictureDrawer drawer = getDrawer();

        // Loop through rows and columns
        final int pictureCount = getPictureCount();
        int pictureIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (pictureIndex < pictureCount) {

                    final Picture picture = getPictureOrPreviewOrNull(pictureIndex);
                    if (picture != null) {
                        final CalculatorBuilder builder = getBuilder(pictureIndex);

                        // Calculate picture position and size on the target image
                        int targetPicW = (int) (builder.getWidth()  * scale);
                        int targetPicH = (int) (builder.getHeight() * scale);
                        int targetPicX = scaledX1 + scaledColumnW * column + scaledPaddingX + (scaledMaxPicW - targetPicW) / 2;
                        int targetPicY = scaledY1 + scaledRowH    * row    + scaledPaddingY + (scaledMaxPicH - targetPicH) / 2;
                        int targetPicX2 = targetPicX + targetPicW;
                        int targetPicY2 = targetPicY + targetPicH;

                        // Draw the image
                        drawer.draw(target, targetPicX, targetPicY, targetPicX2, targetPicY2,
                                    picture, 0, 0, picture.getWidth(), picture.getHeight());

                        /*
                        // Draw the label
                        final String name = builder.getName();
                        if (drawLabels && name != null) {
                            int textCenterX = scaledX1 + scaledColumnW * column + scaledColumnW / 2;
                            int textW = (int) g2.getFontMetrics().getStringBounds(name, g2).getWidth();
                            int textX = textCenterX - textW / 2;
                            int textY = scaledY1 + scaledRowH * row + scaledRowH - scaledPaddingY;

                            g2.drawString(name, textX, textY);
                        }
                        */
                    }
                }

                pictureIndex++;
            }
        }

    }

    protected void calculateLayout() {

        // Calculate max picture size
        maxPicW = 0;
        maxPicH = 0;
        for (CalculatorBuilder builder : getBuilders()) {
            final int w = builder.getWidth();
            final int h = builder.getHeight();
            if (w > maxPicW) maxPicW = w;
            if (h > maxPicH) maxPicH = h;
        }

        // Calculate other variables
        int columnW = maxPicW + 2 * MIN_PADDING_X;
        int rowH    = maxPicH + 2 * MIN_PADDING_Y + DEFAULT_TEXT_AREA_H + MIN_TEXT_PADDING_Y;

        // Calculate the aspect ratio of the screen
        int viewW = getViewWidth();
        int viewH = getViewHeight();
        double aspect = (viewH <= 0 || viewW <= 0) ? 1.0 : (double)viewW / viewH;

        // Calculate the number or rows and columns that most closely matches the aspect ratio
        double closestAspect = Double.POSITIVE_INFINITY;
        int closestRows = 1;
        int closestColumns = 1;
        final int pictureCount = getPictureCount();
        for (int rs = 1; rs <= pictureCount; rs++) {
            int cs = (int) (ceil((double) pictureCount / rs) + 0.5);
            int totalW = cs * columnW;
            int totalH = rs * rowH;
            double a = (double)totalW / totalH;

            if (abs(aspect - a) < abs(aspect - closestAspect) ) {
                closestAspect = a;
                closestRows = rs;
                closestColumns = cs;
            }
        }
        rows    = closestRows;
        columns = closestColumns;

        // Sanity checks
        assert rows    >= 1 : "There should be one or more rows, but we had " + rows;
        assert columns >= 1 : "There should be one or more columns, but we had " + columns;
        assert rows * columns >= pictureCount :
                pictureCount + " pictures should fit in "+rows+" rows and "+columns+" columns, " +
                "but there is only place for " + (rows*columns) + " pictures!";

        // Calculate amount of extra padding that can be added
        int totalW = columns * columnW;
        int totalH = rows * rowH;
        int extraW = max(0, viewW - totalW);
        int extraH = max(0, viewH - totalH);
        // NOTE: Maybe implement this later, for now do not spread out the pictures.

        // Calculate default zoom level
        double targetScale = min(
                (double)viewW / totalW,
                (double)viewH / totalH);
        setScale(targetScale);

        // Specify where we can pan
        final double panMargin = 0.5;
        setPanBounds(-panMargin * totalW,
                     -panMargin * totalH,
                      panMargin * totalW,
                      panMargin * totalH);
    }
}
