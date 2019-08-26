package org.mzj.test.utils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
	private static Map<Class, Logger> classLoggerMap = new HashMap<Class, Logger>();

	public static Logger getLogger(Object obj) {
		Class<? extends Object> clazz = obj.getClass();
		Logger logger = classLoggerMap.get(clazz);
		if (logger == null) {
			synchronized (classLoggerMap) {
				if (logger == null) {
					logger = LoggerFactory.getLogger(clazz);
					System.out.println("load " + clazz.getName() + " -> " + logger);
					classLoggerMap.put(clazz, logger);
				}
			}
		}
		return logger;
	}

	public static String toString(Object obj) {
		if(obj == null) {
			return "";
		}
		StringBuffer strBuf = new StringBuffer();
		Class clazz = obj.getClass();
		// 基本数据类型直接返回
		if (clazz == Integer.class || clazz == Short.class || clazz == Byte.class || clazz == Long.class || clazz == Double.class
				|| clazz == Float.class || clazz == Boolean.class || clazz == String.class || clazz == Character.class) {
			strBuf.append(obj);
			return strBuf.toString();
		}

		// 对数组类型的处理
		if (clazz.isArray()) {
			strBuf.append("[");
			for (int i = 0; i < Array.getLength(obj); i++) {
				if (i > 0)
					strBuf.append(",");
				Object val = Array.get(obj, i);

				if (val != null && !val.equals("")) {
					strBuf.append(toString(val));
				}
			}
			strBuf.append("]");
			return strBuf.toString();
		}
		
		if(clazz == Map.class) {
			
		}

		// 获取所有属性
		Field[] fields = clazz.getDeclaredFields();

		// 设置所有属性方法可访问
		AccessibleObject.setAccessible(fields, true);

		strBuf.append("[");
		for (int i = 0; i < fields.length; i++) {
			Field fd = fields[i];
			if("serialVersionUID".equals(fd.getName())) {
				continue;
			}
			strBuf.append(fd.getName() + "=");
			try {
				if (!fd.getType().isPrimitive() && fd.getType() != String.class) {
//					strBuf.append(toString(fd.get(obj)));
					strBuf.append(fd.get(obj));
				} else {
					strBuf.append(fd.get(obj));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (i != fields.length - 1)
				strBuf.append(",");
		}

		strBuf.append("]");
		return strBuf.toString();
	}

	public static void main(String[] args) {
		LogUtil.getLogger(new LogUtil()).info(LogUtil.toString("ss"));
		LogUtil.getLogger(new LogUtil()).info(LogUtil.toString(Arrays.asList("ss")));
	}
}
