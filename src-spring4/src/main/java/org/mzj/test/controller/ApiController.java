package org.mzj.test.controller;

import org.mzj.test.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class ApiController {
	@Autowired
	private CommService commService;

	// http://localhost:8080/spring/api/time
	@RequestMapping("/time")
	public String getServerTime(@RequestParam(value = "msg", defaultValue = "ok", required = false) String msg) {
		return "服务器时间：" + commService.getServerTime() + " " + msg;
	}
}
