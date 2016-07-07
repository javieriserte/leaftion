package org.jiserte.leaftion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicBorders;

import org.jiserte.leaftion.image.ImageLibrary;
import org.jiserte.leaftion.image.ImageLoader;
import org.jiserte.leaftion.math.MotionEstimator;
import org.jiserte.leaftion.math.Motions;
import org.jiserte.multiselectimagepanel.MultiSelectImagePanel;

public class Leaftion extends JFrame {

	private File imageFolder = null;
	
	private ImageLibrary library;
	
	// ------------------------------------------------------------------------ //
	// Components
	private JLabel currentFolderLabel;

  private MultiSelectImagePanel imagePanel;
  // ------------------------------------------------------------------------ //
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1917154020995579885L;

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					Leaftion pt = new Leaftion();

					// creates the main instance

					pt.setVisible(true);
					pt.setPreferredSize(new Dimension(1024, 768));
					pt.setSize(new Dimension(1024, 768));
					pt.setLocationRelativeTo(null);
					pt.setTitle("Leaftion");
					pt.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

					pt.createGUI();

					pt.pack();

				}

			});

		} catch (Exception e) {
			System.out.println(e);
		}

		//
		//
		// File folder = new
		// File("C:\\Javier\\TRiP-master\\input\\crop_plant1");
		//
		// File outfile = new
		// File("C:\\Javier\\TRiP-master\\input\\my_plat.csv");
		//
		// try {
		//
		// List<BufferedImage> images = (new
		// ImageLoader()).loadFromFolder(folder);
		//
		// MotionEstimator mest = new MotionEstimator();
		//
		// Motions motions = mest.estimateMotionInSeries(images);
		//
		// PrintStream out = new PrintStream(outfile);
		//
		// for ( int i =0 ; i< motions.getV_motion().length ; i++ ) {
		//
		// out.println( String.format(new Locale("es", "ar"), "%6.4f;%6.4f
		// ",motions.getV_motion()[i] ,motions.getH_motion()[i])) ;
		//
		// }
		//
		// out.close();
		//
		//
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	protected void createGUI() {

	   Dimension sepDimension = new Dimension(30,10);
	   
		this.currentFolderLabel = new JLabel("Ninguna carpeta elegida");

		currentFolderLabel.setMinimumSize(new Dimension(250, 50));
		currentFolderLabel.setPreferredSize(new Dimension(450, 50));
		currentFolderLabel.setMaximumSize(new Dimension(450, 50));

		JButton button = new JButton("Buscar Carpeta");
		
		button.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setMultiSelectionEnabled(false);
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.showOpenDialog(Leaftion.this);
				File selectedFile = fc.getSelectedFile();
				if (selectedFile !=null) {
				  Leaftion.this.setImageFolder(selectedFile);
				}
			}

		} );

		button.setToolTipText("Define la carpeta con las imágenes");

		JButton buttonBright = new JButton("Brillante");
		
		buttonBright.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        
        BufferedImage im = Leaftion.this.library.getBrightCompositeImage();
        
        if (im == null) {

          JDialog dialog = new JDialog(Leaftion.this);
          
          //dialog.setUndecorated(true);
          
          dialog.setLocation((int)Leaftion.this.getLocation().getX()+200, ((int)Leaftion.this.getLocation().getY()+200));
          
          JPanel panel = new JPanel();
          
          panel.add( new JLabel("Please wait") );
          
          dialog.add(panel);
          
          dialog.pack();

          dialog.setVisible(true);
          
          dialog.repaint();

          SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
              Leaftion.this.library.makeBrightComposite();
              dialog.dispose();
              BufferedImage img = Leaftion.this.library.getBrightCompositeImage();
              Leaftion.this.imagePanel.setImage(img);
              
            }
          });
          return ; 
        } else {
        
          Leaftion.this.imagePanel.setImage(im);
          
        }
        
      }
    });

		JButton preButton = new JButton("<<");

		JButton nextButton = new JButton(">>");

		JLabel counterLabel = new JLabel("0");

		counterLabel.setMinimumSize(new Dimension(30, 50));
		counterLabel.setPreferredSize(new Dimension(50, 50));
		counterLabel.setMaximumSize(new Dimension(50, 50));
    
		JButton runButton = new JButton("Run!");
		
		runButton.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {

        MotionEstimator mest = new MotionEstimator();
        
        List<Rectangle> regions = Leaftion.this.imagePanel.getRegions();
        

        List<List<BufferedImage>> imagesRegions = Leaftion.this.library.cropRegions(regions);

        List<Motions> motionList= new ArrayList<>();
        
        for (List<BufferedImage> images : imagesRegions) {
        
          Motions motions = mest.estimateMotionInSeries(images);
          
          motionList.add(motions);
          
        }
        
        
        FileDialog out = new FileDialog(Leaftion.this, "Elija archivo de salida", FileDialog.SAVE);
        
        out.setVisible(true);

        System.out.println(out.getFile());
        
        File outfile = new File( out.getDirectory(), out.getFile());
        
        System.out.println(outfile.getAbsolutePath());
        
        PrintStream os;
        try {
          os = new PrintStream(outfile);
          
          for (int i = 0; i< motionList.get(0).getV_motion().length; i++) {
            
            boolean firstField = true;
            for (int j = 0; j < motionList.size(); j++) {
              if (!firstField) {
                os.print(";");
              }
              os.print( String.format(new Locale( "es", "AR" ), "%8.5f" ,motionList.get(j).getV_motion()[i] ));
              firstField = false;
            }
            os.println("");
            
          }
          
          os.close();

        } catch (FileNotFoundException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }

        

        
        
        
      }
    });

		JToolBar tb = new JToolBar();

		tb.add(button);

		tb.addSeparator(sepDimension);

		tb.add(this.currentFolderLabel);

		tb.addSeparator(sepDimension);

		tb.add(buttonBright);

		tb.addSeparator(sepDimension);

		tb.add(preButton);

		tb.add(nextButton);

		tb.addSeparator(sepDimension);

		tb.add(counterLabel);

    tb.addSeparator(sepDimension);

		tb.add(runButton);

		tb.setRollover(true);

		tb.setFloatable(false);

		tb.setPreferredSize(new Dimension(1, 35));
		
		tb.setMargin(new Insets(2, 2, 2, 2));

		this.add(tb, BorderLayout.PAGE_START);

		this.imagePanel = new MultiSelectImagePanel();
		
    this.add(new JScrollPane(this.imagePanel), BorderLayout.CENTER);

	}
	
	
  private void setImageFolder(File selectedFile) {
    
    this.imageFolder = selectedFile;
    
    this.currentFolderLabel.setText(this.imageFolder.getAbsolutePath());
    
    this.library = new ImageLibrary();
    
    this.library.getImageFilesInFolder(this.imageFolder);
    
    BufferedImage im = this.library.getCurrentImage();
    
    if (im != null) {
    
      this.imagePanel.setImage(im);
    
    }
    
  }


}
