package org.phoenix.jmeter.core;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.visualizers.gui.AbstractVisualizer;

public class ShowLog extends AbstractVisualizer{

	public ShowLog() {
		String s = ShowLog.class.getResource("/").getPath().replace("%20", " ")+"result.jtl";
		super.setFile(s);
		super.stateChanged(null);
	}
	public static void main(String[] args) {
		new ShowLog();
	}
	
	@Override
	public void add(SampleResult sample) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLabelResource() {
		// TODO Auto-generated method stub
		return null;
	}

}
