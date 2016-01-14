package org.phoenix.jmeter.cases;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.jmeter.protocol.http.sampler.HTTPHC4Impl;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;

public class HttpDataClient extends HTTPHC4Impl{

	protected HttpDataClient(HTTPSamplerBase testElement) {
		super(testElement);
		//HTTPSamplerFactory.getImplementations();
		//HTTPSamplerFactory.getImplementation(HTTPSamplerFactory.IMPL_JAVA, testElement);
	}
	
	public HTTPSampleResult getResourcesByGET(String url) throws MalformedURLException{
		HTTPSampleResult res = super.sample(new URL(url), HTTPSamplerBase.GET, false, 0);
		return res;
	}
	public HTTPSampleResult getResourcesByPOST(String url) throws MalformedURLException{
		HTTPSampleResult res = super.sample(new URL(url), HTTPSamplerBase.POST, false, 0);
		return res;
	}
	
	public static void main(String[] args) throws MalformedURLException {
    	HTTPSamplerBase httpBase = HTTPSamplerFactory.newInstance();
    	httpBase.setConnectTimeout("1000");
		HttpDataClient h = new HttpDataClient(httpBase);
		System.out.println(h.getResourcesByGET("http://www.baidu.com"));
	}

}
