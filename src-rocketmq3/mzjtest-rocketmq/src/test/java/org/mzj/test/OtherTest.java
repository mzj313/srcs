package org.mzj.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class OtherTest {

	@Test
	public void test01() {
		long bornTimestamp = 1516089082475l;
		long storeTimestamp = 1516089082494l;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
		System.out.println(sdf.format(new Date(bornTimestamp)));
		System.out.println(sdf.format(new Date(storeTimestamp)));
	}
	
	@Test
	public void test02() {
		long pre = 0l;
		for (int i = 0; i < 10000; i++) {
			long t = System.nanoTime();
			if (t == pre) {
				System.out.println(i+ ":" + t);
				throw new RuntimeException("重复");
			}
//			System.out.println(t);
			pre = t;
		}
	}
}
