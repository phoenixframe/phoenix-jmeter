package org.phoenix.jmeter.core;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.jmeter.testelement.TestStateListener;
import org.phoenix.jmeter.utils.FlushQueue;
import org.phoenix.jmeter.utils.FreemarkerUtils;
import org.phoenix.model.PhoenixJmeterBean;

/**
 * jmeter插件
 * @author mengfeiyang
 *
 */
public class PhoenixJmeterLauncher {
	private TestStateListener stateListener;
	public PhoenixJmeterLauncher(TestStateListener stateListener){
		this.stateListener = stateListener;
	}

    public int runTests(PhoenixJmeterBean jmeterModel,String JTLFilePath,String jmxFilePath,String templatePath,String templateName){
    	try{
	    	int maxThread = Integer.parseInt(jmeterModel.getNumThreads());
	    	LinkedBlockingQueue<String> linkedQueue = new LinkedBlockingQueue<String>(maxThread);
	    	FlushQueue.getInstance().setLinkedQueue(linkedQueue);
	    	FlushQueue.getInstance().setSize(maxThread);
			FreemarkerUtils.transToFile(jmeterModel.toHashMap(),false,jmxFilePath,templatePath,templateName);
			File JTLFile = new File(JTLFilePath);
			if(JTLFile.exists())JTLFile.delete();
			JTLFile.createNewFile();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
	    	String[] params = new String[]{"-n","-t",jmxFilePath,"-l",JTLFilePath};
	    	PhoenixJmeter phoenixJmeter = new PhoenixJmeter();
	    	phoenixJmeter.setStateListener(stateListener);
	    	phoenixJmeter.start(params);
    	} catch(Exception ee){
    		return 0;
    	}
        return 1;
    }
    
    public static void main(String[] args) {
		StateListener sl = new StateListener();
		PhoenixJmeterLauncher pj = new PhoenixJmeterLauncher(sl);
		PhoenixJmeterBean model = new PhoenixJmeterBean();
		model.setCaseName("百度测试");
		model.setNumThreads("3");
		model.setRampTime("0");
		model.setScheduler("false");
		model.setSampleErrorControl("continue");
		model.setContinueForever("false");
		model.setControllerLoops("10");
		
		model.setStartTime("1111");
		model.setEndTime("22222");
		model.setDuration("10");
		model.setDelayTime("0");
		model.setDelayedStart("false");
		
		model.setDomainURL("www.baidu.com");
		model.setConnectTimeOut("1000");
		model.setResponseTimeOut("1000");
		model.setContentEncoding("UTF-8");
		model.setRequestProtocol("http");
		model.setRequestMethod("GET");
		
		model.setCheckPointType("2");
		model.setCheckPointValue("百度一下");
		
		model.setClearCache("false");
		
		model.setEmailAttemper("false");
		model.setSuccessLimit("2");
		model.setFailureLimit("2");
		//model.setSuccessSubject("");
		//model.setFailureSubject("");
		model.setFromAddress("fromaddress");
		model.setSmtpHost("1111");
		model.setSendTo("2222");
		model.setEmailServerLoginName("333");
		model.setEmailServerLoginPassword("");
		model.setAuthType("SSL");
		
		String path = PhoenixJmeterLauncher.class.getResource("/").getPath().replace("%20", " ");
		pj.runTests(model, path+"result.jtl", path+"phoenix.jmx", path, "phoenix.ftl");
		
		
	}
}
