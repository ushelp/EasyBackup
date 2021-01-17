package cn.easyproject.easybackup.util;

/**
 * Easy Util
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class EasyUtil {

	public static boolean isTrue(String value) {
		if(value==null){
			return false;
		}
		value=value.toLowerCase();
		return value.equals("true") ||
        value.equals("t") ||
        value.equals("1") ||
        value.equals("enabled") ||
        value.equals("y") ||
        value.equals("yes") ||
        value.equals("on");
	}
	
	
	public static boolean isNotEmpty(String str){
		return !(str==null||str.trim().equals(""));
	}
}
