package org.jiserte.leaftion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jiserte.leaftion.logpanel.LogItem;
import org.jiserte.leaftion.logpanel.LoggingListPanel;
import org.jiserte.leaftion.math.CosineFitResult;
import org.jiserte.leaftion.math.CosineModel;
import org.jiserte.leaftion.math.ModelEvaluator;

public class OptimizePanel extends JPanel {

  /**
   * +
   */
  private static final long serialVersionUID = -3642747013037883477L;

  @SuppressWarnings("unused")
  private FittedMotions[] motions;
  private JList<FittedMotions> motionsList;
  private JList<String> groupAvgList;
  private MotionPlotPanel plotPanel;
  private double interval;

  private int replicates;
  private int iterations;

  private JScrollBar startScrollBar;
  private LoggingListPanel log;
  private JLabel startFrameInd;
  private JLabel endFrameInd;
  private JScrollBar endScrollBar;
  private JTextField repTxt;
  private JTextField iterTxt;

  public OptimizePanel() {
    super();

    this.interval = 1;
    
    this.iterations = 10000;
    
    this.replicates = 5;

    this.createGUI();
  }

  public double getInterval() {
    return interval;
  }

  public void setInterval(double interval) {
    this.interval = interval;
  }

  private void createGUI() {

    JPanel listPanel = new JPanel();

    GridBagLayout lpLayout = new GridBagLayout();
    GridBagConstraints lpc = new GridBagConstraints();

    listPanel.setLayout(lpLayout);

    lpLayout.columnWeights = new double[] { 1, 1, 1 };
    lpLayout.columnWidths = new int[] { 100, 100, 100 };
    lpLayout.rowWeights = new double[] { 0, 0.5, 0, 0, 0.5 };
    lpLayout.rowHeights = new int[] { 20, 100, 20, 20, 100 };
    lpc.insets = new Insets(4, 4, 4, 4);

    lpc.gridx = 0;
    lpc.gridy = 0;
    lpc.fill = GridBagConstraints.BOTH;
    listPanel.add(new JLabel("Samples"), lpc);

    JButton clearGroupsBtn = new JButton("Clear groups");
    lpc.gridx = 0;
    lpc.gridy = 2;
    listPanel.add(clearGroupsBtn, lpc);

    GroupPanelButtonsActionListener grpBtnListener = new GroupPanelButtonsActionListener();
    
    clearGroupsBtn.addActionListener(grpBtnListener);
    clearGroupsBtn.setActionCommand("ADD");

    JButton setGroupBtn = new JButton("Make group");
    lpc.gridx = 1;
    lpc.gridy = 2;
    lpc.fill = GridBagConstraints.BOTH;
    listPanel.add(setGroupBtn, lpc);

    setGroupBtn.addActionListener(grpBtnListener);
    setGroupBtn.setActionCommand("SET");

    JButton calcGroupsBtn = new JButton("Group avg.");
    lpc.gridx = 2;
    lpc.gridy = 2;
    listPanel.add(calcGroupsBtn, lpc);
    calcGroupsBtn.addActionListener(grpBtnListener);
    calcGroupsBtn.setActionCommand("AVG");

    lpc.gridx = 0;
    lpc.gridy = 3;
    listPanel.add(new JLabel("Groups"), lpc);

    lpc.gridx = 0;
    lpc.gridy = 4;
    lpc.gridwidth = 3;
    this.groupAvgList = new JList<>();
    this.groupAvgList.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    listPanel.add(this.groupAvgList, lpc);

    this.motionsList = new JList<>();
    this.motionsList.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

//    this.motionsList.setMinimumSize(new Dimension(150, 100));
//    this.motionsList.setPreferredSize(new Dimension(150, 100));

    this.motionsList.setCellRenderer(new MotionListCellRenderer());

    this.motionsList.addListSelectionListener( new GroupListSelectionListener() );

    lpc.gridx = 0;
    lpc.gridy = 1;
    lpc.gridwidth = 3;
    JScrollPane jScrollPane = new JScrollPane(this.motionsList);
    jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    listPanel.add(jScrollPane, lpc);

    this.plotPanel = new MotionPlotPanel();

    this.setLayout(new BorderLayout());

    JPanel optionsPanel = new JPanel();

    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();

    optionsPanel.setLayout(layout);

    this.startScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 1);
    this.endScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 1);

    TimeScrollAdjustmenteListener startTimeListener = new TimeScrollAdjustmenteListener();
    startTimeListener.isStart=true;
    this.startScrollBar.addAdjustmentListener(startTimeListener);

    TimeScrollAdjustmenteListener endTimeListener = new TimeScrollAdjustmenteListener();
    endTimeListener.isStart=false;
    this.endScrollBar.addAdjustmentListener(endTimeListener);

    JButton optimButton = new JButton("Fit data");
    optimButton.addActionListener(new FitButtonActionListener());

    JButton saveButton = new JButton("Save");
    saveButton.addActionListener(new SaveDataActionListener());

    layout.columnWidths = new int[] { 50, 100, 30, 50, 50, 50, 50 };
    layout.rowHeights = new int[] { 20, 20, 100 };
    layout.columnWeights = new double[] { 0, 1, 0, 0, 0, 0, 0 };
    layout.rowWeights = new double[] { 0, 0, 1 };

    c.insets = new Insets(5, 5, 5, 5);
    c.fill = GridBagConstraints.BOTH;

    c.gridx = 0;
    c.gridy = 0;
    optionsPanel.add(new JLabel("Selection start:"), c);

    c.gridx = 0;
    c.gridy = 1;
    optionsPanel.add(new JLabel("Selection end:"), c);

    c.gridx = 1;
    c.gridy = 0;
    optionsPanel.add(startScrollBar, c);

    c.gridy = 1;
    optionsPanel.add(endScrollBar, c);

    this.startFrameInd = new JLabel();
    c.gridy = 0;
    c.gridx = 2;
    optionsPanel.add(this.startFrameInd, c);

    this.endFrameInd = new JLabel();
    c.gridy = 1;
    c.gridx = 2;
    optionsPanel.add(this.endFrameInd, c);

    c.gridy = 0;
    c.gridx = 3;
    c.gridheight = 1;
    optionsPanel.add(new JLabel("Replicates:"), c);

    c.gridy = 1;
    c.gridx = 3;
    c.gridheight = 1;
    optionsPanel.add(new JLabel("Iterations"), c);

    this.repTxt = new JTextField("5");
    this.repTxt.setInputVerifier(new ReplicatesInputVerifier());
    c.gridy = 0;
    c.gridx = 4;
    c.gridheight = 1;
    optionsPanel.add(this.repTxt, c);

    this.iterTxt = new JTextField("10000");
    this.iterTxt.setInputVerifier(new IterationsInputVerifier());
    c.gridy = 1;
    c.gridx = 4;
    c.gridheight = 1;
    optionsPanel.add(this.iterTxt, c);

    c.gridy = 0;
    c.gridx = 5;
    c.gridheight = 2;
    optionsPanel.add(optimButton, c);

    c.gridy = 0;
    c.gridx = 6;
    c.gridheight = 2;
    optionsPanel.add(saveButton, c);

    c.gridy = 2;
    c.gridx = 0;
    c.gridheight = 1;
    c.gridwidth = 7;

    this.log = new LoggingListPanel(30);

    optionsPanel.add(new JScrollPane(this.log), c);

    JSplitPane jSplitPane = new JSplitPane();
    jSplitPane.setLeftComponent(listPanel);
    jSplitPane.setRightComponent(this.plotPanel);

    this.add(jSplitPane, BorderLayout.CENTER);

    this.add(optionsPanel, BorderLayout.SOUTH);

  }





  protected void groupSelected() {

    int maxGroupIndex = 0;

    for (int i = 0; i < this.motionsList.getModel().getSize(); i++) {
      if (!this.motionsList.isSelectedIndex(i)) {
        maxGroupIndex = Math.max(maxGroupIndex, 
            this.motionsList.getModel().getElementAt(i).group);
      }
    }
    maxGroupIndex++;

    for (int i = 0; i < this.motionsList.getModel().getSize(); i++) {
      if (this.motionsList.isSelectedIndex(i)) {
        this.motionsList.getModel().getElementAt(i).group = maxGroupIndex;
      }
    }

    this.motionsList.updateUI();
  }

  protected void clearGroups() {

    for (int i = 0; i < this.motionsList.getModel().getSize(); i++) {

      this.motionsList.getModel().getElementAt(i).group = 1;

    }
    this.motionsList.updateUI();

  }

  public void setMotionEstimation(FittedMotions[] motions) {
    this.motions = motions;
    if (motions.length > 0) {
      this.motionsList.setListData(motions);
      this.clearGroups();
    }
  }

  public void showGroupAverages() {

    Map<Integer, List<Double>> periodMeansByGroup = new HashMap<>();
    Map<Integer, List<Double>> phaseMeansByGroup = new HashMap<>();

    for (int i = 0; i < this.motionsList.getModel().getSize(); i++) {

      FittedMotions current = this.motionsList.getModel().getElementAt(i);

      CosineFitResult fittedModel = current.fittedModel;
      
      if (fittedModel != null) {
        
        double cPeriod = fittedModel.period;
        double cPhase = fittedModel.phase;
        int cGroup = current.group;
  
        if (!periodMeansByGroup.keySet().contains(cGroup)) {
          periodMeansByGroup.put(cGroup, new ArrayList<>());
          phaseMeansByGroup.put(cGroup, new ArrayList<>());
        }
        periodMeansByGroup.get(cGroup).add(cPeriod);
        phaseMeansByGroup.get(cGroup).add(cPhase);
      }

    }

    List<Integer> groups = new ArrayList<>();
    groups.addAll(periodMeansByGroup.keySet());
    Collections.sort(groups);

    String[] groupMeansData = new String[groups.size()];

    for (int i = 0; i < groups.size(); i++) {

      Integer cgroup = groups.get(i);

      List<Double> cpers = periodMeansByGroup.get(cgroup);
      List<Double> cphas = phaseMeansByGroup.get(cgroup);
      double sumPer = 0;
      double sumPha = 0;
      double sdPer = 0;
      double sdPha = 0;
      for (double d : cpers) {
        sumPer += d;
      }
      for (double d : cphas) {
        sumPha += d;
      }
      sumPer = sumPer / cpers.size();
      sumPha = sumPha / cphas.size();
      for (double d : cpers) {
        sdPer += Math.pow(sumPer - d, 2);
      }
      for (double d : cphas) {
        sdPha += Math.pow(sumPha - d, 2);
      }
      sdPer = Math.sqrt(sdPer) / cpers.size();
      sdPha = Math.sqrt(sdPha) / cpers.size();

      groupMeansData[i] = String.format(
          "Gr: %d Per: %5.2f (%5.2f) Pha: %5.2f (%5.2f)", 
          cgroup, sumPer, sdPer, sumPha, sdPha);
    }

    OptimizePanel.this.groupAvgList.setFont(new Font("Verdana", 1, 9));
    OptimizePanel.this.groupAvgList.setListData(groupMeansData);
    OptimizePanel.this.groupAvgList.updateUI();

  }

  private void prepareAndShowMotionPlotPanel() {
    
    List<FittedMotions> motions = OptimizePanel.this.motionsList.getSelectedValuesList();
    
    if ( motions.isEmpty() ) {
      return;
    }
    
    OptimizePanel.this.plotPanel.fittedMotions = motions ;
    
    for (FittedMotions motion : motions) {
     motion.interval = this.interval; 
    }
    
    


    if (OptimizePanel.this.startScrollBar != null) {
//      OptimizePanel.this.startScrollBar.setValue(0);
      OptimizePanel.this.startScrollBar.setMaximum(
          motions.get(0).motions.getV_motion().length);
    }
    if (OptimizePanel.this.endScrollBar != null) {
//      OptimizePanel.this.endScrollBar.setValue(0);
      OptimizePanel.this.endScrollBar.setMaximum(
          motions.get(0).motions.getV_motion().length);
    }
    

    OptimizePanel.this.plotPanel.updateUI();
  }

  public class MotionListCellRenderer implements ListCellRenderer<FittedMotions> {
    @Override
    public Component getListCellRendererComponent(JList<? extends FittedMotions> list, FittedMotions value, int index,
        boolean isSelected, boolean cellHasFocus) {

      JLabel regionLabel = new JLabel(value.label);

      regionLabel.setFont(new Font("Verdana", Font.BOLD, 12));

      GridBagLayout mgr = new GridBagLayout();

      JPanel p = new JPanel();
      p.setLayout(mgr);

      mgr.columnWidths = new int[] { 100, 100 };
      mgr.columnWeights = new double[] { 1, 1 };
      mgr.rowHeights = new int[] { 20, 20, 20 };
      mgr.rowWeights = new double[] { 1, 1, 1 };

      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.CENTER;
      c.fill = GridBagConstraints.BOTH;

      c.gridx = 0;
      c.gridy = 0;
      c.gridwidth = 2;
      p.add(regionLabel, c);

      String per = "Period: ";
      String phase = "Phase: ";
      String medianPer = "Med Per: ";
      String medianPha = "Med Pha: ";

      if (value.fittedModel == null) {
        per = per + "-";
        phase = phase + "-";
        medianPer = medianPer + "-";
        medianPha = medianPha + "-";
      } else {
        per = per + String.format( "%5.2f(%3.2f)", value.fittedModel.period, 
            value.fittedModel.stdPeriod );
        phase = phase + String.format( "%5.2f(%3.2f)", value.fittedModel.phase, 
            value.fittedModel.stdPhase );
        medianPer = medianPer + String.format( "%5.2f", 
            value.fittedModel.medianPeriod );
        medianPha = medianPha + String.format( "%5.2f", 
            value.fittedModel.medianPhase );
      }

      JLabel perLabel = new JLabel(per);
      JLabel phaseLabel = new JLabel(phase);
      JLabel medPerLabel = new JLabel(medianPer);
      JLabel medPhaseLabel = new JLabel(medianPha);

      Font font = new Font("Verdana", 0, 10);

      perLabel.setFont(font);
      c.gridx = 0;
      c.gridy = 1;
      c.gridwidth = 1;
      p.add(perLabel, c);

      c.gridx = 1;
      c.gridy = 1;
      phaseLabel.setFont(font);
      p.add(phaseLabel, c);

      
      c.gridx = 0;
      c.gridy = 2;
      c.gridwidth = 1;
      medPerLabel.setFont(font);
      p.add(medPerLabel, c);

      c.gridx = 1;
      c.gridy = 2;
      medPerLabel.setFont(font);
      p.add(medPhaseLabel, c);
      
      c.gridx = 1;
      c.gridy = 0;
      p.add(new JLabel("Group:" + value.group), c);
      

      p.setBackground(isSelected ? new Color(0, 150, 240) : Color.white);
      return p;
    }
  }

  class GroupPanelButtonsActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      switch(e.getActionCommand()) {
      case "ADD":
        OptimizePanel.this.clearGroups();
        break;
      case "SET":
        OptimizePanel.this.groupSelected();
        break;
      case "AVG":
        OptimizePanel.this.showGroupAverages();
        break;
      default:
        break;
      }
    }
  }
  
  class GroupListSelectionListener implements ListSelectionListener {
    @Override
    public void valueChanged(ListSelectionEvent e) {
      OptimizePanel.this.prepareAndShowMotionPlotPanel();
    }
  }
  
  class TimeScrollAdjustmenteListener implements AdjustmentListener {
    public boolean isStart;

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
      int start= 0; 
      int end= 0;      
      if (this.isStart) {
        start = e.getValue();
        end = Math.max(start, OptimizePanel.this.endScrollBar.getValue());
      } else {
        end = e.getValue();
        start = Math.min(end, OptimizePanel.this.startScrollBar.getValue());
      }
      
      OptimizePanel.this.endScrollBar.setValue(end);
      OptimizePanel.this.plotPanel.startSelectIndex = start;
      OptimizePanel.this.plotPanel.endSelectIndex = end;

      OptimizePanel.this.startFrameInd.setText(String.valueOf(start));
      OptimizePanel.this.endFrameInd.setText(String.valueOf(end));

      OptimizePanel.this.startFrameInd.updateUI();
      OptimizePanel.this.endFrameInd.updateUI();
      OptimizePanel.this.plotPanel.updateUI();

    }
  }
  
  class FitButtonActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {

      int[] selIdx = OptimizePanel.this.motionsList.getSelectedIndices();
      
      int replicates = OptimizePanel.this.replicates;
      int iterations = OptimizePanel.this.iterations;
      
      SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

        @Override
        protected Void doInBackground() throws Exception {
          for (int i : selIdx) {

            FittedMotions fm = motionsList.getModel().getElementAt(i);

            double[] y = fm.motions.getV_motion();
            double[] x = getTimeInteravals(y);
            
            int from = OptimizePanel.this.startScrollBar.getValue();
            int to = OptimizePanel.this.endScrollBar.getValue();
            
            if (from == to) {
              from = 0;
              to = y.length -1;
            }

            List<CosineModel> models = new ArrayList<>();

            ModelEvaluator me = new ModelEvaluator(iterations, x, y, from, to);

            LogItem msg = new LogItem(
                "Start fitting " + fm.label ,
                LoggingListPanel.NORMAL_TYPE);
            OptimizePanel.this.log.addVolatileMessage(msg);
            
            int loggingSlot = OptimizePanel.this.log.allocateUpdatableSlot("Fitting...");
            
            for (int j = 0; j < replicates; j++) {
              msg = new LogItem(
                  "Fitting " + fm.label + " Replicate: " + (j+1),
                  LoggingListPanel.NORMAL_TYPE);
              OptimizePanel.this.log.updateAllocatedSlot(msg, loggingSlot);
              CosineModel cm = me.optimize();
              models.add(cm);
            }
            
            msg = new LogItem(
                "Finnish fitting " + fm.label ,
                LoggingListPanel.NORMAL_TYPE);
            OptimizePanel.this.log.addVolatileMessage(msg);

            // ------------------------------------------------------------------ //
            // Build Fitted results
            motionsList.getModel().getElementAt(i).fittedModel = 
                new CosineFitResult(models, iterations);
            // ------------------------------------------------------------------ //

            // ------------------------------------------------------------------ //
            // update UI
            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                motionsList.updateUI();
              }
            });
            OptimizePanel.this.prepareAndShowMotionPlotPanel();
            // ------------------------------------------------------------------ //

          }
          return null;
        }
      }; 
      
      worker.execute();
  

    }

    private double[] getTimeInteravals(double[] y) {
      double[] x = new double[y.length];
      for (int j = 0; j < x.length; j++) {
        x[j] = j * OptimizePanel.this.interval;
      }
      return x;
    }
  }
  
  class PositiveNumberValueVerifier extends InputVerifier {
    BigDecimal value;
    @Override
    public boolean verify(JComponent component) {
      String input = ((JTextField)component).getText();

      try {
        this.value = new BigDecimal(input);
        boolean result = this.value!=null && this.value.intValue() > 0;

        return result; 
      } catch (NumberFormatException e) {
        return false;
      }
    }
  }
  
  class ReplicatesInputVerifier extends PositiveNumberValueVerifier {
    @Override
    public boolean verify(JComponent component) {
      boolean result = super.verify(component);
      
      if (result) {
        OptimizePanel.this.replicates = this.value.intValue();
        component.setBackground(new Color(255,255,255));
      } else {
        component.setBackground(new Color(255,235,235));
      }
      
      return result;
    }
  }
  
  class IterationsInputVerifier extends PositiveNumberValueVerifier {
    @Override
    public boolean verify(JComponent component) {
      boolean result = super.verify(component);
      
      if (result) {
        OptimizePanel.this.iterations = this.value.intValue();
        component.setBackground(new Color(255,255,255));
      } else {
        component.setBackground(new Color(255,235,235));
      }
      
      return result;
    }
  }
  
  class SaveDataActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent event) {

      int fittedCounter = 0;
      ListModel<FittedMotions> motionsListModel = OptimizePanel.this.motionsList.getModel();
      for ( int i = 0; i< motionsListModel.getSize(); i++ ) {
        fittedCounter += motionsListModel.getElementAt(i).fittedModel != null ? 1: 0;
      }
      
      if (fittedCounter > 0) {
        
        JFileChooser fc = new JFileChooser();
        fc.showSaveDialog(OptimizePanel.this);
        File saveFile = fc.getSelectedFile();

        try {
          PrintStream ps = new PrintStream(
              new BufferedOutputStream( 
                  new FileOutputStream(saveFile)));

          ps.println("Label\tGroup\tMean Period\tSD Period\tMean Phase\tSD Phase");

          for ( int i = 0; i< motionsListModel.getSize(); i++ ) {
            
            FittedMotions cMotions = motionsListModel.getElementAt(i);
            CosineFitResult cFittedModel = cMotions.fittedModel;
            
            if (cFittedModel != null) {
              ps.println( cMotions.label + "\t" +
                          cMotions.group + "\t" +
                          String.format("%5.2f\t%5.2f\t", 
                              cFittedModel.period, cFittedModel.stdPeriod) +
                          String.format("%5.2f\t%5.2f\t", 
                              cFittedModel.phase, cFittedModel.stdPhase)
                  );
            }
            
          }
          ps.println("");
          
          ListModel<String> groupAvgModel = 
              OptimizePanel.this.groupAvgList.getModel();
          
          if (groupAvgModel.getSize() > 0) {
            ps.println("Group Averages");
            for (int i = 0 ; i < groupAvgModel.getSize(); i++ ) {
              ps.println(groupAvgModel.getElementAt(i));
            }
          }
          
          ps.flush();
          ps.close();
        } catch (FileNotFoundException e) {
          
          e.printStackTrace();
        }

        
      }
      
    }
    
  }
}
