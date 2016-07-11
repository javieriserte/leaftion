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
import java.awt.image.BufferedImage;
import java.beans.FeatureDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import org.jiserte.leaftion.events.ProcessingFramesEvent;
import org.jiserte.leaftion.events.ProcessingFramesListener;
import org.jiserte.leaftion.image.ImageLibrary;
import org.jiserte.leaftion.math.MotionEstimator;
import org.jiserte.leaftion.math.Motions;
import org.jiserte.multiselectimagepanel.MultiSelectImagePanel;

public class Leaftion extends JFrame {

  // ------------------------------------------------------------------------ //
  // Class Constants
  private static final long serialVersionUID = -1917154020995579885L;
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  // Instance variables
  private File imageFolder = null;
  private ImageLibrary library;
  private String[] logContent;
  private int logContentIndex;
  private Locale locale;
  private List<Motions> motionDetectionResults;
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  // Components
  private JLabel currentFolderLabel;
  private MultiSelectImagePanel imagePanel;
  private JList<String> log;
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

  // ------------------------------------------------------------------------ //

  public static void main(String[] args) {

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          // ---------------------------------------------------------------- //
          // creates the main instance
          Leaftion pt = new Leaftion();
          pt.setVisible(true);
          pt.setPreferredSize(new Dimension(1024, 768));
          pt.setSize(new Dimension(1024, 768));
          pt.setLocationRelativeTo(null);
          pt.setTitle("Leaftion");
          pt.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
    Dimension sepDimension = new Dimension(15, 10);
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Define el label para mostrar el nombre de la carpeta
    this.currentFolderLabel = new JLabel("Ninguna carpeta elegida");
    currentFolderLabel.setMinimumSize(new Dimension(150, 50));
    currentFolderLabel.setPreferredSize(new Dimension(250, 50));
    currentFolderLabel.setMaximumSize(new Dimension(250, 50));
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Define el botón para buscar la carpeta de imágenes
    JButton button = new JButton(UIManager.getIcon("FileView.directoryIcon"));
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.showOpenDialog(Leaftion.this);
        File selectedFile = fc.getSelectedFile();
        if (selectedFile != null) {
          boolean foundImages = Leaftion.this.setImageFolder(selectedFile);
          if (foundImages) {
            Leaftion.this.addToLog("Se han econtrado "
                + Leaftion.this.library.getImageFiles().length + " Imágenes!");
            Leaftion.this.buttonBright.setEnabled(true);
            Leaftion.this.preButton.setEnabled(true);
            Leaftion.this.nextButton.setEnabled(true);
            Leaftion.this.runButton.setEnabled(true);
          } else {
            Leaftion.this.addToLog("Error: No se han econtrado imágenes!");
          }
        }
      }
    });
    button.setToolTipText("Define la carpeta con las imágenes");
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Define el botón para mostrar las imágenes con el efecto brillante
    this.buttonBright = new JButton("Brillante");

    buttonBright.setEnabled(false);

    buttonBright.addActionListener(new ActionListener() {
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
    });
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Define botones para buscar entre imágenes
    this.preButton = new JButton("<<");
    this.preButton.setEnabled(false);
    this.preButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        BufferedImage im = Leaftion.this.library.previous();
        if (im != null) {
          Leaftion.this.imagePanel.setImage(im);
          String counter = String
              .valueOf(Leaftion.this.library.getImageIndex());
          Leaftion.this.counterLabel.setText(counter);
        }
      }
    });
    this.nextButton = new JButton(">>");
    this.nextButton.setEnabled(false);
    this.nextButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        BufferedImage im = Leaftion.this.library.next();
        if (im != null) {
          Leaftion.this.imagePanel.setImage(im);
          String counter = String
              .valueOf(Leaftion.this.library.getImageIndex());
          Leaftion.this.counterLabel.setText(counter);
        }
      }
    });
    this.counterLabel = new JLabel("0");
    counterLabel.setMinimumSize(new Dimension(30, 50));
    counterLabel.setPreferredSize(new Dimension(50, 50));
    counterLabel.setMaximumSize(new Dimension(50, 50));
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Define el botón para iniciar la ejecución
    this.runButton = new JButton("Run!");
    this.runButton.setEnabled(false);

    runButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        MotionEstimator mest = new MotionEstimator();

        List<Rectangle> regions = Leaftion.this.imagePanel.getRegions();
        List<String> labels = Leaftion.this.imagePanel.getLabels();

        Thread t = new Thread(new Runnable() {

          @Override
          public void run() {
            List<List<BufferedImage>> imagesRegions = Leaftion.this.library
                .cropRegions(regions);

            if (imagesRegions.size() == 0) {
              Leaftion.this.addToLog("Error: No regions defined!");
              return;
            }

            List<Motions> motionList = new ArrayList<>();

            Leaftion.this.addToLog("Estimating motions");
            for (List<BufferedImage> images : imagesRegions) {
              Motions motions = mest.estimateMotionInSeries(images);
              motionList.add(motions);
            }
            Leaftion.this.modifyLastLog("Estimating motions Done");

            Leaftion.this.motionDetectionResults = motionList;

             String directory = null;
             String file = null;
            
             while (true) {
            
             FileDialog out = new FileDialog(Leaftion.this,
             "Elija archivo de salida", FileDialog.SAVE);
             out.setVisible(true);
            
             directory = out.getDirectory();
             file = out.getFile();
            
             if (directory == null || file == null) {
             Leaftion.this
             .addToLog("Error: File not found or save cancelled");
            
             int input = JOptionPane.showConfirmDialog(Leaftion.this,
             "No ha seleccionado un archivo para guardar los resultados.\nSi no lo hace los datos se perderán.",
             "Seleccione una opción",
             JOptionPane.OK_CANCEL_OPTION,
             JOptionPane.WARNING_MESSAGE);
            
             if (input == JOptionPane.CANCEL_OPTION) {
             return;
             }
            
             } else {
             break;
             }
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
            
             for (int i = 0; i < motionList.get(0).getV_motion().length; i++)
             {
            
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
             os.close();
            
             Leaftion.this.addToLog(
             "Results Saved on file: " +
             outfile.getAbsolutePath() );
            
             } catch (FileNotFoundException e1) {
             e1.printStackTrace();
             }
          }
        });

        t.start();

      }
    });
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Selecciona el Locale
    this.localeLabel = new JLabel("Locale:");
    this.localeLabel
        .setToolTipText("Elija el idioma para guardar los resultados");
    this.localeComboBox = new JComboBox<>(new String[] { "es_AR", "en_US" });
    this.localeComboBox.setPreferredSize(new Dimension(80, 25));
    this.localeComboBox.setMaximumSize(new Dimension(80, 25));

    this.localeComboBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          Leaftion.this.setLocale((String) e.getItem());
        }

      }
    });

    // ---------------------------------------------------------------------- //

    this.optimButton = new JButton("Opt!");

    this.optimButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

