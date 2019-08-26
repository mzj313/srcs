package org.mzj.test.service;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@SuppressWarnings("all")
public class BaseDAO {
	private static final Logger logger = LoggerFactory.getLogger(BaseDAO.class);
//	@Autowired
	@PersistenceContext
	private EntityManager em;
	
	private boolean isLog;
	private Map<String, String> sqlMap = new HashMap<String, String>();
	private File[] sqlFiles;
	private long lastmodified;
	private Map<String, Long> lastmodifiedMap = new HashMap<String, Long>();
	
	@PostConstruct
	private void init() {
		logger.debug("开始初始化sql配置文件...");
		final File dir = new File(BaseDAO.class.getResource("/org/mzj/test/dao").getFile());
		System.out.println(dir.getAbsolutePath());
		sqlFiles = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if(name.startsWith("sql") && name.endsWith(".xml")) {
					return true;
				}
				return false;
			}
		});
		if(sqlFiles == null) return;
		logger.debug("sqlFiles:" + Arrays.asList(sqlFiles));
		loadSqls();
		
		
		new Thread(){
			public void run() {
				listening(dir.getAbsolutePath());
			};
		}.start();
	}
	
	private synchronized void loadSqls() {
		logger.debug("======== begin to load sqls ========");
//		sqlMap.clear();
		for(File sqlFile : sqlFiles) {
			sqlMap.putAll(loadSql(sqlFile));
		}
		logger.debug("======== load sqls finished ========");
	}
	
	private synchronized Map<String,String> loadSql(File f) {
		Map<String, String> sqlMap = new HashMap<String, String>();
		long lastmodified = f.lastModified();
		Long lastModifyTime = this.lastmodifiedMap.get(f.getPath());
		if(lastModifyTime != null && lastModifyTime == lastmodified) return sqlMap;
		Date date = new Date(lastmodified);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.debug("======loadSql " + f.getAbsolutePath() + " begin...");
		InputStream in = null;
		try {
			SAXReader reader = new SAXReader();
//			InputStream in = BaseDAO.class.getClassLoader().getResourceAsStream(sqlfile);
			in = new FileInputStream(f);
			Document doc = reader.read(in);
			// System.out.println(doc.asXML());
			Element root = doc.getRootElement();
			List<Element> childNodes = root.elements();
			for (Element e : childNodes) {
				List<Attribute> attrs = e.attributes();
				if (attrs == null || attrs.isEmpty()) {
					continue;
				}
				String name = null;
				for (Attribute attr : attrs) {
					if ("name".equals(attr.getName())) {
						name = attr.getValue();
						break;
					}
				}
				if (name != null) {
					if (sqlMap.containsKey(name)) {
						System.err.println("SQL " + name + " has exists!");
					} else {
						sqlMap.put(name, e.getTextTrim());
					}
				}
			}
			if(isLog) System.out.println(sqlMap);
			this.lastmodifiedMap.put(f.getPath(), lastmodified);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			logger.debug("======loadSql " + f.getAbsolutePath() + " end. " + sqlMap.keySet());
		}
		return sqlMap;
	}
	
	private void listening(String filepath) {
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			Path path = Paths.get(filepath);
			path.register(watcher,StandardWatchEventKinds.ENTRY_MODIFY);
			while(true){
	            WatchKey key = watcher.take();  
	            for(WatchEvent<?> event : key.pollEvents()){  
	                WatchEvent.Kind kind = event.kind();  
	                if(kind == StandardWatchEventKinds.OVERFLOW){//事件可能lost or discarded  
	                    continue;
	                }
	                WatchEvent<Path> e = (WatchEvent<Path>)event; 
	                //触发事件的文件
	                Path file = e.context(); 
	                if(kind.name().equals(StandardWatchEventKinds.ENTRY_MODIFY.name())){
	                	Thread.sleep(2000);
	                	loadSqls();
	                }
	            }
	            if(!key.reset()){  
	                break;  
	            }  
	        } 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSql(String name) {
		if(isLog) System.out.println("get SQL, name=" + name);
		String sql = sqlMap.get(name);
		Assert.assertNotNull("miss SQL, name=" + name, sql);
		return sql;
	}
	
	@Transactional
	public int doUpdate(String sqlname, Map<?, ?> params) {
		String sql = getSql(sqlname);
		Query query = em.createNativeQuery(sql);// org.hibernate.jpa.spi.AbstractQueryImpl
		System.out.println(sqlname + ": " + sql);
		if(params != null) {
			for(Object key : params.keySet()) {
				query.setParameter(String.valueOf(key), params.get(key));
			}
			System.out.println("params: " + params);
		}
		int result = query.executeUpdate();
		return result;
	}
	
	@Transactional
	public int doUpdate(String sqlname, Object... params) {
		String sql = getSql(sqlname);
		Query query = em.createNativeQuery(sql);
		logger.info("\n" + sqlname + ":\n" + sql);
		if(params != null) {
			for(int i=0;i<params.length;i++) {
				query.setParameter(i+1, params[i]);
			}
			logger.info("\nparams: " + Arrays.asList(params));
		}
		int result = query.executeUpdate();
		return result;
	}

	public List<Map> doQuery(String sqlname, Map<?, ?> params) {
		String sql = getSql(sqlname);
		Query query = em.createNativeQuery(sql);
		System.out.println(sqlname + ": " + sql);
		if(params != null) {
			for(Object key : params.keySet()) {
				query.setParameter(String.valueOf(key), params.get(key));
			}
			System.out.println("params: " + params);
		}
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map> resultList = query.getResultList();
		return resultList == null ? new ArrayList<Map>(0) : resultList;
	}
	
	public List<Map> doQuery(String sqlname, Object... params) {
		String sql = getSql(sqlname);
		Query query = em.createNativeQuery(sql);
		System.out.println(sqlname + ": " + sql);
		if(params != null) {
			for(int i=0;i<params.length;i++) {
				query.setParameter(i+1, params[i]);
			}
			System.out.println("params: " + Arrays.asList(params));
		}
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map> resultList = query.getResultList();
		return resultList == null ? new ArrayList<Map>(0) : resultList;
	}
	
	public <T> List<T> doQuery(String sqlname, Class clazz, Map<?, ?> params) {
		List<Map> mapList = (List<Map>) doQuery(sqlname, params);
		List resultList = mapList2voList(mapList, clazz);
		return resultList;
	}

	public <T> List<T> doQuery(String sqlname, Class clazz, Object... params) {
		List<Map> mapList = (List<Map>) doQuery(sqlname, params);
		List resultList = mapList2voList(mapList, clazz);
		return resultList;
	}
	
	public Page doQueryWithPage(String sqlname, int pageNum, int pageSize, Map<?, ?> params) {
		return doQueryWithPage(sqlname, null, pageNum, pageSize, params);
	}
	
	public Page doQueryWithPage(String sqlname, int pageNum, int pageSize, Object... params) {
		return doQueryWithPage(sqlname, null, pageNum, pageSize, params);
	}
	
	public Page doQueryWithPage(String sqlname, Class clazz, int pageNum, int pageSize, Map<?, ?> params) {
		return doQueryWithPage(sqlname, clazz, pageNum, pageSize, true, params);
	}
	
	public Page doQueryWithPage(String sqlname, Class clazz, int pageNum, int pageSize, boolean reasonable, Map<?, ?> params) {
		String sql = getSql(sqlname);
		
		String countsql = "select count(*) from (" + sql + ") t";
		Query countQuery = em.createNativeQuery(countsql);
		for(Object key : params.keySet()) {
			countQuery.setParameter(String.valueOf(key), params.get(key));
		}
		int total  = ((BigInteger) countQuery.getSingleResult()).intValue();
		logger.info("total=" + total);
		
		int pages = (int)Math.ceil(total / (double)pageSize);
		
		if(reasonable && pageNum > pages && pages > 0) pageNum = pages;//
		
		int start = (pageNum-1) * pageSize;
		sql += " limit " + start + "," + pageSize;
		Query query = em.createNativeQuery(sql);
		System.out.println(sqlname + ": " + sql);
		if(params != null) {
			for(Object key : params.keySet()) {
				query.setParameter(String.valueOf(key), params.get(key));
			}
			System.out.println("params: " + params);
		}
		List resultList = null;
		if(clazz != null) {
//			query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(clazz));
//			resultList = query.getResultList();
			query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map> mapList = query.getResultList();
			resultList = mapList2voList(mapList, clazz);
		} else {
			query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			resultList = query.getResultList();
		}
		
		Page page = new Page();
		page.setTotal(total);
		page.setPages(pages);
		page.setPageNum(pageNum);
		page.setPageSize(pageSize);
		page.setList(resultList == null ? new ArrayList(0) : resultList);
		return page;
	}
	
	public Page doQueryWithPage(String sqlname, Class clazz, int pageNum, int pageSize, Object... params) {
		return doQueryWithPage(sqlname, clazz, pageNum, pageSize, true, params);
	}
	
	public Page doQueryWithPage(String sqlname, Class clazz, int pageNum, int pageSize, boolean reasonable, Object... params) {
		String sql = getSql(sqlname);
		
		String countsql = "select count(*) from (" + sql + ") t";
		Query countQuery = em.createNativeQuery(countsql);
		for (int i = 0; i < params.length; i++) {
			countQuery.setParameter(i + 1, params[i]);
		}
		int total  = ((BigInteger) countQuery.getSingleResult()).intValue();
		System.out.println("total=" + total);
		
        int pages = (int)Math.ceil(total / (double)pageSize);
        
        if(reasonable && pageNum > pages) pageNum = pages;//
		
		int start = (pageNum-1) * pageSize;
		sql += " limit " + start + "," + pageSize;
		
		Query query = em.createNativeQuery(sql);
		System.out.println(sqlname + ": " + sql);
		if(params != null) {
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i + 1, params[i]);
			}
			System.out.println("params: " + Arrays.asList(params));
		}
		List resultList = null;
		if(clazz != null) {
//			query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(clazz));
//			resultList = query.getResultList();
			query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map> mapList = query.getResultList();
			resultList = mapList2voList(mapList, clazz);
		} else {
			query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			resultList = query.getResultList();
		}
		
		Page page = new Page();
		page.setTotal(total);
		page.setPages(pages);
		page.setPageNum(pageNum);
		page.setPageSize(pageSize);
		page.setList(resultList == null ? new ArrayList(0) : resultList);
		return page;
	}
	
	public static <T> T map2vo(Map<String, Object> map, T obj) {
			PropertyDescriptor[] propertyDescriptors;
			try {
				BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
				propertyDescriptors = beanInfo.getPropertyDescriptors();
			} catch (IntrospectionException e) {
				e.printStackTrace();
				return obj;
			}

			for (PropertyDescriptor property : propertyDescriptors) {
				String name = property.getName();
				String key = addUnderline(name);
				Object value = null;
				if (map.containsKey(name)) {
					value = map.get(name);
				} else if (map.containsKey(key)) {
					value = map.get(key);
				}
				if(value == null) continue;
				// 得到property对应的setter方法
				Method setter = property.getWriteMethod();
				String paramType = setter.getParameterTypes()[0].getName();
				try {
					if("java.lang.Long".equals(paramType) || "long".equals(paramType)) {
						setter.invoke(obj, Long.valueOf("" + value));
					} else if("java.lang.Integer".equals(paramType)) {
						setter.invoke(obj, Integer.valueOf("" + value));
					} else {
						setter.invoke(obj, value);
					}
				} catch (Exception e) {
					System.err.println(setter.getName() + "(" + paramType + " " + value + ")");
					e.printStackTrace();
				}
			}
		return obj;
	}

	//userId -> user_id
	public static String addUnderline(String name) {
		StringBuffer keyBuf = new StringBuffer();
		char[] chars = name.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if(Character.isUpperCase(chars[i])) {
				keyBuf.append("_").append(Character.toLowerCase(chars[i]));
			} else {
				keyBuf.append(chars[i]);
			}
		}
		String key = keyBuf.toString();
		return key;
	}
	
	public static List mapList2voList(List<Map> mapList, Class clazz) {
		List resultList = new ArrayList(mapList.size());
		try {
			for(Map map : mapList) {
				Object obj = clazz.newInstance();
				map2vo(map, obj);
				resultList.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	public static void main(String[] args) {
		BaseDAO d = new BaseDAO();
		d.isLog = true;
		d.init();
		
		try {
			Thread.sleep(1000*60);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
