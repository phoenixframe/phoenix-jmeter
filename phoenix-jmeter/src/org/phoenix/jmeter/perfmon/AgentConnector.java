package org.phoenix.jmeter.perfmon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class AgentConnector
  implements AgentCommandsInterface
{
  public static final List<String> metrics = Arrays.asList(new String[] { "CPU", "Memory", "Swap", "Disks I/O", "Network I/O" });
  public static final int PERFMON_CPU = 0;
  public static final int PERFMON_MEM = 1;
  public static final int PERFMON_SWAP = 2;
  public static final int PERFMON_DISKS_IO = 3;
  public static final int PERFMON_NETWORKS_IO = 4;
  private static final Logger log = LoggingManager.getLoggerForClass();
  private String host;
  private int port;
  private Socket socket = null;
  private PrintWriter out = null;
  private BufferedReader in = null;
  private String remoteServerName = null;
  private int metricType;
  
  public AgentConnector(String host, int port)
  {
    this.host = host;
    this.port = port;
  }
  
  public void connect(Socket aSocket)
    throws UnknownHostException, IOException, PerfMonException
  {
    this.socket = aSocket;
    this.out = new PrintWriter(this.socket.getOutputStream(), true);
    this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    
    this.remoteServerName = getData("name");
  }
  
  public void disconnect()
  {
    try
    {
      if (this.out != null)
      {
        this.out.println("bye");
        this.out.close();
        this.in.close();
        this.socket.close();
      }
    }
    catch (IOException e)
    {
      log.error("Exception disconnecting agent:", e);
    }
  }
  
  private String getData(String data)
    throws PerfMonException
  {
    this.out.println(data);
    String ret = null;
    try
    {
      ret = this.in.readLine();
    }
    catch (IOException ex)
    {
      log.error("Error receiving data", ex);
      throw new PerfMonException("Connection lost with '" + this.host + "'!", ex);
    }
    log.debug("Read " + data + "=" + ret);
    return ret;
  }
  
  private void throwNotSupportedMetricException(String metric)
    throws PerfMonException
  {
    throw new PerfMonException("Getting " + metric + " metrics is not supported by Sigar API on this operating system...");
  }
  
  public long getMem()
    throws PerfMonException
  {
    long ret = -1L;
    
    String value = getData("mem");
    if (value != null) {
      ret = Long.parseLong(value);
    }
    if (ret <= 0L) {
      throwNotSupportedMetricException("memory");
    }
    return ret;
  }
  
  public double getCpu()
    throws PerfMonException
  {
    double ret = -1.0D;
    
    String value = getData("cpu");
    if (value != null) {
      ret = Double.parseDouble(value);
    }
    if (ret < 0.0D) {
      throwNotSupportedMetricException("cpu");
    } 
    return ret;
  }
  
  public long[] getSwap()
    throws PerfMonException
  {
    long[] ret = { -1L, -1L };
    String value = getData("swp");
    if (value != null)
    {
      ret[0] = Long.parseLong(value.substring(0, value.indexOf(58)));
      ret[1] = Long.parseLong(value.substring(value.indexOf(58) + 1));
    }
    if ((ret[0] < 0L) || (ret[1] < 0L)) {
      throwNotSupportedMetricException("swap");
    }
    return ret;
  }
  
  public long[] getDisksIO()
    throws PerfMonException
  {
    long[] ret = { -1L, -1L };
    String value = getData("dio");
    if (value != null)
    {
      ret[0] = Long.parseLong(value.substring(0, value.indexOf(58)));
      ret[1] = Long.parseLong(value.substring(value.indexOf(58) + 1));
    }
    if ((ret[0] < 0L) || (ret[1] < 0L)) {
      throwNotSupportedMetricException("disks I/O");
    }
    return ret;
  }
  
  public long[] getNetIO()
    throws PerfMonException
  {
    long[] ret = { -1L, -1L };
    String value = getData("nio");
    if (value != null)
    {
      ret[0] = Long.parseLong(value.substring(0, value.indexOf(58)));
      ret[1] = Long.parseLong(value.substring(value.indexOf(58) + 1));
    }
    if ((ret[0] < 0L) || (ret[1] < 0L)) {
      throwNotSupportedMetricException("network I/O");
    }
    return ret;
  }
  
  public String getRemoteServerName()
  {
    return this.remoteServerName;
  }
  
  public String getHost()
  {
    return this.host;
  }
  
  public int getPort()
  {
    return this.port;
  }
  
  public void setMetricType(String metric)
  {
    this.metricType = metrics.indexOf(metric);
  }
  
  public int getMetricType()
  {
    return this.metricType;
  }
}
