package org.jiserte.leaftion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicBorders;

import org.jiserte.leaftion.image.ImageLoader;
import org.jiserte.leaftion.math.MotionEstimator;
import org.jiserte.leaftion.math.Motions;
import org.jiserte.multiselectimagepanel.MultiSelectImagePanel;

public class Leaftion extends JFrame{

  public static void main(String[] args) {
	  
	  
	    try {
	    	UIManager.setLookAndFeel(
	    			UIManager.getSystemLookAndFeelClassName());
	    	
	    	SwingUtilities.invokeLater(new Runnable() {
		        public void run() {
		         
		          Leaftion pt = new Leaftion();

		          // creates the main instance
		          
		          
		          pt.setVisible(true);
		          pt.setPreferredSize(new Dimension(1024,768));
		          pt.setSize(new Dimension(1024,768));
		          pt.setLocationRelativeTo(null);
		          pt.setTitle("Leaftion");
		          pt.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		          
		          pt.createGUI();
		          
		          pt.pack();
		          
		          
		        }
		      });
	    	
	    	
	    	} catch (Exception e){
	    		System.out.println(e);
	    	}
	    
	    
	    
	  
//	  
//
//    File folder = new File("C:\\Javier\\TRiP-master\\input\\crop_plant1");
//    
//    File outfile = new File("C:\\Javier\\TRiP-master\\input\\my_plat.csv");
//    
//    try {
//      
//      List<BufferedImage> images = (new ImageLoader()).loadFromFolder(folder);
//      
//      MotionEstimator mest = new MotionEstimator();
//      
//      Motions motions = mest.estimateMotionInSeries(images);
//      
//      PrintStream out = new PrintStream(outfile);
//      
//      for ( int i =0 ; i< motions.getV_motion().length ; i++ ) {
//        
//        out.println( String.format(new Locale("es", "ar"), "%6.4f;%6.4f ",motions.getV_motion()[i] ,motions.getH_motion()[i])) ;
//        
//      }
//      
//      out.close();
//      
//      
//    
//    } catch (IOException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
    
  }

protected void createGUI() {
	
	JLabel textField = new JLabel("Ninguna carpeta elegida");
	
//	textField.setBorder(BorderFactory.createBevelBorder(1));
	
	textField.setPreferredSize( new Dimension(350,50) );
	
	JButton button = new JButton("Buscar Carpeta");
	
	button.setToolTipText("Define la carpeta con las imágenes");

	JButton buttonBright = new JButton("Brillante");
	
	JButton preButton = new JButton("<<");
	
	JButton nextButton = new JButton(">>");
	
	JLabel counterLabel = new JLabel("0");
	
	
//	counterLabel.setBorder(BorderFactory.createBevelBorder(1));
	
	counterLabel.setPreferredSize( new Dimension(50,50) );
	
	JButton runButton = new JButton("Run!");
	
	JToolBar tb  = new JToolBar();

	tb.add(button);

	tb.addSeparator();

	tb.add(textField);
	
	tb.addSeparator();
	
	tb.add(buttonBright);
	
	tb.addSeparator();

	tb.add(preButton);
	
	
	tb.add(nextButton);

	tb.addSeparator();
	
	tb.add(counterLabel);
	
	tb.addSeparator();

	tb.add(runButton);

	
	tb.setRollover(true);
	
	tb.setFloatable(false);
	
	tb.setPreferredSize(new Dimension(100,50)); 

	
	this.add(tb,BorderLayout.PAGE_START);

		
	this.add(new MultiSelectImagePanel(), BorderLayout.CENTER);

	
}

}
