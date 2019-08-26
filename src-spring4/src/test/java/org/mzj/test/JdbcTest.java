package org.mzj.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class JdbcTest {

	@Test
	public void testDs() {
		Connection conn = null;
		PreparedStatement  pstmt = null;
		try {
			DriverManagerDataSource ds = new DriverManagerDataSource();
			ds.setDriverClassName("com.mysql.jdbc.Driver");
			ds.setUrl("jdbc:mysql://localhost:3306/testutf8mb4?useUnicode=true&amp;characterEncoding=UTF-8");
			ds.setUsername("root");
			ds.setPassword("root123");
			conn = ds.getConnection();
			
			pstmt = conn.prepareStatement("insert into test values(?)");
			pstmt.setString(1, "􀲔"); // Incorrect string value: '\xF4\x80\xB2\x94'
//			pstmt.setBytes(1, "􀲔".getBytes("GBK")); // ?
			doExecute(pstmt);
			pstmt.close();
			
			pstmt = conn.prepareStatement("select * from test");
			doExecute(pstmt);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
			} catch (SQLException e) {
			}
			try {
				if(conn != null) conn.close();
			} catch (SQLException e) {
			}
		}
	}

	//执行处理
	public static void doExecute(PreparedStatement pstmt) throws SQLException {
		if(pstmt.execute()){
			ResultSet rs = pstmt.getResultSet();
			int cols = rs.getMetaData().getColumnCount();
			StringBuffer result = new StringBuffer();
			while(rs.next()) {
				for (int i = 1; i <= cols; i++) {
					result.append(rs.getString(i)).append(" ");
				}
				result.append("\n");
			}
			System.out.println(result);
		} else {
			System.out.println(pstmt.getUpdateCount());
		}
	}
}
