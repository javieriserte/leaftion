package org.jiserte.leaftion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jiserte.leaftion.math.CosineFitResult;
import org.jiserte.leaftion.math.CosineModel;
import org.jiserte.leaftion.math.ModelEvaluator;

public class OptimizePanel extends JPanel {

	/**
	 * +
	 */
	private static final long serialVersionUID = -3642747013037883477L;


	private FittedMotions[] motions;
	private JList<FittedMotions> motionsList;
	private JList<String> groupAvgList;
	private MotionPlotPanel plotPanel;
	private double interval;


  private JScrollBar startScrollBar;

  private JLabel startFrameInd;
  private JLabel endFrameInd;
  private JScrollBar endScrollBar;
	
	
	

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
    
    lpLayout.columnWeights = new double[]{1,1};
    lpLayout.columnWidths = new int[]{100,100};
    lpLayout.rowWeights = new double[]{0,0.5,0,0,0.5};
    lpLayout.rowHeights = new int [] {20,0,20,20,0};
    lpc.insets = new Insets(4,4,4,4);
    
    lpc.gridx = 0;
    lpc.gridy = 0;
    lpc.fill = GridBagConstraints.BOTH;
    listPanel.add(new JLabel("Muetras"),lpc);
    
    JButton clearGroupsBtn = new JButton("Borrar");
    lpc.gridx = 0;
    lpc.gridy = 2;
    listPanel.add(clearGroupsBtn,lpc);
    
    clearGroupsBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        OptimizePanel.this.clearGroups();
      }
    });
    

    JButton setGroupBtn = new JButton("Agrupar");
    lpc.gridx = 1;
    lpc.gridy = 2;
    lpc.fill = GridBagConstraints.BOTH;
    listPanel.add(setGroupBtn,lpc);

    setGroupBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        OptimizePanel.this.groupSelected();
      }
    });
    
    lpc.gridx = 0;
    lpc.gridy = 3;
    listPanel.add(new JLabel("Grupos"),lpc);

    lpc.gridx = 0;
    lpc.gridy = 4;
    lpc.gridwidth = 2;
    this.groupAvgList = new JList<>();
    this.groupAvgList.setBorder(BorderFactory.createBevelBorder(
        BevelBorder.LOWERED));
    listPanel.add(this.groupAvgList,lpc);

    
		this.motionsList = new JList<>();
		this.motionsList.setBorder(BorderFactory.createBevelBorder(
		    BevelBorder.LOWERED));
		
		this.motionsList.setMinimumSize(new Dimension(150, 100));
		this.motionsList.setPreferredSize(new Dimension(150, 100));

		
		this.motionsList.setCellRenderer( new MotionListCellRenderer());
		
		this.motionsList.addListSelectionListener(new ListSelectionListener() {
      
      @Override
      public void valueChanged(ListSelectionEvent e) {
        
        FittedMotions motion = OptimizePanel.this.motionsList.getSelectedValue();
        
        OptimizePanel.this.plotPanel.yData = motion.motions.getV_motion();
        
        OptimizePanel.this.plotPanel.xData = new double[OptimizePanel.this.plotPanel.yData.length];
        
        for (int i = 0 ; i< OptimizePanel.this.plotPanel.xData.length; i++) {
          OptimizePanel.this.plotPanel.xData[i] = (double) i * OptimizePanel.this.interval;
        }
        
        OptimizePanel.this.plotPanel.startSelectIndex = 0;
        OptimizePanel.this.plotPanel.endSelectIndex = 0;
        if (OptimizePanel.this.startScrollBar != null) {
          OptimizePanel.this.startScrollBar.setValue(0);
          OptimizePanel.this.startScrollBar.setMaximum(OptimizePanel.this.plotPanel.yData.length);
        }
        if (OptimizePanel.this.endScrollBar != null) {
          OptimizePanel.this.endScrollBar.setValue(0);
          OptimizePanel.this.endScrollBar.setMaximum(OptimizePanel.this.plotPanel.yData.length);
        }
        
        
        OptimizePanel.this.plotPanel.updateUI();
      }
      
    } );
		
		lpc.gridx=0;
		lpc.gridy=1;
		lpc.gridwidth=2;
		listPanel.add(this.motionsList, lpc);
		
		this.plotPanel = new MotionPlotPanel();
		
