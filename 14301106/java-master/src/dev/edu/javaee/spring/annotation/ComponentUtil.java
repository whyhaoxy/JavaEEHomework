package dev.edu.javaee.spring.annotation;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dev.edu.javaee.spring.bean.BeanDefinition;
import dev.edu.javaee.spring.bean.PropertyValues;

public class ComponentUtil {
	
	
	private List<String> beanNameList = null;
	private String rootPath = System.getProperty("user.dir") + "/src/test/edu/javaee/spring";
	

	public List<String> getAutoCreateBeanName() {
		return this.beanNameList;
	}
	
	public List<BeanDefinition> ComponentCreateBean() {

		
		List<BeanDefinition> beanDefineList = new ArrayList<BeanDefinition>();
		beanNameList = new ArrayList<String>();

		File file = new File(rootPath);
		if (file.isDirectory()) {

			String files[] = file.list();
			for (String filename : files) {
			
				BeanDefinition beandef = new BeanDefinition();
				Component annotationTmp = null;
				String thePath = "test.edu.javaee.spring" + "." + filename.substring(0, filename.length() - 5);
				beandef.setBeanClassName(thePath);
				Class<?> beanClass = null;
				try {
					beanClass = Class.forName(thePath);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				beandef.setBeanClass(beanClass);
				if ((annotationTmp = (Component) beanClass.getAnnotation(Component.class)) != null) {
				
					this.beanNameList.add(annotationTmp.value());
					Object bean = null;
					try {
						bean = beanClass.newInstance();
						beandef.setBean(bean);

					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					beanDefineList.add(beandef);
				} 
			}
		}
		return beanDefineList;

	}

}
