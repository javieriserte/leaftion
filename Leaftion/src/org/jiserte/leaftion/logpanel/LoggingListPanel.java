package org.jiserte.leaftion.logpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

public class LoggingListPanel extends JList<LogItem> {

  // ------------------------------------------------------------------------ //
  // Class constants
  private static final long serialVersionUID = 1L;
  public static final Color ERROR_TYPE_COLOR = new Color(255,111,105);
  public static final Color WARNING_TYPE_COLOR = new Color(255,238,173);
  public static final Color NORMAL_TYPE_COLOR = new Color(255,255,255);
  public static final int ERROR_TYPE =   2;
  public static final int WARNING_TYPE = 1;
  public static final int NORMAL_TYPE =  0;
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  // Instance variables
  private LogItem[] logContent;
  private int logIndex;
  private Map<Integer, Integer> allocatedSlots;
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  // Constructor
  public LoggingListPanel(int bufferSize) {
    super();
    this.allocatedSlots = new HashMap<>();
    this.logContent = new LogItem[bufferSize];
    this.logIndex = 0;
    this.setListData(this.logContent);
    this.setCellRenderer(this.new LogItemCellRenderer());
    this.updateLogUI();
  }
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  // Public interface
  @Deprecated
  public void addMessage(LogItem msg) {
    if ( this.logIndex >= this.logContent.length ) {
      this.shiftLogBack();
    }
    this.logContent[this.logIndex] = msg;
    this.logIndex++;
    this.updateLogUI();
  }
  
  public void addVolatileMessage(LogItem msg) {
    if ( this.headerAtTheEnd() ) {
      this.shiftLogBack();
    }
    this.putMessageAtHeader(msg);
    this.moveHeaderForward();
    this.updateLogUI();
  }
  
  public int allocateUpdatableSlot(String initialMessage) {
    
    if ( this.headerAtTheEnd() ) {
      this.shiftLogBack();
    } else {
    }
    
    int slotNumber = getNextFreeUpdatableSlotIndex();

    this.assignUpdatableSlotToHeader(slotNumber);
    this.putMessageAtHeader( new LogItem( initialMessage, 0 ));
    this.moveHeaderForward();
    this.updateLogUI() ;
    return slotNumber ;
  }

  private boolean headerAtTheEnd() {
    return this.logIndex >= this.logContent.length;
  }
  
  private void assignUpdatableSlotToHeader(int slotNumber ) {
    this.allocatedSlots.put( slotNumber, this.logIndex ) ;
  }

  private void putMessageAtHeader(LogItem logItem) {

    this.logContent[ this.logIndex ] = logItem;
  }

  private void moveHeaderForward() {
    this.logIndex++;
  }

  
  @SuppressWarnings("unused")
  private void moveHeaderBackwards() { 
    this.logIndex-- ; 
  }
  
  private int getNextFreeUpdatableSlotIndex() {
    Set<Integer> slots = this.allocatedSlots.keySet();
    if (slots.isEmpty()) {return 0;}
    int slotNumber = 0 ;
    for ( Integer i : slots ) { slotNumber = Math.max( i, slotNumber ); }
    return slotNumber+1;
  }
  
  public void updateAllocatedSlot(LogItem msg, int slot) {
    
    int index = this.allocatedSlots.get(slot);
    System.out.println("Slot: "+slot+" / Index: "+ index);
    if (index >= 0) {
      this.logContent[index] = msg;
      this.updateLogUI();
    }
  }
  
  public void clearLog() {
    
    this.logIndex = 0;
    this.updateLogUI();
  }
  
  @Deprecated
  public void updateMessage(LogItem msg) {
    if (this.logIndex >= this.logContent.length) {
      this.shiftLogBack();
    } else {
      this.logIndex--;
    }
    this.logContent[this.logIndex] = msg;
    this.logIndex++;
    this.updateLogUI();
    
    
  }

  
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  // private methods
  private void updateLogUI() {
    // Copy data of the log to the UI list object
    this.setListData(Arrays.copyOf(this.logContent, this.logIndex ));
    
    // Asynchronous update of the UI of the parent component
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        int lastIndex = LoggingListPanel.this.getModel().getSize() - 1;
        if (lastIndex >= 0) { 
          LoggingListPanel.this.ensureIndexIsVisible(lastIndex);
        }
      }
    });
  }

  // ------------------------------------------------------------------------ //
  // rewind moves all elements of a position lesser, drops the first element of
  // the list
  private void shiftLogBack() {
    // Checks that list contains elements
    if (this.logIndex>0) {
            
      // move all elements one step back in the list
      for (int i = 1; i< this.logIndex; i++) {
        this.logContent[i-1] = this.logContent[i]; 
      }
      
      // update indexs of allocated slots
      for (int i : this.allocatedSlots.keySet() ) {
        this.allocatedSlots.put(i, this.allocatedSlots.get(i)-1);
      }
      
    }
  }
  // ------------------------------------------------------------------------ //
  

  // ------------------------------------------------------------------------ //
  
  // ------------------------------------------------------------------------ //
  // Auxiliary Classes
  class LogItemCellRenderer extends JPanel implements ListCellRenderer<LogItem> {

    // ---------------------------------------------------------------------- //
    // Class constants
    private static final long serialVersionUID = -8889243393002627490L;
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Components
    private JLabel timeStamp;
    private JLabel type;
    private JLabel message;
    // ---------------------------------------------------------------------- //

    // ---------------------------------------------------------------------- //
    // Constructor
    public LogItemCellRenderer() {

      GridBagLayout layout = new GridBagLayout();
      layout.columnWeights = new double[]{0,0,1};
      layout.columnWidths = new int[]{40,20,100};
      layout.rowWeights = new double[]{1};
      layout.rowHeights = new int[]{20};
      this.setLayout(layout);

      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.CENTER;
      c.fill   = GridBagConstraints.BOTH;
      c.insets = new Insets(3, 3, 3, 3);

      c.gridx = 0;
      c.gridy = 0;
      this.timeStamp = new JLabel();
      this.timeStamp.setOpaque(false);
      
      this.add(this.timeStamp,c);

      
      c.gridx = 1;
      c.gridy = 0;
      this.type = new JLabel();
      this.type.setOpaque(false);
      this.type.setText(">");
      this.add(this.type,c);
      
      c.gridx = 2;
      c.gridy = 0;
      this.message = new JLabel();
      this.message.setOpaque(false);
      this.add(this.message,c);

      this.setOpaque(true);
        
    }
    // ---------------------------------------------------------------------- //

    
    @Override
    public Component getListCellRendererComponent(JList<? extends LogItem> list,
        LogItem value, int index, boolean isSelected, boolean cellHasFocus) {
      
      if (value==null){
        return this;
      }
      switch (value.getType()) {
      case LoggingListPanel.ERROR_TYPE:
        this.setBackground(ERROR_TYPE_COLOR );
        break;
      case LoggingListPanel.WARNING_TYPE:
        this.setBackground(WARNING_TYPE_COLOR);
        break;
      case LoggingListPanel.NORMAL_TYPE:
        this.setBackground(NORMAL_TYPE_COLOR);
        break;
      }
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
      this.timeStamp.setText(sdf.format(value.getTimeStamp()));
      this.message.setText(value.getMessage());
      return this;
    }
   
}

  
  // ------------------------------------------------------------------------ //
  
}

