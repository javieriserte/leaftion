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
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int width = this.getWidth();
		int height = this.getHeight();
		
		int plotWidth = (int)(width * 0.9);
		int plotHeight = (int)(height * 0.9);
		
		
		double maxY = this.maxArray(yData);
		double maxX = this.maxArray(xData);
		double minY = this.minArray(yData);
		double minX = this.minArray(xData);

		g.setColor(new Color( 255,255,255 ));
		
		g.fillRect(0, 0, width, height);

		int bgBoxSpacerSize = 53;
		int bgBoxSize = 50;
		for (int i = 0; i< (int)( width / bgBoxSpacerSize) +1; i++) {

			for (int j = 0; j < (int)( height/ bgBoxSpacerSize) +1; j++) {

				g.setColor(new Color( 252,252,252 ));
				g.fillRect(i*bgBoxSpacerSize, j*bgBoxSpacerSize, bgBoxSize, bgBoxSize);
				
				g.setColor(new Color( 240,240,240 ));
				g.drawRect(i*bgBoxSpacerSize, j*bgBoxSpacerSize, bgBoxSize, bgBoxSize);
				
				
			}
			
		}

		// ------------------------------------------------------------------ //
		// Draw Selected Area
		BufferedImage plot = new BufferedImage(
				plotWidth,
				plotHeight,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D plotGr = (Graphics2D) plot.getGraphics();
		// ------------------------------------------------------------------ //
		
		// ------------------------------------------------------------------ //
		// Draw axis
		plotGr.setColor(new Color( 255,255,255 ));
		
		plotGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int hTickAreaWidth = 50;
		int vTickAreaHeight = 50;
		int dataAreaWidth = plotWidth -hTickAreaWidth ;
		int dataAreaHeight = plotHeight - vTickAreaHeight;
		
		plotGr.setColor(new Color( 120,120,120 ));
		plotGr.setStroke(new BasicStroke(1));
		plotGr.drawLine(hTickAreaWidth, plotHeight-1, plotWidth-1, plotHeight-1);
		plotGr.drawLine(0 , 0, 0, dataAreaHeight);
		
		// ------------------------------------------------------------------ //


		// ------------------------------------------------------------------ //
		// Add Ticks

		double h = (maxY-minY)/10;
		h = this.selectSpacer(h);
		
		plotGr.setFont(new Font("Verdana",Font.BOLD,12));
		
		double tickY = ( (int) (maxY/h)) * h;
		while ( tickY >= minY ) {

			double tickY_tr = dataAreaHeight - ( tickY - minY ) / 
					( maxY - minY ) * dataAreaHeight;
			
			plotGr.drawLine(2 , (int)tickY_tr, 8, (int)tickY_tr);
			tickY_tr = Math.max(tickY_tr, 5 );
			plotGr.drawString(String.format("%5.2f", tickY), 9 , (int) tickY_tr + 5);
			tickY -= h;
			
		}
		
		h = (maxX-minX)/10;
		h = this.selectSpacer(h);
		
		plotGr.setFont(new Font("Verdana",Font.BOLD,12));
		FontMetrics fm = plotGr.getFontMetrics();
		double tickX = ( (int) (maxX/h)) * h;
		while ( tickX >= minX ) {

			double tickX_tr = ( tickX - minX ) / 
					( maxX - minX ) * dataAreaWidth;
			
			plotGr.drawLine(hTickAreaWidth + (int)tickX_tr, plotHeight-8  ,hTickAreaWidth +(int)tickX_tr, plotHeight -2) ;

			String format = String.format("%4.1f", tickX).trim();
			int tickXWidth = fm.stringWidth(format);
			tickX_tr = Math.min(tickX_tr, dataAreaWidth  -tickXWidth/2);
			plotGr.drawString(format, hTickAreaWidth + (int) (tickX_tr - tickXWidth/2), plotHeight-9 );
			tickX -= h;
			
		}
		
		// ------------------------------------------------------------------ //

		
		// ------------------------------------------------------------------ //
		// Plot links
		plotGr.setColor( Color.RED );
		plotGr.setStroke(new BasicStroke(2));
		System.out.println("Dibujando lineas");
		for (int i =1; i< xData.length; i++) {
			double x1_tr = (xData[i-1] - minX) / (maxX-minX) * dataAreaWidth +hTickAreaWidth;
			double x2_tr = (xData[i] - minX) / (maxX-minX) * dataAreaWidth +hTickAreaWidth;
			double y1_tr = dataAreaHeight - (yData[i-1] - minY) / (maxY-minY) * dataAreaHeight;
			double y2_tr = dataAreaHeight - (yData[i] - minY) / (maxY-minY) * dataAreaHeight;
			plotGr.drawLine((int)x1_tr , (int)y1_tr, (int)x2_tr, (int)y2_tr);
		}
		// ------------------------------------------------------------------ //

		g.translate( ( width -plotWidth)/2, ( height -plotHeight)/2);
		g.drawImage(plot, 0, 0, null);
		
		
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
		double[] spacers = new double[]{1,0.5,0.1,0.05,0.01,0.005};
		
		for (int i = 1; i < spacers.length ; i++) {
			
			if (spacers[i] <= h) {
				return spacers[i];
			}
		};
		return 0.005;
		
	}

}
