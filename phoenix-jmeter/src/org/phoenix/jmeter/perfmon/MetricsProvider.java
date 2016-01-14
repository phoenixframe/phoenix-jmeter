package org.phoenix.jmeter.perfmon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.jmeter.util.JMeterUtils;

public class MetricsProvider
  implements Runnable, AgentCommandsInterface
{
  public static final String SEMICOLON = ";";
  public static final int DELAY = 1000;
  private boolean testIsRunning = false;
  private HashMap<String, Long> oldValues = new HashMap<String, Long>();
  private int monitorType = -1;
  private AgentConnector[] connectors = null;
  private static BufferedWriter outWriter = null;
  private int connectTimeout = 10000;
  
  public MetricsProvider(int monitorType,AgentConnector[] connectors,String s)
  {
    this.connectors = connectors;
    this.monitorType = monitorType;
  }
  
  public MetricsProvider(int monitorType, AgentConnector[] connectors)
  {
    this.connectors = connectors;
    this.monitorType = monitorType;
    openOutputFile();
  }
  
  private static synchronized void openOutputFile()
  {
    if (outWriter == null)
    {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
      
      String fileName = JMeterUtils.getPropDefault("jppmfile", "perfmon_" + formatter.format(Long.valueOf(System.currentTimeMillis())) + ".jppm");
      File f = new File(fileName);
      try
      {
        outWriter = new BufferedWriter(new FileWriter(f));
        outWriter.write("PerfMon");
        outWriter.newLine();
        outWriter.flush();
      }
      catch (IOException ex)
      {
    	  ex.printStackTrace();
      }
    }
  }
  
  private void addLine(String line)
  {
    try
    {
      String[] data = line.split(";");
      long time = Long.valueOf(data[0]).longValue();
      int type = Integer.valueOf(data[1]).intValue();
      double value = Double.valueOf(data[3]).doubleValue();
    }
    catch (Exception e)
    {
    	e.printStackTrace();
    }
  }
  
  public void loadFile(File file)
  {
    BufferedReader reader = null;
    try
    {
      reader = new BufferedReader(new FileReader(file));
      String line = reader.readLine();
      if ((line == null) || (!"PerfMon".equals(line)))
      {
        reportError(file.getAbsolutePath() + " is not a valid PerfMon file.");
      }
      else
      {

        line = reader.readLine();
        boolean isFileEmpty = true;
        while (line != null)
        {
          if (line.length() > 0)
          {
            isFileEmpty = false;
            addLine(line);
          }
          line = reader.readLine();
        }
        if (isFileEmpty) {
          reportError(file.getAbsolutePath() + " is empty.");
        }
      }
      return;
    }
    catch (Exception ex)
    {
      reportError("Failed to read " + file.getAbsolutePath() + ": " + ex.getMessage(), ex);
    }
    finally
    {
      try
      {
        reader.close();
      }
      catch (IOException ex)
      {
        reportError("Failed to close " + file.getAbsolutePath(), ex);
      }
    }
  }
  
  private static synchronized void writeRecord(String line)
  {
    try
    {
      if (outWriter != null)
      {
        outWriter.write(line);
        outWriter.newLine();
        outWriter.flush();
      }
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }
  
  private void addPerfRecord(String label, double value)
  {
      long now = System.currentTimeMillis();
      StringBuilder builder = new StringBuilder();
      builder.append(now);
      builder.append(";");
      builder.append(this.monitorType);
      builder.append(";");
      builder.append(label);
      builder.append(";");
      builder.append(value);
      writeRecord(builder.toString());
  }
  
  private void reportError(String msg)
  {
      System.out.println("Perfmon plugin error: " + msg);
  }
  
  private void reportError(String string, Exception ex)
  {
    reportError(string);
  }
  
  public void run()
  {
    while (this.testIsRunning) {
      try
      {
        processConnectors();
        Thread.sleep(1000L);
      }
      catch (Exception e)
      {
    	  e.printStackTrace();
        this.testIsRunning = false;
      }
    }
  }
  
  private void processConnectors()
    throws IllegalArgumentException
  {
    for (int i = 0; i < this.connectors.length; i++) {
      if (this.connectors[i] != null) {
        try
        {
          boolean success = true;
          switch (this.monitorType)
          {
          case 0: 
            success = addCPURecord(this.connectors[i]);
            break;
          case 1: 
            success = addMemRecord(this.connectors[i]);
            break;
          case 2: 
            success = addSwapRecord(this.connectors[i]);
            break;
          case 3: 
            success = addDisksIORecord(this.connectors[i]);
            break;
          case 4: 
            success = addNetworkRecord(this.connectors[i]);
            break;
          default: 
            throw new IllegalArgumentException("Unhandled perfmon type:" + this.monitorType);
          }
          if (!success) {
            if (this.testIsRunning)
            {
              reportError("Connection lost with '" + this.connectors[i].getHost() + "'!");
              this.connectors[i] = null;
            }
          }
        }
        catch (PerfMonException e)
        {
          reportError(e.getMessage());
          this.connectors[i] = null;
        }
      }
    }
  }
  
  private boolean addCPURecord(AgentConnector connector)
    throws PerfMonException
  {
    double value = (100.0D * connector.getCpu());
    if (value >= 0L)
    {
      addPerfRecord(connector.getRemoteServerName(), value);
      return true;
    }
    return false;
  }
  
  private boolean addMemRecord(AgentConnector agentConnector)
    throws PerfMonException
  {
    double value = agentConnector.getMem() / 1048576.0D;
    if (value >= 0.0D)
    {
      addPerfRecord(agentConnector.getRemoteServerName(), value);
      return true;
    }
    return false;
  }
  
  private boolean addSwapRecord(AgentConnector agentConnector)
    throws PerfMonException
  {
    long[] values = agentConnector.getSwap();
    if ((values[0] == -2L) || (values[1] == -2L)) {
      return false;
    }
    String keyPageIn = agentConnector.getRemoteServerName() + " (in)";
    String keyPageOut = agentConnector.getRemoteServerName() + " (out)";
    if ((this.oldValues.containsKey(keyPageIn)) && (this.oldValues.containsKey(keyPageOut)))
    {
      addPerfRecord(keyPageIn, values[0] - ((Long)this.oldValues.get(keyPageIn)).longValue());
      addPerfRecord(keyPageOut, values[1] - ((Long)this.oldValues.get(keyPageOut)).longValue());
    }
    this.oldValues.put(keyPageIn, new Long(values[0]));
    this.oldValues.put(keyPageOut, new Long(values[1]));
    return true;
  }
  
  private boolean addDisksIORecord(AgentConnector agentConnector)
    throws PerfMonException
  {
    long[] values = agentConnector.getDisksIO();
    if ((values[0] == -2L) || (values[1] == -2L)) {
      return false;
    }
    String keyReads = agentConnector.getRemoteServerName() + " (reads)";
    String keyWrites = agentConnector.getRemoteServerName() + " (writes)";
    if ((this.oldValues.containsKey(keyReads)) && (this.oldValues.containsKey(keyWrites)))
    {
      addPerfRecord(keyReads, values[0] - ((Long)this.oldValues.get(keyReads)).longValue());
      addPerfRecord(keyWrites, values[1] - ((Long)this.oldValues.get(keyWrites)).longValue());
    }
    this.oldValues.put(keyReads, new Long(values[0]));
    this.oldValues.put(keyWrites, new Long(values[1]));
    return true;
  }
  
  private boolean addNetworkRecord(AgentConnector agentConnector)
    throws PerfMonException
  {
    long[] values = agentConnector.getNetIO();
    if ((values[0] == -2L) || (values[1] == -2L)) {
      return false;
    }
    String keyReads = agentConnector.getRemoteServerName() + " (received)";
    String keyWrites = agentConnector.getRemoteServerName() + " (transfered)";
    if ((this.oldValues.containsKey(keyReads)) && (this.oldValues.containsKey(keyWrites)))
    {
      addPerfRecord(keyReads, (values[0] - ((Long)this.oldValues.get(keyReads)).longValue()) / 1024.0D);
      addPerfRecord(keyWrites, (values[1] - ((Long)this.oldValues.get(keyWrites)).longValue()) / 1024.0D);
    }
    this.oldValues.put(keyReads, new Long(values[0]));
    this.oldValues.put(keyWrites, new Long(values[1]));
    return true;
  }
  
  public void testStarted()
  {
    if (this.connectors != null)
    {
      AgentConnector connector = null;
      this.oldValues.clear();
      if (!this.testIsRunning)
      {
        for (int i = 0; i < this.connectors.length; i++) {
          try
          {
            connector = this.connectors[i];
            Socket sock = createSocket(connector.getHost(), connector.getPort());
            if (getConnectTimeout() > 0) {
              sock.setSoTimeout(getConnectTimeout());
            }
            connector.connect(sock);
          }
          catch (UnknownHostException e)
          {
            reportError("Unknown host exception occured. Please verify access to the server '" + connector.getHost() + "'.", e);
            this.connectors[i] = null;
          }
          catch (IOException e)
          {
            reportError("Unable to connect to server '" + connector.getHost() + "'. Please verify the agent is running on port " + connector.getPort() + ".", e);
            this.connectors[i] = null;
          }
          catch (PerfMonException e)
          {
            reportError(e.getMessage());
            this.connectors[i] = null;
          }
        }
        Thread t = new Thread(this);
        this.testIsRunning = true;
        t.start();
      }
    }
  }
  
  public void testEnded()
  {
    this.testIsRunning = false;

    if (this.connectors != null) {
      for (int i = 0; i < this.connectors.length; i++) {
        if (this.connectors[i] != null) {
          this.connectors[i].disconnect();
        }
      }
    }
    if (outWriter != null) {
      try
      {
        outWriter.flush();
        outWriter.close();
        outWriter = null;
      }
      catch (IOException ex)
      {
    	  ex.printStackTrace();
      }
    }
  }
  
  public int getConnectTimeout()
  {
    return this.connectTimeout;
  }
  
  public void setConnectTimeout(int connectTimeout)
  {
    this.connectTimeout = connectTimeout;
  }
  
  protected Socket createSocket(String host, int port)
    throws IOException
  {
    return new Socket(host, port);
  }
}
