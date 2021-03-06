package org.jiserte.leaftion;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jiserte.leaftion.events.ProcessingFramesEvent;
import org.jiserte.leaftion.events.ProcessingFramesListener;
import org.jiserte.leaftion.image.ImageLibrary;
import org.jiserte.leaftion.image.filenamefilter.JpegFileNameFilter;
import org.jiserte.leaftion.image.filenamefilter.PngFileNameFilter;
import org.jiserte.leaftion.logpanel.LogItem;
import org.jiserte.leaftion.logpanel.LoggingListPanel;
import org.jiserte.leaftion.math.MotionEstimator;
import org.jiserte.leaftion.math.Motions;
import org.jiserte.leaftion.thumbimagelist.ThumbImageList;
import org.jiserte.multiselectimagepanel.MultiSelectImagePanel;

public class Leaftion extends JFrame {

  // ------------------------------------------------------------------------ //
  // Class Constants
  private static final long serialVersionUID = -1917154020995579885L;
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  // Instance variables
  private ImageLibrary library;
  private Locale locale;
  private List<Motions> motionDetectionResults;
  private boolean hasUnsavedResults;
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  // Components
//  private JLabel currentFolderLabel;
  private MultiSelectImagePanel imagePanel;
  private JButton buttonBright;
  private JButton preButton;
  private JButton nextButton;
  private JScrollPane logScrollPane;
  private JButton runButton;
  private JLabel counterLabel;
  private JLabel localeLabel;
  private JComboBox<String> localeComboBox;
  private JButton optimButton;
  private TextField intervalTxt;
  private LoggingListPanel log;
  private JButton saveButton;
  private JButton exportRegionButton;
  private JButton loadRegionsButton;
  private ThumbImageList thumbnails;
  // ------------------------------------------------------------------------ //



