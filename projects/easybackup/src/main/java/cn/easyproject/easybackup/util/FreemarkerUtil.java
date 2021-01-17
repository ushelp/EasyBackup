package cn.easyproject.easybackup.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Freemarker utils
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class FreemarkerUtil {
	
	static Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);

	public static String formatTemplate(String templateContent, Map<String, Object> data){
		if(templateContent==null){
			return "";
		}
		StringWriter writer = new StringWriter();    
        StringTemplateLoader stringLoader = new StringTemplateLoader();  
        stringLoader.putTemplate("myTemplate",templateContent);  
        cfg.setTemplateLoader(stringLoader);  
        try {  
            Template template = cfg.getTemplate("myTemplate","utf-8");  
            try {  
                template.process(data, writer);  
            } catch (TemplateException e) {  
                e.printStackTrace();  
            }    
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return writer.toString();
	}
	
	
	
}
