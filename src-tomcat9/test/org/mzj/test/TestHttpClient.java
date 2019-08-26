package org.mzj.test;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

public class TestHttpClient {

	@Test
	public void test01() {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet("http://localhost:8080/web1/static/js/main.js");
			CloseableHttpResponse response = httpclient.execute(httpget);
			System.out.println(response.getProtocolVersion());
			System.out.println(response.getStatusLine().getStatusCode());
			System.out.println(response.getStatusLine().getReasonPhrase());
			System.out.println(response.getStatusLine().toString());
			HttpEntity entity = response.getEntity();
			
			InputStream is = entity.getContent();
			// 将is写入os
			FileOutputStream fos = new FileOutputStream("down.txt");
			int ch = 0;
			while ((ch = is.read()) != -1) {
				fos.write(ch);
			}
			fos.close();
			is.close();
			
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