//		this.motionsList.setSelectedIndex(0);
//		this.plotPanel.yData = this.motionsList.getSelectedValue().motions.getV_motion();
//    this.plotPanel.xData = new double[this.plotPanel.yData.length];
//    for (int i = 0 ; i< this.plotPanel.xData.length; i++) {
//      this.plotPanel.xData[i] = (double) i;
//    }
		
		
		
		this.setLayout(new BorderLayout());
		
		JPanel optionsPanel = new JPanel();
		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		optionsPanel.setLayout(layout);
		
//		this.startScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, this.plotPanel.yData.length);
    this.startScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 1);

    this.endScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 1);
//  this.endScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, this.plotPanel.yData.length);
    
		this.startScrollBar.addAdjustmentListener(new AdjustmentListener() {
      @Override
      public void adjustmentValueChanged(AdjustmentEvent e) {
        
        int start = e.getValue();
        int end = Math.max(start, OptimizePanel.this.endScrollBar.getValue() );
        
        OptimizePanel.this.endScrollBar.setValue(end);
        OptimizePanel.this.plotPanel.startSelectIndex = start;
        OptimizePanel.this.plotPanel.endSelectIndex = end;
        
        
        OptimizePanel.this.startFrameInd.setText(String.valueOf(start));
        OptimizePanel.this.endFrameInd.setText(String.valueOf(end));
        
//        OptimizePanel.this.endScrollBar.updateUI();
        OptimizePanel.this.startFrameInd.updateUI();
        OptimizePanel.this.endFrameInd.updateUI();
        OptimizePanel.this.plotPanel.updateUI();
      }
      
    });


    
    this.endScrollBar.addAdjustmentListener(new AdjustmentListener() {
      
      @Override
      public void adjustmentValueChanged(AdjustmentEvent e) {
        int end = e.getValue();
        int start = Math.min(end, OptimizePanel.this.startScrollBar.getValue() );
        
        OptimizePanel.this.plotPanel.startSelectIndex = start;
        OptimizePanel.this.plotPanel.endSelectIndex = end;
        
        OptimizePanel.this.startScrollBar.setValue(start);
        OptimizePanel.this.startFrameInd.setText(String.valueOf(start));
        OptimizePanel.this.endFrameInd.setText(String.valueOf(end));
//        
//        OptimizePanel.this.startScrollBar.updateUI();
        OptimizePanel.this.startFrameInd.updateUI();
        OptimizePanel.this.endFrameInd.updateUI();
        OptimizePanel.this.plotPanel.updateUI();
      }
    });
    
    
    JButton optimButton = new JButton("Optimize");
    
    
    optimButton.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			int[] selIdx = OptimizePanel.this.motionsList.getSelectedIndices();
			
			for (int i : selIdx) {
			
				FittedMotions fm = motionsList.getModel().getElementAt(i);
				
				double[] y = fm.motions.getV_motion();
				
				double[] x = new double[y.length];
				
				for (int j = 0; j < x.length; j++) {
					
					x[j] = j * interval;
					
				}
				
				List<CosineModel> models = new ArrayList<>();
				
				ModelEvaluator me = new ModelEvaluator( 10000, x, y );
				
				for (int j = 0; j < 100; j++) {
				  CosineModel cm = me.optimize();
				  models.add(cm);
				}
				
				double meanPer = 0;
        double meanPha = 0;
        double stdPer = 0;
        double stdPha = 0;

				
				for ( CosineModel m : models) {
				  meanPer += m.getPeriod();
          meanPha += m.getPhase();
				}
				meanPer /= 100;
				meanPha /= 100;

				for ( CosineModel m : models) {
          stdPer += Math.pow(m.getPeriod() - meanPer,2);
          stdPha += Math.pow(m.getPhase() - meanPha,2);
        }
				stdPer = Math.sqrt( stdPer ) / 100;
        stdPha = Math.sqrt( stdPha ) / 100;
        
        CosineFitResult r = new CosineFitResult();
        
        r.period = meanPer;
        r.phase = meanPha;
				r.stdPeriod = stdPer;
				r.stdPhase = stdPha;
				
				motionsList.getModel().getElementAt(i).fittedModel = r;
				
				motionsList.updateUI();
			
			}
			
			
		}
	});
    
    JButton saveButton = new JButton("Guardar");

    layout.columnWidths  = new int[] {50,100,40,50,50};
    layout.rowHeights    = new int[] {20,20};
    layout.columnWeights = new double[]{0,1,0,0,0};
    layout.rowWeights    = new double[]{1,1};

    c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;

    c.gridx = 0;
    c.gridy = 0;
		optionsPanel.add(new JLabel("Inicio de selección:"), c);

		c.gridx = 0;
    c.gridy = 1;
		optionsPanel.add(new JLabel("Fin de selección:"), c);
		
    c.gridx = 1;
    c.gridy = 0;

		optionsPanel.add(startScrollBar,c);
		
		c.gridy=1;
    optionsPanel.add(endScrollBar,c);

    this.startFrameInd = new JLabel();
    c.gridy = 0;
    c.gridx = 2;
    optionsPanel.add(this.startFrameInd, c);

    this.endFrameInd = new JLabel();
    c.gridy = 1;
    c.gridx = 2;
    optionsPanel.add(this.endFrameInd, c);
    
    c.gridy = 0;
    c.gridx=3;
    c.gridheight = 2;
    optionsPanel.add(optimButton, c);
    
    c.gridy = 0;
    c.gridx=4;
    c.gridheight = 2;
    optionsPanel.add(saveButton, c);
    
	
	JSplitPane jSplitPane = new JSplitPane();
    jSplitPane.setLeftComponent(listPanel);
		jSplitPane.setRightComponent(this.plotPanel);
    this.add(jSplitPane, BorderLayout.CENTER);
		
		this.add(optionsPanel, BorderLayout.SOUTH);
		
	}
	
	
	protected void groupSelected() {

	 int maxGroupIndex = 0;
	  
	  for (int i = 0; i < this.motionsList.getModel().getSize(); i++) {
	    if (! this.motionsList.isSelectedIndex(i)) {
	      maxGroupIndex = Math.max(
	          maxGroupIndex, 
	          this.motionsList.getModel().getElementAt(i).group );
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

  public void setMotionEstimation( FittedMotions[] motions ) {
	  this.motions = motions;
	  if (motions.length>0) {
	    this.motionsList.setListData(motions);
	    this.clearGroups();
	  }
	}
  
  public void showGroupAverages() {
  
    Map<Integer, List<Double>> meansByGroup = new HashMap<>();
    
    for (int i = 0; i < this.motionsList.getModel().getSize(); i++) {

      

    }
    
  }
  
  public class MotionListCellRenderer implements ListCellRenderer<FittedMotions> {
    @Override
    public Component getListCellRendererComponent(
        JList<? extends FittedMotions> list, FittedMotions value,
        int index, boolean isSelected, boolean cellHasFocus) {
      
      JLabel regionLabel = new JLabel(value.label);
      
      regionLabel.setFont(new Font("Verdana",Font.BOLD,12));
      
      GridBagLayout mgr = new GridBagLayout();
      
      JPanel p = new JPanel();
      p.setLayout(mgr);        
      
      mgr.columnWidths = new int[]{100,100};
      mgr.columnWeights = new double[]{1,1};
      mgr.rowHeights = new int[]{20,20};
      mgr.rowWeights = new double[]{1,1};
      
      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.CENTER;
      c.fill = GridBagConstraints.BOTH;
      
      c.gridx=0;
      c.gridy=0;
      c.gridwidth=2;
      p.add(regionLabel, c);
      
      String per = "Período: ";
      String phase = "Fase: ";
      
      if (value.fittedModel == null ) {
        per = per + "-";
        phase = phase + "-";
      } else {
        per = per + String.format("%5.2f(%3.2f)", value.fittedModel.period, value.fittedModel.stdPeriod);
        phase = phase + String.format("%5.2f(%3.2f)", value.fittedModel.phase, value.fittedModel.stdPhase);
      }
      
      JLabel perLabel = new JLabel(per);
      JLabel phaseLabel = new JLabel(phase);
      
      Font font = new Font("Verdana",0,10);
      
      perLabel.setFont(font);
      c.gridx=0;
      c.gridy=1;
      c.gridwidth=1;
      p.add(perLabel, c);


      c.gridx=1;
      c.gridy=1;
      phaseLabel.setFont( font);
      p.add(phaseLabel, c);
      
      c.gridx=1;
      c.gridy=0;
      p.add(new JLabel("Grupo:" + value.group), c);

      p.setBackground( isSelected?new Color( 0, 150, 240):Color.white);
      return p;
    }
  }
  
  
  

}
