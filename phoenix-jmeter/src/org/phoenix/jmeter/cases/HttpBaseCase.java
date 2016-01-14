package org.phoenix.jmeter.cases;

import java.net.MalformedURLException;

import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.phoenix.jmeter.utils.FlushQueue;
import org.phoenix.model.PhoenixJmeterBean;

/**
 * Http并发测试实例
 * @author mengfeiyang
 *
 */
public class HttpBaseCase extends AbstractJavaSamplerClient{
	
    @Override
	public SampleResult runTest(JavaSamplerContext arg0) {
		HTTPSampleResult result = null;
		PhoenixJmeterBean resModel = (PhoenixJmeterBean) FlushQueue.getInstance().getHashMap().get("phoenixJmeterModel");
		HTTPSamplerBase httpBase = HTTPSamplerFactory.newInstance();
		httpBase.setConnectTimeout(resModel.getConnectTimeOut());
		HttpDataClient httpClient = new HttpDataClient(httpBase);
		try {
			if(resModel.getRequestMethod().equalsIgnoreCase("GET"))result = httpClient.getResourcesByGET(resModel.getDomainURL());
			else if (resModel.getRequestMethod().equalsIgnoreCase("post"))result = httpClient.getResourcesByPOST(resModel.getDomainURL());
			else result = httpClient.getResourcesByGET(resModel.getDomainURL());
			if(result.getResponseDataAsString().contains("百度一下"))result.setSuccessful(true);
			//System.out.println(Thread.currentThread().getName()+" "+result.getBodySize()+" "+result.getResponseCode()+" "+result.getConnectTime()+" "+result.isResponseCodeOK());
			FlushQueue.getInstance().offer(Thread.currentThread().getName()+" "+result.getBodySize()+" "+result.getResponseCode()+" "+result.getConnectTime()+" "+result.isResponseCodeOK());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return result;
	}
}