//        if (Leaftion.this.motionDetectionResults.isEmpty()) {
//          
//          JOptionPane.showMessageDialog(Leaftion.this,
//              "No hay resultados de estimación de movimiento", "Error", 
//              JOptionPane.ERROR_MESSAGE);
//          return;
//        }

        
        
        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {

            JFrame optFrame = new JFrame();
            OptimizePanel optimizePanel = new OptimizePanel();
            FittedMotions[] fmot = new FittedMotions[
              Leaftion.this.motionDetectionResults.size()];
            
            optimizePanel.setInterval(
                Double.parseDouble(intervalTxt.getText()));
            
            for (int i = 0; i< fmot.length; i++) {
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
    });

    this.intervalTxt = new TextField();
    this.intervalTxt.setEditable(true);
    this.intervalTxt.setText("1.0");
    this.intervalTxt.addTextListener(new TextListener() {

      @Override
      public void textValueChanged(TextEvent e) {

        String text = intervalTxt.getText();
        if (text.trim().length() > 0) {
          try {
            Double.parseDouble(text);
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Ingrese un número");
            intervalTxt.setText("1.0");
          }
        }
      }
    });
    this.intervalTxt.setPreferredSize(new Dimension(40, 20));
    this.intervalTxt.setMaximumSize(new Dimension(40, 20));

    // ---------------------------------------------------------------------- //
    // Define la toolbar
    JToolBar tb = new JToolBar();

    tb.addSeparator(new Dimension(10, 0));
    tb.add(button);
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
    tb.addSeparator(sepDimension);
    tb.add(this.localeLabel);
    tb.add(this.localeComboBox);
    tb.addSeparator(sepDimension);
    tb.add(new JLabel("Intervalo (hs):"));
    tb.add(this.intervalTxt);
    tb.addSeparator(sepDimension);
    tb.add(this.optimButton);

    this.localeComboBox.setSelectedIndex(0);
    this.setLocale(this.localeComboBox.getItemAt(0));

    tb.setRollover(true);
    tb.setFloatable(false);
    tb.setPreferredSize(new Dimension(1, 35));
    tb.setMargin(new Insets(2, 2, 2, 2));
    this.add(tb, BorderLayout.PAGE_START);
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    this.imagePanel = new MultiSelectImagePanel();
    this.add(new JScrollPane(this.imagePanel), BorderLayout.CENTER);

    this.imagePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke("control LEFT"), "previousFrame");
    this.imagePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke("control RIGHT"), "nextFrame");

    AbstractAction preFrameAction = new AbstractAction() {
      private static final long serialVersionUID = -8050051488649477806L;

      @Override
      public void actionPerformed(ActionEvent e) {
        ImageLibrary library = Leaftion.this.library;
        if (library != null) {
          BufferedImage im = library.previous();
          if (im != null) {
            Leaftion.this.imagePanel.setImage(im);
            String counter = String.valueOf(library.getImageIndex());
            Leaftion.this.counterLabel.setText(counter);
          }
        }
      }
    };

    AbstractAction nextFrameAction = new AbstractAction() {
      private static final long serialVersionUID = 559853393227529303L;

      @Override
      public void actionPerformed(ActionEvent e) {
        ImageLibrary library = Leaftion.this.library;
        if (library != null) {
          BufferedImage im = Leaftion.this.library.next();
          if (im != null) {
            Leaftion.this.imagePanel.setImage(im);
            String counter = String
                .valueOf(Leaftion.this.library.getImageIndex());
            Leaftion.this.counterLabel.setText(counter);
          }
        }
      }
    };

    preFrameAction.setEnabled(true);
    this.imagePanel.getActionMap().put("previousFrame", preFrameAction);
    this.imagePanel.getActionMap().put("nextFrame", nextFrameAction);

    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Log
    this.log = new JList<>();
    this.logContent = new String[100];
    this.logScrollPane = new JScrollPane(this.log);
    this.addToLog("Welcome!");
    this.add(this.logScrollPane, BorderLayout.SOUTH);
    // ---------------------------------------------------------------------- //

  }

  private void addToLog(String value) {

    if (this.logContentIndex >= this.logContent.length) {
      this.logContentIndex = this.logContent.length - 1;
      for (int i = 1; i < this.logContent.length; i++) {
        this.logContent[i - 1] = this.logContent[i];
      }
    }

    this.logContent[this.logContentIndex] = "> " + value;
    this.log.setListData(
        Arrays.copyOfRange(this.logContent, 0, this.logContentIndex + 1));
    this.logContentIndex++;
    this.validate();
    JScrollBar vertical = this.logScrollPane.getVerticalScrollBar();
    vertical.setValue(vertical.getMaximum() + 1);

  }

  private void modifyLastLog(String value) {

    this.logContent[this.logContentIndex - 1] = "> " + value;
    this.log.setListData(
        Arrays.copyOfRange(this.logContent, 0, this.logContentIndex));
    this.validate();
    JScrollBar vertical = this.logScrollPane.getVerticalScrollBar();
    vertical.setValue(vertical.getMaximum() + 1);

  }

  private boolean setImageFolder(File selectedFile) {

    this.imageFolder = selectedFile;
    this.currentFolderLabel.setText(this.imageFolder.getAbsolutePath());
    this.library = new ImageLibrary();
    this.library
        .addCropImageProccessingListener(new ProcessingFramesListener() {

          @Override
          public void updateFrame(ProcessingFramesEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                if (e.currentFrame == 1) {
                  Leaftion.this.addToLog("Crop Image (" + e.currentFrame + "/"
                      + e.numberOfFrames + ")");
                } else {
                  Leaftion.this.modifyLastLog("Crop Image (" + e.currentFrame
                      + "/" + e.numberOfFrames + ")");
                }
              }
            });
          }

          @Override
          public void startProccess(ProcessingFramesEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                Leaftion.this.addToLog("Start Cropping Images");
              }
            });
          }

          @Override
          public void finnishProccess(ProcessingFramesEvent e) {
            // SwingUtilities.invokeLater(new Runnable() {
            // @Override
            // public void run() {
            Leaftion.this.addToLog("Cropping Done");
            // }
            // });
          }
        });

    this.library
        .addBrightImageProccesingListener(new ProcessingFramesListener() {
          @Override
          public void updateFrame(ProcessingFramesEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                if (e.currentFrame == 1) {
                  Leaftion.this.addToLog("Making Bright Image ("
                      + e.currentFrame + "/" + e.numberOfFrames + ")");
                } else {
                  Leaftion.this.modifyLastLog("Making Bright Image ("
                      + e.currentFrame + "/" + e.numberOfFrames + ")");
                }
              }
            });
          }

          @Override
          public void startProccess(ProcessingFramesEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                Leaftion.this.addToLog("Start Bright Image");
              }
            });
          }

          @Override
          public void finnishProccess(ProcessingFramesEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                Leaftion.this.addToLog("Bright Image Done");
              }
            });
          }
        });

    this.library.getImageFilesInFolder(this.imageFolder);

    BufferedImage im = this.library.getCurrentImage();

    if (im != null) {
      this.imagePanel.setImage(im);
      this.counterLabel.setText("1");
      this.counterLabel.setToolTipText(
          "Control+Izq y Control+Der para ir a la imagen siguiente o previa.");
      return true;
    }

    return false;

  }

  public Locale getLocale() {
    return locale;
  }

  protected void setLocale(String item) {
    String[] a = item.split("_");
    this.locale = new Locale(a[0], a[1]);
  }

}
