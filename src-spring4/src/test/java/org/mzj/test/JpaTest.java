package org.mzj.test;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jpa.AvailableSettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mzj.test.po.User;

public class JpaTest {
	EntityManagerFactory emf;
	
	@Before
	public void setUp() {
		// 文件META-INF/persistence.xml必须存在(参照PersistenceXmlParser.doResolve)
		Map<Object, Object> props = new HashMap<Object, Object>();
		props.put(AvailableSettings.PROVIDER, "org.hibernate.ejb.HibernatePersistence");
		props.put(AvailableSettings.JDBC_DRIVER, "com.mysql.jdbc.Driver");
		props.put(AvailableSettings.JDBC_URL, "jdbc:mysql://127.0.0.1:3306/test1");
		props.put(AvailableSettings.JDBC_USER, "root");
		props.put(AvailableSettings.JDBC_PASSWORD, "root123");
		props.put("hibernate.show_sql", "true");
		// 这里persistenceUnitName对应persistence.xml文件中persistence-unit的name
		// 如果找不到会报 PersistenceException: No Persistence provider for EntityManager named
		emf = Persistence.createEntityManagerFactory("mzjtest", props);
	}
	
	@After
	public void tearDown() {
		if(emf != null) emf.close();
	}

	@Test
	public void testJpa() {
		try {
			EntityManager em = emf.createEntityManager();
			User u = new User();
			u.setId(1);
			u.setName("zhang3");
			em.persist(u);
			User u2 = em.find(User.class, "1");
			System.out.println(u2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testHibernate() {
		try {
			Configuration cf = new Configuration().configure("META-INF/hibernate.cfg.xml");
			SessionFactory sf = cf.buildSessionFactory();
			Session session = sf.openSession();
			
			User u = new User();
			u.setId(1L);
			u.setName("zhang3");
			Transaction trans = session.beginTransaction();
			trans.begin();
			session.persist(u);
			trans.commit();
			
			User u2 = session.get(User.class, 1L);
			System.out.println(u2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
