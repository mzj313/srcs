package org.mzj.test;

import org.junit.Test;
import org.mzj.test.service.CommService;

public class TxTest extends TestBase{
	
	@Test
	public void testTx() {
		try {
			CommService commService = (CommService)ctx.getBean("commService");
			int[] param = {10, -1, 155};// {正常, Exception, RuntimeException}
			System.out.println(commService.save(param[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testTx2() {
		try {
			CommService commService = (CommService)ctx.getBean("commService");
			commService.update("aa");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
