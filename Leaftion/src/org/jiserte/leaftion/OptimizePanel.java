package org.jiserte.leaftion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;

import org.jiserte.leaftion.math.Motions;

public class OptimizePanel extends JPanel {

	/**
	 * +
	 */
	private static final long serialVersionUID = -3642747013037883477L;


	private FittedMotions[] motions;
	
	
	private JList<FittedMotions> motionsList;
	private MotionPlotPanel plotPanel;
	
	
	

	public OptimizePanel() {
		super();
		
		// ------------------------------------------------------------------ //
		// Create dummies Fitted Motions for debug
		FittedMotions fm1 = new FittedMotions();
		fm1.label = "FM1";
		fm1.fittedModel = null;
		fm1.motions = new Motions(new double[]{0,0,0}, new double[]{1,2,3});
		FittedMotions fm2 = new FittedMotions();
		fm2.label = "FM2";
		fm2.fittedModel = null;
		fm2.motions = new Motions(new double[]{0,0,0}, new double[]{2,3,4});
		this.motions = new FittedMotions[]{fm1,fm2};
		// ------------------------------------------------------------------ //
		
		this.createGUI();
	}

	private void createGUI() {

		this.motionsList = new JList<>(motions);
		
		this.motionsList.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		this.motionsList.setMinimumSize(new Dimension(150, 100));
		this.motionsList.setPreferredSize(new Dimension(150, 100));

		
		this.motionsList.setCellRenderer( new ListCellRenderer<FittedMotions>() {

			@Override
			public Component getListCellRendererComponent(JList<? extends FittedMotions> list, FittedMotions value,
					int index, boolean isSelected, boolean cellHasFocus) {
				
				JLabel r = new JLabel(value.label);
				
				JPanel jPanel = new JPanel();
				jPanel.setLayout(new BorderLayout());
				jPanel.add(r, BorderLayout.CENTER);
//				jPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
				jPanel.setBackground( isSelected?new Color( 0, 150, 240):Color.white);
				return jPanel;
			}
		});
		
		this.plotPanel = new MotionPlotPanel();
		
		this.plotPanel.xData = new double[]{0,1,2,3,4,5,6,7,8,9,10};
		this.plotPanel.yData = new double[]{0,1,2,3,2,1,0,-1,-2,-1,0};

		
		this.setLayout(new BorderLayout());
		
		this.add(this.motionsList, BorderLayout.WEST);
		
		this.add(this.plotPanel, BorderLayout.CENTER);
		
	}
	
	
	

}
