package org.mzj.test.service;

import java.util.List;

import org.mzj.test.dao.UserDao;
import org.mzj.test.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
	/**
	 * @param age
	 *            [1, 150]
	 * @return
	 */
	@Transactional
	public String save(int age) {
		try {
			System.out.println("UserService.save-->");
			if (age <= 0 || age > 150) {
				// 默认只回滚RuntimeException和Error，见
				// DefaultTransactionAttribute.rollbackOn
				throw new RuntimeException("Out of range");
			}
			return "OK";
		} finally {
			System.out.println("UserService.save--|");
		}
	}

	@Autowired
	private UserDao userDao;

	@Transactional
	public User addUser(String name, Long groupId) {
		User user = new User();
		user.setName(name);
		user.setGroupId(groupId);
		return userDao.save(user);
	}
	
	@Transactional
	public User saveUser(User user) {
		return userDao.save(user);
	}

	public User findUserById(Long id) {
		return userDao.findById(id);
	}

	public List<User> findUserByGroupIdGreaterThan(Long groupId, Pageable pageable) {
		Page<User> users = userDao.findByGroupIdGreaterThan(groupId, pageable);
		return users.getContent();
	}
}
