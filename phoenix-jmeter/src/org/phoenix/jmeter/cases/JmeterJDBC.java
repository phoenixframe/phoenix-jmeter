package org.phoenix.jmeter.cases;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.jmeter.protocol.jdbc.AbstractJDBCTestElement;
import org.apache.jmeter.util.JMeterUtils;

/**
 * 使用jmeter做数据库测试示例
 * @author mengfeiyang
 *
 */
public class JmeterJDBC extends AbstractJDBCTestElement{
	
	private static final long serialVersionUID = 1L;

	public byte[] testJDBC() throws SQLException, ClassNotFoundException, UnsupportedEncodingException, UnsupportedOperationException, IOException{
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/yks?useUnicode=true&amp;characterEncoding=utf8", 
				"root", 
				"root"
				);
		JMeterUtils.getPropDefault("jdbcsampler.maxopenpreparedstatements", 10); 
		setQueryType("Select Statement");
		setQuery("Select * from t_node limit 0,2;");
		return execute(conn);
	}
	
	public static void main(String[] args) {
		JmeterJDBC j = new JmeterJDBC();
		try {
			String s = new String(j.testJDBC());
			String[] ss = s.split("\n");
			System.out.println(ss[1]);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
