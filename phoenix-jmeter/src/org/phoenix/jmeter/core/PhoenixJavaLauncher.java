package org.phoenix.jmeter.core;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.jmeter.testelement.TestStateListener;
import org.phoenix.jmeter.utils.FlushQueue;
import org.phoenix.jmeter.utils.FreemarkerUtils;
import org.phoenix.model.PhoenixJmeterBean;

public class PhoenixJavaLauncher {
	private TestStateListener stateListener;
	public PhoenixJavaLauncher(TestStateListener stateListener){
		this.stateListener = stateListener;
	}

    public int runTests(PhoenixJmeterBean javaModel,String JTLFilePath,String jmxFilePath,String templatePath,String templateName){
    	int maxThread = Integer.parseInt(javaModel.getNumThreads());
    	LinkedBlockingQueue<String> linkedQueue = new LinkedBlockingQueue<String>(maxThread);
    	FlushQueue.getInstance().setLinkedQueue(linkedQueue);
    	FlushQueue.getInstance().setSize(maxThread);
		FreemarkerUtils.transToFile(javaModel.toHashMap(),false,jmxFilePath,templatePath,templateName);
		File JTLFile = new File(JTLFilePath);
		if(JTLFile.exists())JTLFile.delete();
		try {
			JTLFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
    	String[] params = new String[]{"-n","-t",jmxFilePath,"-l",JTLFilePath};
    	PhoenixJmeter phoenixJmeter = new PhoenixJmeter();
    	phoenixJmeter.setStateListener(stateListener);
    	phoenixJmeter.start(params);
        return 1;
    }
    
    public static void main(String[] args) {
		StateListener sl = new StateListener();
		PhoenixJavaLauncher pj = new PhoenixJavaLauncher(sl);
		PhoenixJmeterBean model = new PhoenixJmeterBean();
		model.setNumThreads("3");
		model.setRampTime("0");
		model.setScheduler("false");
		model.setSampleErrorControl("continue");
		model.setContinueForever("false");
		model.setControllerLoops("5");
		
		model.setStartTime("1111");
		model.setEndTime("22222");
		model.setDuration("100");
		model.setDelayTime("0");
		
		model.setClassName("org.phoenix.jmeter.cases.HttpBaseCase");
		
		model.setConnectTimeOut("1000");
		model.setRequestMethod("get");
		model.setDomainURL("http://www.baidu.com");
		
		FlushQueue.getInstance().getHashMap().put("phoenixJmeterModel", model);
		String path = PhoenixJmeterLauncher.class.getResource("/").getPath().replace("%20", " ");
		pj.runTests(model, path+"result.jtl", path+"phoenixjava.jmx", path, "phoenixjava.ftl");
	}
}
