package org.mzj.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JumpController {

	// http://localhost:8080/spring/welcome
	@RequestMapping("/welcome")
	public ModelAndView welcome(ModelAndView mv) {
		mv.addObject("user", "沐紫剑");
		mv.setViewName("welcome");
		return mv;
	}

	// 也可通过这种方式
	@RequestMapping("/welcome1")
	public String hello(Model mv) {
		mv.addAttribute("user", "沐紫剑");
		return "welcome";
	}

	// 不能通过这种方式
	@RequestMapping("/welcome2")
	public String hello(ModelAndView mv) {
		// 可以通过${modelAndView.model.user}来获取
		mv.addObject("user", "沐紫剑");
		// 新生成一个ModelAndView[model=[modelAndView=mv],view=welcome]
		return "welcome";
	}
}
