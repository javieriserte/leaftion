package org.jiserte.leaftion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;

import javax.swing.JPanel;

public class MotionPlotPanel extends JPanel {
	
	public double[] xData;
	public double[] yData;
	
	public int startSelectIndex=2;
	public int endSelectIndex=5;
	

//	 @Override
//	  protected void paintComponent(Graphics g) {
//	    super.paintComponent(g);
//	 
//	 }

	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g; 
		
		
		int width = this.getWidth();
		int height = this.getHeight();
		
		int plotWidth = (int)(width * 0.95);
		int plotHeight = (int)(height * 0.95);
		
    
    int hTickAreaWidth = 50;
    int vTickAreaHeight = 50;
    int dataAreaWidth = plotWidth -hTickAreaWidth ;
    int dataAreaHeight = plotHeight - vTickAreaHeight;
    



		g2d.setColor(new Color( 255,255,255 ));
		
		g2d.fillRect(0, 0, width, height);

		int bgBoxSpacerSize = 53;
		int bgBoxSize = 50;
		for (int i = 0; i< (int)( width / bgBoxSpacerSize) +1; i++) {

			for (int j = 0; j < (int)( height/ bgBoxSpacerSize) +1; j++) {

			  g2d.setColor(new Color( 252,252,252 ));
			  g2d.fillRect(i*bgBoxSpacerSize, j*bgBoxSpacerSize, bgBoxSize, bgBoxSize);
				
			  g2d.setColor(new Color( 240,240,240 ));
			  g2d.drawRect(i*bgBoxSpacerSize, j*bgBoxSpacerSize, bgBoxSize, bgBoxSize);
				
				
			}
			
		}
		
    if (xData==null || xData.length<=1) {
      return;
    }
    
    double maxY = this.maxArray(yData);
    double maxX = this.maxArray(xData);
    double minY = this.minArray(yData);
    double minX = this.minArray(xData);
    g.translate( ( width -plotWidth)/2, ( height -plotHeight)/2);
		// ------------------------------------------------------------------ //
		// Draw Selected Area
    if (this.endSelectIndex > this.startSelectIndex) {
      
      int x1_tr = (int) ((xData[this.startSelectIndex] - minX) / (maxX-minX) * dataAreaWidth +hTickAreaWidth);
      int x2_tr = (int) ((xData[this.endSelectIndex] - minX) / (maxX-minX) * dataAreaWidth +hTickAreaWidth);

      g.setColor(new Color(200,245,200,60));
      g.fillRect(x1_tr, 0, x2_tr-x1_tr, dataAreaHeight);
      g.setColor(new Color(30,255,30,90));
      g.drawRect(x1_tr, 0, x2_tr-x1_tr, dataAreaHeight);
      
    }
    
    // ------------------------------------------------------------------ //
		
		// ------------------------------------------------------------------ //
		// Draw axis
		g2d.setColor(new Color( 255,255,255 ));
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		
		g2d.setColor(new Color( 120,120,120 ));
		g2d.setStroke(new BasicStroke(1));
		g2d.drawLine(hTickAreaWidth, plotHeight-1, plotWidth-1, plotHeight-1);
		g2d.drawLine(0 , 0, 0, dataAreaHeight);
		
		// ------------------------------------------------------------------ //


		// ------------------------------------------------------------------ //
		// Add Ticks

		double h = (maxY-minY)/10;
		h = this.selectSpacer(h);
		
		g2d.setFont(new Font("Verdana",Font.BOLD,12));
		
		double tickY = ( (int) (maxY/h)) * h;
		while ( tickY >= minY ) {

			double tickY_tr = dataAreaHeight - ( tickY - minY ) / 
					( maxY - minY ) * dataAreaHeight;
			
			g2d.drawLine(2 , (int)tickY_tr, 8, (int)tickY_tr);
			g2d.drawString(String.format("%5.2f", tickY), 9 , (int) tickY_tr + 5);
			tickY -= h;
			
		}
		
		h = (maxX-minX)/10;
		h = this.selectSpacer(h);
		
		g2d.setFont(new Font("Verdana",Font.BOLD,12));
		FontMetrics fm = g2d.getFontMetrics();
		double tickX = ( (int) (maxX/h)) * h;
		while ( tickX >= minX ) {

			double tickX_tr = ( tickX - minX ) / 
					( maxX - minX ) * dataAreaWidth;
			
			g2d.drawLine(hTickAreaWidth + (int)tickX_tr, plotHeight-8  ,hTickAreaWidth +(int)tickX_tr, plotHeight -2) ;

			String format = String.format("%3.1f",tickX ).trim();
			int tickXWidth = fm.stringWidth(format);
			g2d.drawString(format, hTickAreaWidth + (int) (tickX_tr - tickXWidth/2), plotHeight-12 );
			tickX -= h;
			
		}
		
		// ------------------------------------------------------------------ //

		
		// ------------------------------------------------------------------ //
		// Plot links
		g2d.setColor( Color.RED );
		g2d.setStroke(new BasicStroke(2));
		for (int i =1; i< xData.length; i++) {
			double x1_tr = (xData[i-1] - minX) / (maxX-minX) * dataAreaWidth +hTickAreaWidth;
			double x2_tr = (xData[i] - minX) / (maxX-minX) * dataAreaWidth +hTickAreaWidth;
			double y1_tr = dataAreaHeight - (yData[i-1] - minY) / (maxY-minY) * dataAreaHeight;
			double y2_tr = dataAreaHeight - (yData[i] - minY) / (maxY-minY) * dataAreaHeight;
			g2d.drawLine((int)x1_tr , (int)y1_tr, (int)x2_tr, (int)y2_tr);
		}
		// ------------------------------------------------------------------ //

	}
	
	
	private double maxArray(double[] array) {
		double max = Double.MIN_VALUE;
		for (int i = 0 ; i< array.length; i++) {
			max = Math.max(array[i], max);
		}
		return max;
	}
	
	private double minArray(double[] array) {
		double min = Double.MAX_VALUE;
		for (int i = 0 ; i< array.length; i++) {
			min = Math.min(array[i], min);
		}
		return min;
	}

	private double selectSpacer(double h){
		double[] spacers = new double[]{50,20,10,5,1,0.5,0.1,0.05,0.02,0.01,0.005};
		
		for (int i = 0; i < spacers.length ; i++) {
			
			if (spacers[i] <= h) {
				return spacers[i];
			}
		};
		return 0.005;
		
	}

}
