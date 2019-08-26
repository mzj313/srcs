package org.mzj.test.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mzj.test.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
//@Lazy
public class CommService {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public String getServerTime() {
		return sdf.format(new Date());
	}
	
	@Autowired
	private ApplicationContext ctx;
	@Autowired
	private UserService userService;
	@Autowired
	private BaseDAO baseDAO;
	
	@Transactional
	public String save(int i) throws Exception {
		try {
			System.out.println("CommService.save------>");
			//从外部调用被拦截
//			return ((CommService) AopContext.currentProxy()).doSave(i);//aop需要设置expose-proxy
			return ((CommService) ctx.getBean("commService")).doSave(i);
			//内部调用不被拦截，doSave的@Transactional不起作用
//			return this.doSave(i);
		} finally {
			System.out.println("CommService.save------|");
		}
	}
	
	/**
	 * 
	 * @param positive 正数
	 * @return
	 * @throws Exception
	 */
	//只在这个地方加而上面save上不加注解，事务不起作用，内部调用
	@Transactional
	public String doSave(int positive) throws Exception {
		try {
			System.out.println("CommService.doSave-->");
			if(positive <= 0) {
				//默认只回滚RuntimeException和Error，见 DefaultTransactionAttribute.rollbackOn
				throw new Exception("must be positive");
			}
			return userService.save(positive);
		} finally {
			System.out.println("CommService.doSave--|");
		}
	}
	
	@Transactional
	public void update(String name) throws Exception {
		Map<String,String> params = new HashMap<String,String>();
//		params.put("name", name);
//		List<User> list = baseDAO.doQuery("user.findUsers",User.class,params);
//		if(list.isEmpty()) {
//			return;
//		}
//		User user = list.get(0);
//		user.setName(user.getName() + "_new");
//		userService.saveUser(user);
//		
//		params.clear();
//		String id = user.getId();
		Long id = 1l;
		params.put("id", "" + id);
		params.put("name", name);
		baseDAO.doUpdate("user.updateUser", params);
		
		User userNew = userService.findUserById(id);
		System.out.println(userNew);
	}
}
