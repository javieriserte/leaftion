package org.jiserte.leaftion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class MotionPlotPanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = -6430517195416277953L;

  public double[] xData;
  public double[] yData;

  public double[] objmaxs;
  public double[] objmeans;
  public double[] objmins;
  public double[] iters;

  public boolean showFittingProfiles = false;

  public int startSelectIndex = 2;
  public int endSelectIndex = 5;

  // @Override
  // protected void paintComponent(Graphics g) {
  // super.paintComponent(g);
  //
  // }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;

    int width = this.getWidth();
    int height = this.getHeight();

    int plotWidth = 0;
    int plotHeight = 0;

    if (showFittingProfiles) {
      plotWidth = (int) (width * 0.95);
      plotHeight = (int) (height * 0.45);
    } else {
      plotWidth = (int) (width * 0.95);
      plotHeight = (int) (height * 0.95);
    }

    // ---------------------------------------------------------------------- //
    // Main Plot
    int hTickAreaWidth = 50;
    int vTickAreaHeight = 50;
    int dataAreaWidth = plotWidth - hTickAreaWidth;
    int dataAreaHeight = plotHeight - vTickAreaHeight;

    g2d.setColor(new Color(255, 255, 255));
    g2d.fillRect(0, 0, width, height);

    int bgBoxSpacerSize = 53;
    int bgBoxSize = 50;
    for (int i = 0; i < (int) (width / bgBoxSpacerSize) + 1; i++) {

      for (int j = 0; j < (int) (height / bgBoxSpacerSize) + 1; j++) {

        g2d.setColor(new Color(252, 252, 252));
        g2d.fillRect(i * bgBoxSpacerSize, j * bgBoxSpacerSize, bgBoxSize,
            bgBoxSize);

        g2d.setColor(new Color(240, 240, 240));
        g2d.drawRect(i * bgBoxSpacerSize, j * bgBoxSpacerSize, bgBoxSize,
            bgBoxSize);

      }

    }

    if (xData == null || xData.length <= 1) {
      return;
    }

    double maxY = this.maxArray(yData);
    double maxX = this.maxArray(xData);
    double minY = this.minArray(yData);
    double minX = this.minArray(xData);
    if (this.showFittingProfiles) {
      g.translate((width - plotWidth) / 2, (int) (height * 0.05));
    } else {
      g.translate((width - plotWidth) / 2, (height - plotHeight) / 2);
    }

    // ---------------------------------------------------------------------- //
    // Draw Selected Area
    if (this.endSelectIndex > this.startSelectIndex) {

      int x1_tr = (int) ((xData[this.startSelectIndex] - minX) / (maxX - minX)
          * dataAreaWidth + hTickAreaWidth);
      int x2_tr = (int) ((xData[this.endSelectIndex] - minX) / (maxX - minX)
          * dataAreaWidth + hTickAreaWidth);

      g.setColor(new Color(200, 245, 200, 60));
      g.fillRect(x1_tr, 0, x2_tr - x1_tr, dataAreaHeight);
      g.setColor(new Color(30, 255, 30, 90));
      g.drawRect(x1_tr, 0, x2_tr - x1_tr, dataAreaHeight);

    }
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Draw axis
    g2d.setColor(new Color(255, 255, 255));

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    g2d.setColor(new Color(120, 120, 120));
    g2d.setStroke(new BasicStroke(1));
    g2d.drawLine(hTickAreaWidth, plotHeight - 1, plotWidth - 1, plotHeight - 1);
    g2d.drawLine(0, 0, 0, dataAreaHeight);

    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Add Ticks
    double h = (maxY - minY) / 10;
    h = this.selectSpacer(h);

    g2d.setFont(new Font("Verdana", Font.BOLD, 12));

    double tickY = ((int) (maxY / h)) * h;
    while (tickY >= minY) {

      double tickY_tr = dataAreaHeight
          - (tickY - minY) / (maxY - minY) * dataAreaHeight;

      g2d.drawLine(2, (int) tickY_tr, 8, (int) tickY_tr);
      g2d.drawString(String.format("%5.2f", tickY), 9, (int) tickY_tr + 5);
      tickY -= h;

    }

    h = (maxX - minX) / 10;
    h = this.selectSpacer(h);

    g2d.setFont(new Font("Verdana", Font.BOLD, 12));
    FontMetrics fm = g2d.getFontMetrics();
    double tickX = ((int) (maxX / h)) * h;
    while (tickX >= minX) {

      double tickX_tr = (tickX - minX) / (maxX - minX) * dataAreaWidth;

      g2d.drawLine(hTickAreaWidth + (int) tickX_tr, plotHeight - 8,
          hTickAreaWidth + (int) tickX_tr, plotHeight - 2);

      String format = String.format("%3.1f", tickX).trim();
      int tickXWidth = fm.stringWidth(format);
      g2d.drawString(format, hTickAreaWidth + (int) (tickX_tr - tickXWidth / 2),
          plotHeight - 12);
      tickX -= h;
    }
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Plot lines
    g2d.setColor(Color.RED);
    g2d.setStroke(new BasicStroke(2));
    for (int i = 1; i < xData.length; i++) {
      double x1_tr = (xData[i - 1] - minX) / (maxX - minX) * dataAreaWidth
          + hTickAreaWidth;
      double x2_tr = (xData[i] - minX) / (maxX - minX) * dataAreaWidth
          + hTickAreaWidth;
      double y1_tr = dataAreaHeight
          - (yData[i - 1] - minY) / (maxY - minY) * dataAreaHeight;
      double y2_tr = dataAreaHeight
          - (yData[i] - minY) / (maxY - minY) * dataAreaHeight;
      g2d.drawLine((int) x1_tr, (int) y1_tr, (int) x2_tr, (int) y2_tr);
    }
    // ---------------------------------------------------------------------- //

    // End of Main Plot
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Objective Function Profile Plot

    if (this.showFittingProfiles) {

      // Reverse last translation back to origin
      g.translate(-(width - plotWidth) / 2, (int) -(height * 0.05));

      int profilePlotWidth = (int) (0.45 * width);
      int profilePlotHeight = (int) (0.45 * height);

      int drawWidth = (int) (profilePlotWidth * 0.85);
      int drawHeight = (int) (profilePlotHeight * 0.85);

      int pointsWidth = (int) (drawWidth * 0.95);
      int pointsHeight = (int) (drawHeight * 0.95);

      int hGapA = profilePlotWidth - drawWidth;
      int vGapA = 0;

      int hGapB = hGapA + (int) ((drawWidth - pointsWidth) / 2);
      int vGapB = vGapA + (int) ((drawHeight - pointsHeight) / 2);

      // apply new translation
      g.translate((int) (width * 0.025), (int) (height * 0.525));
      g2d.setStroke(new BasicStroke(1));
      g.setColor(Color.BLACK);

      // -------------------------------------------------------------------- //
      // Draw axis
      g2d.setColor(new Color(120, 120, 120));
      g2d.setStroke(new BasicStroke(1));
      g.drawLine(0, vGapB, 0, vGapB + pointsHeight);
      g.drawLine(hGapB, profilePlotHeight, profilePlotWidth, profilePlotHeight);
      // -------------------------------------------------------------------- //

      // -------------------------------------------------------------------- //
      // Draw ticks
      double max = this.objmeans[3];
      double min = Double.MAX_VALUE;
      for (int i = 0; i < this.objmeans.length; i++) {
        if (this.objmins[i] >= 0) {
          min = Math.min(this.objmins[i], min);
        }
      }
      
      h = (max - min) / 10;
      h = this.selectSpacer(h);

      g2d.setFont(new Font("Verdana", Font.BOLD, 9));

      tickY = ((int) (max / h)) * h;
      while (tickY >= min) {
        double tickY_tr = (int) pointsHeight + vGapB - ((int) ((tickY - min)
            * (pointsHeight) / (max - min)));

        g2d.drawLine(2, (int) tickY_tr, 8, (int) tickY_tr);
        g2d.drawString(String.format("%5.2f", tickY), 9, (int) tickY_tr + 5);
        tickY -= h;
      }
      
      System.out.println(this.iters.length);
      
      maxX = this.iters[this.iters.length-1];
      h = (int) (maxX) / 3;
      h = this.selectSpacer(h);

      g2d.setFont(new Font("Verdana", Font.BOLD, 9));
      fm = g2d.getFontMetrics();
      tickX = ((int) (maxX / h)) * h;
      while (tickX >= 0) {

        double tickX_tr = (tickX) / (maxX) * pointsWidth;

        g2d.drawLine(hGapB + (int) tickX_tr, profilePlotHeight - 8,
                     hGapB + (int) tickX_tr, profilePlotHeight - 2);

        String format = String.format("%d", (int)tickX).trim();
        int tickXWidth = fm.stringWidth(format);
        g2d.drawString(format, hGapB + (int) (tickX_tr - tickXWidth / 2),
            profilePlotHeight - 12);
        tickX -= h;
      }
      // -------------------------------------------------------------------- //


      // -------------------------------------------------------------------- //
      // Draw Points and max & mins
      g.translate(hGapB, vGapB);
      for (int i = 3; i < this.objmeans.length; i++) {
      
        if (this.objmeans[i] >= 0) {
        
        int cx = (int) (i * ( pointsWidth) / (this.objmeans.length));

        int cy = (int) pointsHeight - ((int) ((this.objmeans[i] - min)
            * (pointsHeight) / (max - min)));

        double cm = Math.min(this.objmaxs[i] ,max);
        
        int cyma = (int) pointsHeight - ((int) ((cm - min)
            * (pointsHeight) / (max - min)));

        int cymi = (int) pointsHeight - ((int) ((this.objmins[i] - min)
            * (pointsHeight) / (max - min)));

        
        g.drawLine(cx, cyma, cx, cymi);
        g.fillRect(cx - 2, cy - 2, 5, 5);
        
        }
        
      }
      // -------------------------------------------------------------------- //

    }
    // ---------------------------------------------------------------------- //

  }

  private double maxArray(double[] array) {
    double max = Double.MIN_VALUE;
    for (int i = 0; i < array.length; i++) {
      max = Math.max(array[i], max);
    }
    return max;
  }

  private double minArray(double[] array) {
    double min = Double.MAX_VALUE;
    for (int i = 0; i < array.length; i++) {
      min = Math.min(array[i], min);
    }
    return min;
  }

  private double selectSpacer(double h) {
    double[] spacers = new double[] { 2000,1000,500, 200, 100, 50, 20, 10, 5, 1, 0.5, 0.1, 0.05, 0.02,
        0.01, 0.005 };

    for (int i = 0; i < spacers.length; i++) {

      if (spacers[i] <= h) {
        return spacers[i];
      }
    }
    ;
    return 0.005;

  }

}
