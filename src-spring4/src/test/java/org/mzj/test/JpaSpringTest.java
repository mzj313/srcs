package org.mzj.test;

import org.junit.Test;
import org.mzj.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class JpaSpringTest extends TestBase {
	@Autowired
	private UserService userService;

	@Test
	public void testAddUser() {
		try {
			userService.addUser("lisi", 1L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
