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

import org.jiserte.leaftion.math.ModelEvaluator;

public class OptimizePanel extends JPanel {

	/**
	 * +
	 */
	private static final long serialVersionUID = -3642747013037883477L;


	private FittedMotions[] motions;
	private JList<FittedMotions> motionsList;
	private MotionPlotPanel plotPanel;
	private double interval;


  private JScrollBar startScrollBar;


  private JScrollBar endScrollBar;
	
	
	

	public OptimizePanel() {
		super();


		// ------------------------------------------------------------------ //
		// Create dummies Fitted Motions for debug
//		FittedMotions fm1 = new FittedMotions();
//		fm1.label = "FM1";
//		fm1.fittedModel = null;
//		fm1.motions = new Motions(
//		    new double[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}, 
//		    new double[]{0,1,2,3,2,1,0,-1,-2,-1,0,0,1,2,1,2,1,0,-1,-4,-1});
//		FittedMotions fm2 = new FittedMotions();
//		fm2.label = "FM2";
//		fm2.fittedModel = null;
//		fm2.motions = new Motions(
//		    new double[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}, 
//		    new double[]{-2,-1,0,0,1,2,3,2,1,0,-1,-2,-1,-3,-2.5,-1,2,3,3,1,0});
//		this.motions = new FittedMotions[]{fm1,fm2};
		// ------------------------------------------------------------------ //
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

		this.motionsList = new JList<>();
		
		this.motionsList.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		this.motionsList.setMinimumSize(new Dimension(150, 100));
		this.motionsList.setPreferredSize(new Dimension(150, 100));

		
		this.motionsList.setCellRenderer( new ListCellRenderer<FittedMotions>() {

			@Override
			public Component getListCellRendererComponent(JList<? extends FittedMotions> list, FittedMotions value,
					int index, boolean isSelected, boolean cellHasFocus) {
				
				JLabel regionLabel = new JLabel(value.label);
				
				regionLabel.setFont(new Font("Verdana",Font.BOLD,12));
				
				JPanel jPanel = new JPanel();
				GridBagLayout mgr = new GridBagLayout();
				jPanel.setLayout(mgr);				
				
				mgr.columnWidths = new int[]{100,100};
				mgr.columnWeights = new double[]{0,1};
				mgr.rowHeights = new int[]{20,20};
				mgr.rowWeights = new double[]{1,1};
				
				GridBagConstraints c = new GridBagConstraints();
				c.anchor = GridBagConstraints.CENTER;
				c.fill = GridBagConstraints.BOTH;
				
				c.gridx=0;
				c.gridy=0;
				c.gridwidth=2;
				jPanel.add(regionLabel, c);
				
				String per = "Período: ";
				String phase = "Fase: ";
				
				if (value.fittedModel == null ) {
					per = per + "-";
					phase = phase + "-";
				} else {
					per = per + String.format("%5.2f", value.fittedModel.getPeriod());
					phase = phase + String.format("%5.2f", value.fittedModel.getPhase());
				}
				
				JLabel perLabel = new JLabel(per);
				JLabel phaseLabel = new JLabel(phase);
				
				Font font = new Font("Verdana",0,10);
				
				perLabel.setFont(font);
				c.gridx=0;
				c.gridy=1;
				c.gridwidth=1;
				jPanel.add(perLabel, c);


				c.gridx=1;
				c.gridy=1;
				phaseLabel.setFont( font);
				jPanel.add(phaseLabel, c);

				
//				jPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
				jPanel.setBackground( isSelected?new Color( 0, 150, 240):Color.white);
				return jPanel;
			}
		});
		
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

		
		this.startScrollBar.addAdjustmentListener(new AdjustmentListener() {
      @Override
      public void adjustmentValueChanged(AdjustmentEvent e) {
        OptimizePanel.this.plotPanel.startSelectIndex = e.getValue();
        OptimizePanel.this.plotPanel.updateUI();
      }
      
    });

    this.endScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 1);
//    this.endScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, this.plotPanel.yData.length);
    
    this.endScrollBar.addAdjustmentListener(new AdjustmentListener() {
      
      @Override
      public void adjustmentValueChanged(AdjustmentEvent e) {
        OptimizePanel.this.plotPanel.endSelectIndex = e.getValue();
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
				
				ModelEvaluator me = new ModelEvaluator( 10000, x, y );
				
				motionsList.getModel().getElementAt(i).fittedModel = me.optimize();
				
				motionsList.updateUI();
			
			}
			
			
		}
	});

    layout.columnWidths = new int[]{50,100,50};
    layout.rowHeights = new int[]{20,20};
    layout.columnWeights = new double[]{0,1,0};
    layout.rowWeights = new double[]{1,1};

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
    
    c.gridy = 0;
    c.gridx=2;
    c.gridheight = 2;
    
    optionsPanel.add(optimButton, c);

	
	JSplitPane jSplitPane = new JSplitPane();
    jSplitPane.setLeftComponent(this.motionsList);
		jSplitPane.setRightComponent(this.plotPanel);
    this.add(jSplitPane, BorderLayout.CENTER);
		
		this.add(optionsPanel, BorderLayout.SOUTH);
		
	}
	
	
	public void setMotionEstimation( FittedMotions[] motions ) {
	  
	  this.motions = motions;
	  
	  if (motions.length>0) {

	    this.motionsList.setListData(motions);
	    this.motionsList.updateUI();
	    
	    
	  }
	  
	}

}
