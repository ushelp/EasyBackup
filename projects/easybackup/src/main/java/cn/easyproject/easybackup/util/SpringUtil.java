package cn.easyproject.easybackup.util;

import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * Spring 工具类
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class SpringUtil {
	public static ClassPathXmlApplicationContext applicationContext=new ClassPathXmlApplicationContext("applicationContext.xml");
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String id){
		return (T) applicationContext.getBean(id);
	}
	public static <T> T get(Class<T> c){
		return applicationContext.getBean(c);
	}
}
