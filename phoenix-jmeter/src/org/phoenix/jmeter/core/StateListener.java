package org.phoenix.jmeter.core;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.testelement.TestStateListener;
import org.phoenix.jmeter.utils.FlushQueue;

public class StateListener implements TestStateListener{

	@Override
	public void testStarted() {
		System.out.println("测试开始................");
		
	}

	@Override
	public void testStarted(String host) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testEnded() {
		FlushQueue.getInstance().releaseQueue();
		StandardJMeterEngine.stopEngine();
		System.out.println("测试结束................");		
	}

	@Override
	public void testEnded(String host) {
		// TODO Auto-generated method stub
		
	}

}
