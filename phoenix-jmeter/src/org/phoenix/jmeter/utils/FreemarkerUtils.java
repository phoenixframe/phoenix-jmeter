package org.phoenix.jmeter.utils;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;

import org.apache.commons.io.output.FileWriterWithEncoding;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 通过freemarker生成能被jmeter识别的.jmx文件
 * @author mengfeiyang
 *
 */
public class FreemarkerUtils {
	
	/**
	 * 将模版转换成文件
	 * @param hashMap 
	 * @param isPrintOut 是否打印文件内容
	 */
	public static void transToFile(HashMap<String,Object> hashMap,boolean isPrintOut,String jmxFilePath,String templatePath,String templateName) {
		Configuration cfg = new Configuration();
		try {
				File jmxFile = new File(jmxFilePath);
				if(jmxFile.exists())jmxFile.delete();
				else jmxFile.createNewFile();
				cfg.setDirectoryForTemplateLoading(new File(templatePath));
				Template temp = cfg.getTemplate(templateName);
				temp.setEncoding("UTF-8");
				if(isPrintOut)temp.process(hashMap, new PrintWriter(System.out));
				temp.process(hashMap, new FileWriterWithEncoding(jmxFile, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
