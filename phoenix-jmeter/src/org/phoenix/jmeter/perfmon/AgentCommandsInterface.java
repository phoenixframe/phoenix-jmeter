package org.phoenix.jmeter.perfmon;

public abstract interface AgentCommandsInterface
{
  public static final String BADCMD = "badCmd";
  public static final String BYE = "bye";
  public static final String CPU = "cpu";
  public static final String DISKIO = "dio";
  public static final String MEMORY = "mem";
  public static final String NAME = "name";
  public static final String NETWORK = "nio";
  public static final String SWAP = "swp";
  public static final String PID = "pid";
  public static final long SIGAR_ERROR = -1L;
  public static final long AGENT_ERROR = -2L;
  public static final long[] SIGAR_ERROR_ARRAY = { -1L, -1L };
  public static final long[] AGENT_ERROR_ARRAY = { -2L, -2L };
}
