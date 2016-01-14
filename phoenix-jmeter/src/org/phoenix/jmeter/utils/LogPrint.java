package org.phoenix.jmeter.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * 打印日志文件全部内容
 * @author mengfeiyang
 *
 */
public class LogPrint {
	
	public static String print(String logPath){
		String content = null;
		try {
			content = FileUtils.readFileToString(new File(logPath), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public static String printBr(String logPath){
		StringBuilder strs = new StringBuilder();
		try {
			File file = new File(logPath);
			if(file.length() > 0x3938700){//如果大于60M
				strs.append("文件过大，不予显示。文件位于："+logPath);
			} else {
				List<String> ll = FileUtils.readLines(file, "UTF-8");
				for(String l : ll){
					strs.append(l).append("<br>");
				}
			}
			return strs.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String println(String logPath){
		StringBuilder strs = new StringBuilder();
		try {
			List<String> ll = FileUtils.readLines(new File(logPath), "UTF-8");
			for(String l : ll){
				strs.append(l).append("\r\n");
			}
			return strs.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
