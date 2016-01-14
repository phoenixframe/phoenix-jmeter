package org.phoenix.jmeter.perfmon;

import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class PerfMonCollector implements Runnable{
  public static final List<String> metrics = Arrays.asList(new String[] { "CPU", "Memory", "Swap", "Disks I/O", "Network I/O" });
  private static boolean translateHostName = false;
  private static final Logger log = LoggingManager.getLoggerForClass();
  public static final String DATA_PROPERTY = "metricConnections";
  public String metricsResources;
  private Thread workerThread;
  private AgentConnector[] connectors = null;
  private HashMap<String, Long> oldValues = new HashMap<String, Long>();
  private String targetIP;
  private boolean rflag = false;
  public PerfMonCollector(String targetIP){
	  this.targetIP = targetIP;
  }
  
  public String getMetricsResources() {
	return metricsResources;
  }

	public void setMetricsResources(String metricsResources) {
		this.metricsResources = metricsResources;
	}

public synchronized void run()
  {
    while(rflag)
    {
      processConnectors();
      try
      {
        wait(1000L);
      }
      catch (InterruptedException ex)
      {
        log.debug("Monitoring thread was interrupted", ex);
      }
    }
  }
  
  public int testStarted()
  {

    try {
		initiateConnectors();
		rflag = true;
	    this.workerThread = new Thread(this);
	    this.workerThread.start();
	    return 1;
	} catch (Exception e) {
		rflag = false;
		return 0;
		//e.printStackTrace();
	}
  }
  
  public void testEnded()
  {
	rflag = false;
    this.workerThread.interrupt();
    shutdownConnectors();
  }
  
  private void initiateConnectors() throws Exception
  {
    this.oldValues.clear();
    this.connectors = new AgentConnector[0];
    
    this.connectors = new AgentConnector[metrics.size()];
    for (int i = 0; i < this.connectors.length; i++)
    {
      String metric = metrics.get(i);
      
      AgentConnector connector = new AgentConnector(targetIP, 4444);
      connector.setMetricType(metric);
      Socket sock = createSocket(connector.getHost(), connector.getPort());
      connector.connect(sock);
      this.connectors[i] = connector;
    }
  }
  
  private void shutdownConnectors()
  {
    for (int i = 0; i < this.connectors.length; i++) {
      if (this.connectors[i] != null) {
        this.connectors[i].disconnect();
      }
    }
  }
  
  protected Socket createSocket(String host, int port){
    try {
		return new Socket(host, port);
	} catch (Exception e) {
		 setMetricsResources(e.getMessage());
	}
    return null;
  }
  
  private void processConnectors()
  {
	  String r = "";
      String hostName  = "";
    for (int i = 0; i < this.connectors.length; i++) {
      if (this.connectors[i] != null)
      {
        if (translateHostName) {
          hostName = this.connectors[i].getRemoteServerName();
        } else {
          hostName = this.connectors[i].getHost();
        }
        String label = (String)AgentConnector.metrics.get(this.connectors[i].getMetricType());
        try
        {
        	r += label+":";
          switch (this.connectors[i].getMetricType())
          {
          case 0: 
            r += this.connectors[i].getCpu()/100.0D+"_";
            break;
          case 1: 
        	r += this.connectors[i].getMem() / 1048576.0D+"_";
            break;
          case 2: 
        	r += Arrays.toString(this.connectors[i].getSwap())+"_";
            break;
          case 3: 
        	r += Arrays.toString(this.connectors[i].getDisksIO())+"_";
            break;
          case 4: 
            r += Arrays.toString(this.connectors[i].getNetIO());
            break;
          default: 
            log.error("Unknown metric index: " + this.connectors[i].getMetricType());
          }
        }
        catch (PerfMonException e)
        {
          log.error(e.getMessage());
          this.connectors[i] = null;
        }
      }
    }
    setMetricsResources("hostName:"+hostName+"_"+r);
    System.out.println(getMetricsResources());
  }
  
  public static void main(String[] args) {
	  new PerfMonCollector("10.16.57.106").testStarted();
  }
}
