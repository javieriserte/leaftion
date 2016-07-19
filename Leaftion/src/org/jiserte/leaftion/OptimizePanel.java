package org.jiserte.leaftion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
    lpLayout.rowHeights = new int[] { 20, 0, 20, 20, 0 };
    lpc.insets = new Insets(4, 4, 4, 4);

    lpc.gridx = 0;
    lpc.gridy = 0;
    lpc.fill = GridBagConstraints.BOTH;
    listPanel.add(new JLabel("Samples"), lpc);

    JButton clearGroupsBtn = new JButton("Clear groups");
    lpc.gridx = 0;
    lpc.gridy = 2;
    listPanel.add(clearGroupsBtn, lpc);

    clearGroupsBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        OptimizePanel.this.clearGroups();
      }
    });

    JButton setGroupBtn = new JButton("Make group");
    lpc.gridx = 1;
    lpc.gridy = 2;
    lpc.fill = GridBagConstraints.BOTH;
    listPanel.add(setGroupBtn, lpc);

    setGroupBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        OptimizePanel.this.groupSelected();
      }
    });

    JButton calcGroupsBtn = new JButton("Group avg.");
    lpc.gridx = 2;
    lpc.gridy = 2;
    listPanel.add(calcGroupsBtn, lpc);
    calcGroupsBtn.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        OptimizePanel.this.showGroupAverages();

      }
    });

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

    this.motionsList.setMinimumSize(new Dimension(150, 100));
    this.motionsList.setPreferredSize(new Dimension(150, 100));

    this.motionsList.setCellRenderer(new MotionListCellRenderer());

    this.motionsList.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent e) {

        prepareAndShowMotionPlotPanel();

      }

    });

    lpc.gridx = 0;
    lpc.gridy = 1;
    lpc.gridwidth = 3;
    listPanel.add(this.motionsList, lpc);

    this.plotPanel = new MotionPlotPanel();

    // this.motionsList.setSelectedIndex(0);
    // this.plotPanel.yData =
    // this.motionsList.getSelectedValue().motions.getV_motion();
    // this.plotPanel.xData = new double[this.plotPanel.yData.length];
    // for (int i = 0 ; i< this.plotPanel.xData.length; i++) {
    // this.plotPanel.xData[i] = (double) i;
    // }

    this.setLayout(new BorderLayout());

    JPanel optionsPanel = new JPanel();

    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();

    optionsPanel.setLayout(layout);

    // this.startScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0,
    // this.plotPanel.yData.length);
    this.startScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 1);

    this.endScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 1);
    // this.endScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0,
    // this.plotPanel.yData.length);

    this.startScrollBar.addAdjustmentListener(new AdjustmentListener() {
      @Override
      public void adjustmentValueChanged(AdjustmentEvent e) {

        int start = e.getValue();
        int end = Math.max(start, OptimizePanel.this.endScrollBar.getValue());

        OptimizePanel.this.endScrollBar.setValue(end);
        OptimizePanel.this.plotPanel.startSelectIndex = start;
        OptimizePanel.this.plotPanel.endSelectIndex = end;

        OptimizePanel.this.startFrameInd.setText(String.valueOf(start));
        OptimizePanel.this.endFrameInd.setText(String.valueOf(end));

        // OptimizePanel.this.endScrollBar.updateUI();
        OptimizePanel.this.startFrameInd.updateUI();
        OptimizePanel.this.endFrameInd.updateUI();
        OptimizePanel.this.plotPanel.updateUI();
      }

    });

    this.endScrollBar.addAdjustmentListener(new AdjustmentListener() {

      @Override
      public void adjustmentValueChanged(AdjustmentEvent e) {
        int end = e.getValue();
        int start = Math.min(end, OptimizePanel.this.startScrollBar.getValue());

        OptimizePanel.this.plotPanel.startSelectIndex = start;
        OptimizePanel.this.plotPanel.endSelectIndex = end;

        OptimizePanel.this.startScrollBar.setValue(start);
        OptimizePanel.this.startFrameInd.setText(String.valueOf(start));
        OptimizePanel.this.endFrameInd.setText(String.valueOf(end));
        //
        // OptimizePanel.this.startScrollBar.updateUI();
        OptimizePanel.this.startFrameInd.updateUI();
        OptimizePanel.this.endFrameInd.updateUI();
        OptimizePanel.this.plotPanel.updateUI();
      }
    });

    JButton optimButton = new JButton("Fit data");

    optimButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        int[] selIdx = OptimizePanel.this.motionsList.getSelectedIndices();

        int replicates = 5;

        int iterations = 10000;

        for (int i : selIdx) {

          FittedMotions fm = motionsList.getModel().getElementAt(i);

          double[] y = fm.motions.getV_motion();

          double[] x = new double[y.length];

          for (int j = 0; j < x.length; j++) {

            x[j] = j * interval;

          }

          List<CosineModel> models = new ArrayList<>();

          ModelEvaluator me = new ModelEvaluator(iterations, x, y);

          for (int j = 0; j < replicates; j++) {
            CosineModel cm = me.optimize();
            models.add(cm);
          }

          // ------------------------------------------------------ //
          // Get means and std dev for periods and phases
          double meanPer = 0;
          double meanPha = 0;
          double stdPer = 0;
          double stdPha = 0;

          for (CosineModel m : models) {
            meanPer += m.getPeriod();
            meanPha += m.getPhase();
          }

          meanPer /= replicates;
          meanPha /= replicates;

          for (CosineModel m : models) {
            stdPer += Math.pow(m.getPeriod() - meanPer, 2);
            stdPha += Math.pow(m.getPhase() - meanPha, 2);
          }
          stdPer = Math.sqrt(stdPer) / replicates;
          stdPha = Math.sqrt(stdPha) / replicates;

          CosineFitResult r = new CosineFitResult();

          r.period = meanPer;
          r.phase = meanPha;
          r.stdPeriod = stdPer;
          r.stdPhase = stdPha;
          // ------------------------------------------------------ //

          // ------------------------------------------------------ //
          // Build fitting profile
          double[][] objProfile = new double[5][50];

          double cellSize = iterations / 50;

          for (int jj = 0; jj < 50; jj++) {

            objProfile[0][jj] = (int) ((jj + 1) * cellSize); // Iteration
            // range
            objProfile[1][jj] = Double.MAX_VALUE; // Min Objective
                                                  // Value
            objProfile[2][jj] = 0; // Mean Objective Value
            objProfile[3][jj] = 0; // Max Objective Value
            objProfile[4][jj] = 0; // counter

          }

          for (CosineModel m : models) {
            for (int k = 0; k < 50; k++) {
              double oValue = m.getObjectiveSeries()[k];

              objProfile[1][k] = Math.min(objProfile[1][k], oValue);
              objProfile[2][k] += oValue;
              objProfile[3][k] = Math.max(objProfile[3][k], oValue);
              objProfile[4][k] = objProfile[4][k] + 1;

            }
          }

          for (int k = 0; k < 50; k++) {
            if (objProfile[4][k] > 0) {
              objProfile[2][k] = objProfile[2][k] / objProfile[4][k];
            } else {
              objProfile[2][k] = -1;
            }
          }

          r.objMaxs = objProfile[3];
          r.objMeans = objProfile[2];
          r.objMins = objProfile[1];
          r.acceptedIter = objProfile[0];

          // ------------------------------------------------------ //

          // ------------------------------------------------------ //
          // Build means histogram
          double[] hist = OptimizePanel.this.getHistogram(models);
          r.hist = hist;
          double[] minmax = OptimizePanel.this.getMinMaxPeriods(models);
          r.minPer = minmax[0];
          r.maxPer = minmax[1];
          // ------------------------------------------------------ //

          motionsList.getModel().getElementAt(i).fittedModel = r;

          // ------------------------------------------------------ //
          // update UI
          motionsList.updateUI();
          OptimizePanel.this.prepareAndShowMotionPlotPanel();
          // ------------------------------------------------------ //

        }

      }
    });

    JButton saveButton = new JButton("Save");

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

    this.repTxt = new JTextField("100");
    c.gridy = 0;
    c.gridx = 4;
    c.gridheight = 1;
    optionsPanel.add(this.repTxt, c);

    this.iterTxt = new JTextField("10000");
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

  protected double[] getHistogram(List<CosineModel> models) {
    double[] minMax = getMinMaxPeriods(models);

    double minPer = minMax[0];
    double maxPer = minMax[1];

    double cellSize = (maxPer - minPer) / 10;
    double[] hist = new double[10];
    for (int i = 0; i < hist.length; i++) {
      hist[i] = 0;
    }
    for (CosineModel m : models) {

      double currentPeriod = m.getPeriod();

      int index = (int) Math.min((currentPeriod - minPer) / cellSize, 9);
      hist[index]++;

    }

    return hist;

  }

  private double[] getMinMaxPeriods(List<CosineModel> models) {
    double[] minMax = new double[2];
    minMax[0] = Double.MAX_VALUE;
    minMax[1] = 0;

    for (CosineModel m : models) {
      double currentPeriod = m.getPeriod();
      minMax[0] = Math.min(minMax[0], currentPeriod);
      minMax[1] = Math.max(minMax[1], currentPeriod);
    }
    return minMax;
  }

  protected void groupSelected() {

    int maxGroupIndex = 0;

    for (int i = 0; i < this.motionsList.getModel().getSize(); i++) {
      if (!this.motionsList.isSelectedIndex(i)) {
        maxGroupIndex = Math.max(maxGroupIndex, this.motionsList.getModel().getElementAt(i).group);
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

      double cPeriod = current.fittedModel.period;
      double cPhase = current.fittedModel.phase;
      int cGroup = current.group;

      if (!periodMeansByGroup.keySet().contains(cGroup)) {
        periodMeansByGroup.put(cGroup, new ArrayList<>());
        phaseMeansByGroup.put(cGroup, new ArrayList<>());
      }
      periodMeansByGroup.get(cGroup).add(cPeriod);
      phaseMeansByGroup.get(cGroup).add(cPhase);

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

      groupMeansData[i] = String.format("Gr: %d Per: %5.2f (%5.2f) Pha: %5.2f (%5.2f)", cgroup, sumPer, sdPer, sumPha,
          sdPha);
    }

    OptimizePanel.this.groupAvgList.setFont(new Font("Verdana", 1, 9));
    OptimizePanel.this.groupAvgList.setListData(groupMeansData);
    OptimizePanel.this.groupAvgList.updateUI();

  }

  private void prepareAndShowMotionPlotPanel() {

    int[] indexes = OptimizePanel.this.motionsList.getSelectedIndices();
    
    if (indexes.length == 1) {

      FittedMotions motion = OptimizePanel.this.motionsList.getSelectedValue();

      if (motion == null) {
        return;
      }

      if (motion.fittedModel != null) {
        OptimizePanel.this.plotPanel.objmeans = motion.fittedModel.objMeans;
        OptimizePanel.this.plotPanel.objmins = motion.fittedModel.objMins;
        OptimizePanel.this.plotPanel.objmaxs = motion.fittedModel.objMaxs;
        OptimizePanel.this.plotPanel.iters = motion.fittedModel.acceptedIter;
        OptimizePanel.this.plotPanel.minPer = motion.fittedModel.minPer;
        OptimizePanel.this.plotPanel.maxPer = motion.fittedModel.maxPer;
        OptimizePanel.this.plotPanel.hist = motion.fittedModel.hist;

        OptimizePanel.this.plotPanel.modelPeriod = motion.fittedModel.period;
        OptimizePanel.this.plotPanel.modelPhase = motion.fittedModel.phase;

        OptimizePanel.this.plotPanel.showFittingProfiles = true;
      } else {
        OptimizePanel.this.plotPanel.showFittingProfiles = false;
      }

      OptimizePanel.this.plotPanel.yData = new double[1][];
      OptimizePanel.this.plotPanel.yData[0] = motion.motions.getV_motion();

      OptimizePanel.this.plotPanel.xData = new double[1][OptimizePanel.this.plotPanel.yData[0].length];

      for (int i = 0; i < OptimizePanel.this.plotPanel.xData[0].length; i++) {
        OptimizePanel.this.plotPanel.xData[0][i] = (double) i * OptimizePanel.this.interval;
      }

      OptimizePanel.this.plotPanel.startSelectIndex = 0;
      OptimizePanel.this.plotPanel.endSelectIndex = 0;
      if (OptimizePanel.this.startScrollBar != null) {
        OptimizePanel.this.startScrollBar.setValue(0);
        OptimizePanel.this.startScrollBar.setMaximum(
            OptimizePanel.this.plotPanel.yData[0].length);
      }
      if (OptimizePanel.this.endScrollBar != null) {
        OptimizePanel.this.endScrollBar.setValue(0);
        OptimizePanel.this.endScrollBar.setMaximum(
            OptimizePanel.this.plotPanel.yData[0].length);
      }

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
      mgr.rowHeights = new int[] { 20, 20 };
      mgr.rowWeights = new double[] { 1, 1 };

      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.CENTER;
      c.fill = GridBagConstraints.BOTH;

      c.gridx = 0;
      c.gridy = 0;
      c.gridwidth = 2;
      p.add(regionLabel, c);

      String per = "Period: ";
      String phase = "Phase: ";

      if (value.fittedModel == null) {
        per = per + "-";
        phase = phase + "-";
      } else {
        per = per + String.format("%5.2f(%3.2f)", value.fittedModel.period, value.fittedModel.stdPeriod);
        phase = phase + String.format("%5.2f(%3.2f)", value.fittedModel.phase, value.fittedModel.stdPhase);
      }

      JLabel perLabel = new JLabel(per);
      JLabel phaseLabel = new JLabel(phase);

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

      c.gridx = 1;
      c.gridy = 0;
      p.add(new JLabel("Group:" + value.group), c);

      p.setBackground(isSelected ? new Color(0, 150, 240) : Color.white);
      return p;
    }
  }

}