  public static void main(String[] args) {

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          // ---------------------------------------------------------------- //
          // creates the main instance
          Leaftion pt = new Leaftion();
          pt.hasUnsavedResults = false;
          pt.setVisible(true);
          pt.setPreferredSize(new Dimension(1024, 768));
          pt.setSize(new Dimension(1024, 768));
          pt.setLocationRelativeTo(null);
          pt.setTitle("Leaftion");
          pt.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE);
          pt.addWindowListener(new WindowListener() {
            
            @Override public void windowOpened(WindowEvent e) {  }
            @Override public void windowIconified(WindowEvent e) {  }
            @Override public void windowDeiconified(WindowEvent e) {  }
            @Override public void windowDeactivated(WindowEvent e) {  }
            
            @Override public void windowClosing(WindowEvent e) {
              Leaftion l = (Leaftion ) e.getSource();
              if ( l.hasUnsavedResults ) {
                int a = JOptionPane.showConfirmDialog(l, "You have unsaved results, exit anyway?", "Exit", JOptionPane.YES_NO_OPTION);
                if (a == JOptionPane.YES_OPTION) { l.dispose(); }
              } else { l.dispose(); }
            }
            
            @Override public void windowClosed(WindowEvent e) {  }
            @Override public void windowActivated(WindowEvent e) {  }

          });
          pt.createGUI();
          pt.pack();
          // ---------------------------------------------------------------- //
        }
      });
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public Leaftion() {
    super();
    this.motionDetectionResults = new ArrayList<>();
  }

  protected void createGUI() {

    // ---------------------------------------------------------------------- //
    // Define variables comunes
    Dimension sepDimension = new Dimension(7, 10);
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Define el label para mostrar el nombre de la carpeta
//    this.currentFolderLabel = new JLabel("No folder selected");
//    currentFolderLabel.setMinimumSize(new Dimension(150, 50));
//    currentFolderLabel.setPreferredSize(new Dimension(250, 50));
//    currentFolderLabel.setMaximumSize(new Dimension(250, 50));
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Define el botón para buscar la carpeta de imágenes
    JButton button = new JButton(  new ImageIcon("resources/icons/folder.32.png"));
    button.addActionListener(new OpenFolderButtonActionListener());
    button.setToolTipText("Select the image files");
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Define el botón para mostrar las imágenes con el efecto brillante
    this.buttonBright = new JButton(new ImageIcon("resources/icons/Bright.32.png"));
    buttonBright.setEnabled(false);
    buttonBright.addActionListener(new BrightButtonActionListener());
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Define botones para buscar entre imágenes
    this.preButton = new JButton("<<");
    this.preButton.setEnabled(false);
    this.preButton.addActionListener(new NavigateImageActionListener());
    this.preButton.setActionCommand("PREVIOUS");
    
    this.nextButton = new JButton(">>");
    this.nextButton.setEnabled(false);
    this.nextButton.addActionListener(new NavigateImageActionListener());
    this.nextButton.setActionCommand("NEXT");
    
    this.counterLabel = new JLabel("0");
    counterLabel.setMinimumSize(new Dimension(30, 50));
    counterLabel.setPreferredSize(new Dimension(50, 50));
    counterLabel.setMaximumSize(new Dimension(50, 50));
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Define el botón para iniciar la ejecución
    this.runButton = new JButton( new ImageIcon("resources/icons/run.32.png") );
    this.runButton.setEnabled(false);

    runButton.addActionListener(new RunButtonActionListener());
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Selecciona el Locale
    this.localeLabel = new JLabel("Locale:");
    this.localeLabel
        .setToolTipText("Choose Locale for output data");
    this.localeComboBox = new JComboBox<>(new String[] { "es_AR", "en_US" });
    this.localeComboBox.setPreferredSize(new Dimension(80, 25));
    this.localeComboBox.setMaximumSize(new Dimension(80, 25));

    this.localeComboBox.addItemListener(new LocaleSelectionItemListener());
    // ---------------------------------------------------------------------- //

    
    // ---------------------------------------------------------------------- //
    // Show fitting Panel Button
    this.optimButton = new JButton(
        new ImageIcon("resources/icons/cosine.32.png"));
    this.optimButton.addActionListener(new OptimizeButtonActionListener());
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    this.intervalTxt = new TextField();
    this.intervalTxt.setEditable(true);
    this.intervalTxt.setText("1.0");
    this.intervalTxt.addTextListener(new ValidateIntervalTextListener());
    this.intervalTxt.setPreferredSize(new Dimension(40, 20));
    this.intervalTxt.setMaximumSize(new Dimension(40, 20));

    // ---------------------------------------------------------------------- //
    // Define Save button
    this.saveButton = new JButton(
        new ImageIcon("resources/icons/save.32.png"));
    this.saveButton.addActionListener(new SaveButtonActionListener());
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Define export regions
    this.exportRegionButton = new JButton("E. R.");
    this.exportRegionButton.addActionListener(new ExportButtonActionListener());
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Define export regions
    this.loadRegionsButton = new JButton("L.R");
    this.loadRegionsButton.addActionListener(new LoadRegionsButtonActionListener());
    // ---------------------------------------------------------------------- //
    
    // ---------------------------------------------------------------------- //
    // Define la toolbar
    JToolBar tb = new JToolBar();

    tb.addSeparator(new Dimension(5, 0));
    tb.add(button);
    tb.add(this.saveButton);
    tb.add(this.exportRegionButton);
    tb.add(this.loadRegionsButton);
    tb.addSeparator(new Dimension(3, 0));
//    tb.add(this.currentFolderLabel);
    tb.addSeparator(sepDimension);
    tb.add(buttonBright);
    tb.addSeparator(sepDimension);
    tb.add(preButton);
    tb.add(nextButton);
    tb.addSeparator(sepDimension);
    tb.add(counterLabel);
    tb.addSeparator(sepDimension);
    tb.add(runButton);
    tb.addSeparator(sepDimension);
    tb.add(this.localeLabel);
    tb.add(this.localeComboBox);
    tb.addSeparator(sepDimension);
    tb.add(new JLabel("Interval (h):"));
    tb.add(this.intervalTxt);
    tb.addSeparator(sepDimension);
    tb.add(this.optimButton);

    this.localeComboBox.setSelectedIndex(0);
    this.setLocale( this.localeComboBox.getItemAt(0) );

    tb.setRollover( true );
    tb.setFloatable( false );
    tb.setPreferredSize( new Dimension(1, 36) );
    tb.setMargin( new Insets(2, 2, 2, 2) );
    this.add(tb, BorderLayout.PAGE_START);
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Thumbnails list
    this.thumbnails = new ThumbImageList();
    this.thumbnails.setMinimumSize(new Dimension(100,100));
    this.thumbnails.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.thumbnails.addListSelectionListener(new ListSelectionListener() {
      
      @Override
      public void valueChanged(ListSelectionEvent e) {
        try {
          int index = Leaftion.this.thumbnails.getSelectedIndex();
          BufferedImage im = Leaftion.this.library.imageAt(index);
          Leaftion.this.imagePanel.setImage(im);
          Leaftion.this.counterLabel.setText(String.valueOf(index+1));
        } catch (Exception ex) {
        
        }
      }
    } );
    JScrollPane jspThumbs = new JScrollPane(this.thumbnails);
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // image panel
    this.imagePanel = new MultiSelectImagePanel();
    JScrollPane jspIm = new JScrollPane(this.imagePanel);
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // image panel
    JSplitPane jSplitPane = new JSplitPane();
    jSplitPane.setRightComponent(jspIm);
    jSplitPane.setLeftComponent(jspThumbs);
    this.add(jSplitPane, BorderLayout.CENTER);
    // ---------------------------------------------------------------------- //
    
    
    // ---------------------------------------------------------------------- //
    // Add Key bindings for image navigation
    this.imagePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke("control LEFT"), "previousFrame");
    this.imagePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke("control RIGHT"), "nextFrame");

    AbstractAction preFrameAction = new NavigateAction(
        NavigateAction.PREVIOUS_IMAGE_ACTION);

    AbstractAction nextFrameAction = new NavigateAction(
        NavigateAction.NEXT_IMAGE_ACTION);

    preFrameAction.setEnabled(true);
    this.imagePanel.getActionMap().put("previousFrame", preFrameAction);
    this.imagePanel.getActionMap().put("nextFrame", nextFrameAction);
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Log Panel
    this.log = new LoggingListPanel(100);
    this.logScrollPane = new JScrollPane(this.log);
    this.log.addVolatileMessage(new LogItem("Welcome!",LoggingListPanel.NORMAL_TYPE));
    //this.log.addMessage(new LogItem("Welcome!", LoggingListPanel.NORMAL_TYPE));
    this.add(this.logScrollPane, BorderLayout.SOUTH);
    // ---------------------------------------------------------------------- //
    

  }
  
  @Update
  private void setImageFiles( File[] files ) {
    
    this.library = new ImageLibrary();
    
    // TODO: Change these listener for UIworkers
    this.library.addCropImageProccessingListener( new CroppingProccessFrameListener() );

    this.library.addBrightImageProccesingListener( new BrightProccessFrameListener() );
    
    this.library.setImageFiles(files);

    BufferedImage im = this.library.getCurrentImage();

    if (im != null) {
      this.imagePanel.setImage(im);
      this.counterLabel.setText("1");
      this.counterLabel.setToolTipText(
          "Control+Izq y Control+Der para ir a la imagen siguiente o previa.");
    }

  }

  public Locale getLocale() {
    return locale;
  }

  protected void setLocale(String item) {
    String[] a = item.split("_");
    this.locale = new Locale(a[0], a[1]);
  }
  
  // ------------------------------------------------------------------------ //
  // Auxiliary classes
  
  class NavigateAction extends AbstractAction {
    
    private static final long serialVersionUID = -8050051488649477806L;
    private static final int NEXT_IMAGE_ACTION = 0;
    private static final int PREVIOUS_IMAGE_ACTION = 1;
    private int actionType;
    
    public NavigateAction(int actionType) {
      super();
      this.actionType = actionType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      ImageLibrary library = Leaftion.this.library;
      if (library != null) {
        BufferedImage im = null; 
        switch (this.actionType) {
          case PREVIOUS_IMAGE_ACTION:
            im = library.previous();
            break;
          case NEXT_IMAGE_ACTION:
            im = library.next();
            break;
          default:
            return;
        } 
        if (im != null) {
          Leaftion.this.imagePanel.setImage(im);
          String counter = String.valueOf(library.getImageIndex());
          Leaftion.this.counterLabel.setText(counter);
        }
      }
    }
  }
  
  class ValidateIntervalTextListener implements TextListener {
    @Override
    public void textValueChanged(TextEvent e) {
      String text = intervalTxt.getText();
      if (text.trim().length() > 0) {
        try {
          Double.parseDouble(text);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, "Enter a number.");
          intervalTxt.setText("1.0");
        }
      }
    }
  }
  
  class LocaleSelectionItemListener implements ItemListener {
    @Override
    public void itemStateChanged(ItemEvent e) {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        Leaftion.this.setLocale((String) e.getItem());
      }
    }
  }
  
  class NavigateImageActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      BufferedImage im = null;
      if (e.getActionCommand()=="NEXT") {
        im = Leaftion.this.library.next();
      } else {
        im = Leaftion.this.library.previous();
      }
      if (im != null) {
        Leaftion.this.imagePanel.setImage(im);
        String counter = String
            .valueOf(Leaftion.this.library.getImageIndex());
        Leaftion.this.counterLabel.setText(counter);
      }
    }
  }
  class BrightButtonActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {

      BufferedImage im = Leaftion.this.library.getBrightCompositeImage();
      if (im == null) {

        Runnable runnable = new Runnable() {
          @Override
          public void run() {
            Leaftion.this.library.makeBrightComposite();
            BufferedImage img = Leaftion.this.library
                .getBrightCompositeImage();
            Leaftion.this.imagePanel.setImage(img);
          }
        };

        Thread t = new Thread(runnable);
        t.start();
        return;
      } else {
        Leaftion.this.imagePanel.setImage(im);
      }

    }
  }
  
  class OpenFolderButtonActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent arg0) {

      JFileChooser fc = new JFileChooser();
      fc.setMultiSelectionEnabled(true);
      fc.setFileSelectionMode(  JFileChooser.FILES_ONLY );

      fc.addChoosableFileFilter( new PngFileNameFilter());
      fc.addChoosableFileFilter( new JpegFileNameFilter());
      
      fc.showOpenDialog(Leaftion.this);
      File[] selectedFiles = fc.getSelectedFiles();
      
      Arrays.sort( selectedFiles, new Comparator<File>() {
        @Override public int compare(File o1, File o2) {
          return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
        } } );
      
      if (selectedFiles.length > 0) { 
        Leaftion.this.setImageFiles( selectedFiles ); 
        LogItem msg = new LogItem(
            Leaftion.this.library.getImageFiles().length + " Images Found",
            LoggingListPanel.NORMAL_TYPE );
        Leaftion.this.log.addVolatileMessage(  msg  );
        Leaftion.this.thumbnails.loadImages(   Leaftion.this.library.getImageFiles() );
        Leaftion.this.buttonBright.setEnabled( true );
        Leaftion.this.preButton.setEnabled(    true );
        Leaftion.this.nextButton.setEnabled(   true );
        Leaftion.this.runButton.setEnabled(    true );
        
      } else {
        
        LogItem msg = new LogItem("No Images Found",
            LoggingListPanel.ERROR_TYPE);
        Leaftion.this.log.addVolatileMessage(msg);
        
        
      }

    }

  }
  
  class SaveButtonActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent event) {
      
      List<Motions> motionList = Leaftion.this.motionDetectionResults;
      List<String>     labels = Leaftion.this.imagePanel.getLabels();
      
      if ( Leaftion.this.motionDetectionResults !=null &&
          Leaftion.this.motionDetectionResults.size()>0)  {
        
        String directory = null;
        String file = null;
        
        FileDialog out = new FileDialog(Leaftion.this,
            "Select output file", FileDialog.SAVE);
        out.setVisible(true);

        directory = out.getDirectory();
        file = out.getFile();
        
        if (directory == null || file == null) {
          LogItem msg = new LogItem("File not found or save cancelled",
              LoggingListPanel.WARNING_TYPE);
          Leaftion.this.log.addVolatileMessage(msg);
//          Leaftion.this.log.addMessage(msg);
          return;
        }
        
        File outfile = new File(directory, file);

        PrintStream os;
        try {
          os = new PrintStream(outfile);

          boolean firstField = true;
          for (int i = 0; i < labels.size(); i++) {
            if (!firstField) {
              os.print(";");
            }
            os.print(labels.get(i));
            firstField = false;
          }
          os.println("");

          for (int i = 0; i < motionList.get(0).getV_motion().length; i++) {

            firstField = true;
            for (int j = 0; j < motionList.size(); j++) {
              if (!firstField) {
                os.print(";");
              }
              os.print(String.format(Leaftion.this.getLocale(), "%8.5f",
                  motionList.get(j).getV_motion()[i]));
              firstField = false;
            }
            os.println("");

          }
          Leaftion.this.hasUnsavedResults = false;
          os.close();

          LogItem msg = new LogItem(
              "Results Saved on file: " + outfile.getAbsolutePath(),
              LoggingListPanel.NORMAL_TYPE);
          Leaftion.this.log.addVolatileMessage(msg);
//          Leaftion.this.log.addMessage(msg);

        } catch (FileNotFoundException e1) {
          e1.printStackTrace();
        }
        
      }
      
    }
    
  }

  class RunButtonActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {

      MotionEstimator mest = new MotionEstimator();
      List<Rectangle> regions = Leaftion.this.imagePanel.getRegions();

      Thread t = new Thread(new Runnable() {

        @Override
        public void run() {
          
          // ---------------------------------------------------------------- //
          // Get the regions
          List<List<BufferedImage>> imagesRegions = Leaftion.this.library
              .cropRegions(regions);
          // ---------------------------------------------------------------- //

          // ---------------------------------------------------------------- //
          // Checl if there are regions defined
          if (imagesRegions.size() == 0) {
            LogItem msg = new LogItem("No regions defined!",
                LoggingListPanel.ERROR_TYPE);
            Leaftion.this.log.addVolatileMessage(msg);
            return;
          }
          // ---------------------------------------------------------------- //


          // ---------------------------------------------------------------- //
          // Log start of motion estimation
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              LogItem msg = new LogItem("Estimating motions",
                  LoggingListPanel.NORMAL_TYPE);
              Leaftion.this.log.addVolatileMessage(msg);
            }
          });
          // ---------------------------------------------------------------- //

          // ---------------------------------------------------------------- //
          // Estimate motions
          List<Motions> motionList = new ArrayList<>();
          for (List<BufferedImage> images : imagesRegions) {
            Motions motions = mest.estimateMotionInSeries(images);
            motionList.add(motions);
          }
          Leaftion.this.motionDetectionResults = motionList;
          Leaftion.this.hasUnsavedResults = true;
          // ---------------------------------------------------------------- //
          
          // ---------------------------------------------------------------- //
          // Log End of estimation
          LogItem msg = new LogItem("Estimating motions Done",
              LoggingListPanel.NORMAL_TYPE);
          Leaftion.this.log.addVolatileMessage(msg);
          // ---------------------------------------------------------------- //
        }
      });

      t.start();

    }
  }
  
  class OptimizeButtonActionListener implements ActionListener {

    
    @SuppressWarnings("unused")
    private Motions dummyMotions(double per, double noise) {

      Motions m = new Motions();
      
      double[] mot = new double[300];
      
      for (int i = 0; i< 300; i++) {
        mot[i] = 0.5 * Math.cos(2 * Math.PI * ( i + 2 ) / per) + Math.random() * noise - noise/2;
      }
          
      m.setV_motion(mot);
      m.setH_motion(mot);
      
      return m;
      
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

      SwingUtilities.invokeLater(new Runnable() {

        
        @Override
        public void run() {

          JFrame optFrame = new JFrame();
          OptimizePanel optimizePanel = new OptimizePanel();
          
          FittedMotions[] fmot = new FittedMotions[
              Leaftion.this.motionDetectionResults.size()];

          optimizePanel
              .setInterval(Double.parseDouble(intervalTxt.getText()));

          for (int i = 0; i < fmot.length; i++) {
            fmot[i] = new FittedMotions();
            fmot[i].fittedModel = null;
            fmot[i].label = Leaftion.this.imagePanel.getLabels().get(i);
            fmot[i].motions = Leaftion.this.motionDetectionResults.get(i);
          }

          optimizePanel.setMotionEstimation(fmot);
          optFrame.add(optimizePanel);
          optFrame.setVisible(true);
          optFrame.setPreferredSize(new Dimension(1024, 768));
          optFrame.setSize(new Dimension(1024, 768));
          optFrame.setLocationRelativeTo(null);
          optFrame.setTitle("Optimize");
          optFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
          optFrame.pack();

        }
      });

    }
  }
  
  class CroppingProccessFrameListener implements ProcessingFramesListener {
    int loggingSlot ;
    @Override
    public void updateFrame(ProcessingFramesEvent e) {
      LogItem msg = new LogItem(
          "Crop Image (" + e.currentFrame + "/" + e.numberOfFrames + ")",
          LoggingListPanel.NORMAL_TYPE);
      Leaftion.this.log.updateAllocatedSlot(msg, this.loggingSlot);

    }
    @Override
    public void startProccess(ProcessingFramesEvent e) {
      Leaftion.this.log.addVolatileMessage(new LogItem("Start Cropping Images",
          LoggingListPanel.NORMAL_TYPE));
      this.loggingSlot = Leaftion.this.log.allocateUpdatableSlot("Crop Image (0/"+e.numberOfFrames+")");
    }
    @Override
    public void finnishProccess(ProcessingFramesEvent e) {
      Leaftion.this.log.addVolatileMessage(
          new LogItem("Cropping Done", LoggingListPanel.NORMAL_TYPE));
    }
  }
  
  class BrightProccessFrameListener implements ProcessingFramesListener {
    int loggingSlot = -1; 
    @Override
    public void updateFrame(ProcessingFramesEvent e) {
      LogItem msg = new LogItem("Making Bright Image (" + e.currentFrame
          + "/" + e.numberOfFrames + ")", LoggingListPanel.NORMAL_TYPE);

      System.out.println(this.loggingSlot);
      if (this.loggingSlot >= 0 ) {
        Leaftion.this.log.updateAllocatedSlot(msg, this.loggingSlot);
      } 
    }
    @Override
    public void startProccess(ProcessingFramesEvent e) {
      LogItem msg = new LogItem("Start Bright Image",
          LoggingListPanel.NORMAL_TYPE);
      Leaftion.this.log.addVolatileMessage(msg);
      this.loggingSlot = Leaftion.this.log.allocateUpdatableSlot("Making Bright Image (0"+ e.numberOfFrames + ")");
    }
    @Override
    public void finnishProccess(ProcessingFramesEvent e) {
      LogItem msg = new LogItem("Bright Image Done",
          LoggingListPanel.NORMAL_TYPE);
      Leaftion.this.log.addVolatileMessage(msg);
    }
  }
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  //
  class ExportButtonActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {

      List<Rectangle> currentRegions = Leaftion.this.imagePanel.getRegions();
      
      if ( currentRegions.isEmpty() )  return ;
      
      JFileChooser fc = new JFileChooser();
      
      fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      fc.setDialogTitle("Export created regios");
      fc.addChoosableFileFilter( new CsvFileFilter() );
      fc.setMultiSelectionEnabled(false);
      
      fc.showSaveDialog(Leaftion.this);
      
      File selectedFile = fc.getSelectedFile();
      
      if (selectedFile != null) {
        
        try {
          
          PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(selectedFile)));
          List<String> labels = Leaftion.this.imagePanel.getLabels();

          for (int i = 0 ; i < currentRegions.size(); i++) {
            
            Rectangle reg = currentRegions.get(i);
            String lab = labels.get(i);
            
            String outline = String.join(";", lab, 
                String.valueOf( (int) reg.getX() ) ,
                String.valueOf( (int) reg.getY() ) ,
                String.valueOf( (int) reg.getWidth() ) ,
                String.valueOf( (int) reg.getHeight() )
                );
            
            ps.println(outline);
            
          }
          
          ps.close();
          
        } catch (FileNotFoundException e1) {
          e1.printStackTrace();
        }
      }
    }
  }
  
  class LoadRegionsButtonActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {

      JFileChooser fc = new JFileChooser();
      
      fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      fc.setDialogTitle("Export created regios");
      fc.addChoosableFileFilter( new CsvFileFilter() );
      fc.setMultiSelectionEnabled(false);
      
      fc.showSaveDialog(Leaftion.this);
      
      File selectedFile = fc.getSelectedFile();
      
      if (selectedFile != null) {
        
        try {
          
          List<Rectangle> regions = new ArrayList<>();
          List<String> labels = new ArrayList<>();
          
          BufferedReader br = new BufferedReader(new FileReader(selectedFile));
          
          String currentLine = null;
          
          while( (currentLine=br.readLine()) != null) {
            System.out.println(currentLine);
            String[] fields = currentLine.split(";");
            if ( fields.length ==5 ){
              int x = Integer.parseInt(fields[1].trim());
              int y = Integer.parseInt(fields[2].trim());
              int w = Integer.parseInt(fields[3].trim());
              int h = Integer.parseInt(fields[4].trim());
              Rectangle r = new Rectangle(x,y,w,h );
              regions.add(r);
              labels.add(fields[0]);
            }
          }
          System.out.println(regions);
          System.out.println(labels);
          
          Leaftion.this.imagePanel.setRegionsAndLabels( regions, labels );
          System.out.println("Regions Updated");
          
          br.close();
          
        } catch (IOException  | NumberFormatException e2) {
          e2.printStackTrace();
        } 
          
      }
    }
  }

  
  // ------------------------------------------------------------------------ //
}
