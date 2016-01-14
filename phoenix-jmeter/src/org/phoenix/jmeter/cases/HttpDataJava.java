package org.phoenix.jmeter.cases;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.jmeter.protocol.http.sampler.HTTPJavaImpl;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;

public class HttpDataJava extends HTTPJavaImpl{

	protected HttpDataJava(HTTPSamplerBase base) {
		super(base);
	}
	
	public String getResources() throws MalformedURLException{
		HTTPSampleResult res = super.sample(new URL("http://www.baidu.com"), HTTPSamplerBase.GET, false, 0);
		return res.getResponseDataAsString();
	}
	
	public static void main(String[] args) throws MalformedURLException {
    	HTTPSamplerBase httpBase = HTTPSamplerFactory.newInstance();
    	httpBase.setConnectTimeout("1000");
		HttpDataJava httpJava = new HttpDataJava(httpBase);
		System.out.println(httpJava.getResources());
	}

}
