package org.phoenix.jmeter.perfmon;

public class PerfMonException extends Exception
{
	private static final long serialVersionUID = 1L;

public PerfMonException(String message, Throwable cause)
  {
    super(message, cause);
  }
  
  public PerfMonException(String message)
  {
    super(message);
  }
}
