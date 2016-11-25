package org.jiserte.leaftion.logpanel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogItem {

  // ------------------------------------------------------------------------ //
  // Class constans
//  public static final int LOG_ITEM_ERROR = 0;
//  public static final int LOG_ITEM_WARNING = 1;
//  public static final int LOG_ITEM_NORMAL = 2;
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  // Instance variables
  private String message;
  private int type;
  private Date timeStamp;
  // TODO: add message types here
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  // Constructor
  public LogItem(String message, int type) {
    this.setMessage(message);
    this.setType(type);
    this.setTimeStamp(Calendar.getInstance().getTime());
  }
  // ------------------------------------------------------------------------ //

  // ------------------------------------------------------------------------ //
  // Public Interface
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }
  public int getType() {
    return type;
  }
  public void setType(int type) {
    this.type = type;
  }
  public void setTimeStamp(Date time) {
    this.timeStamp = time;
  }
  public Date getTimeStamp() {
    return this.timeStamp;
  }

  public String toString() {
    String typeDesc = "";
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    if (this.getType() == LoggingListPanel.ERROR_TYPE) {
      typeDesc = " ERROR:";
    }
    if (this.getType() == LoggingListPanel.WARNING_TYPE) {
      typeDesc = " WARNING: ";
    }
    return sdf.format(this.getTimeStamp()) + ">>" + typeDesc + " " + this.getMessage();
  }
  // ------------------------------------------------------------------------ //


}
