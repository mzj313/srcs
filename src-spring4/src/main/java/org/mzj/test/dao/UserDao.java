package org.mzj.test.dao;

import org.mzj.test.po.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Long> {
	public User save(User accountInfo);

	public User findById(Long id);

	public Page<User> findByGroupIdGreaterThan(Long groupId, Pageable pageable);
}
