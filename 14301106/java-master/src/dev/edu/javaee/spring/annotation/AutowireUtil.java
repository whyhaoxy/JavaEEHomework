package dev.edu.javaee.spring.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dev.edu.javaee.spring.bean.BeanDefinition;
import dev.edu.javaee.spring.bean.BeanUtil;
import dev.edu.javaee.spring.bean.PropertyValue;
import dev.edu.javaee.spring.factory.BeanFactory;
import dev.edu.javaee.spring.factory.XMLBeanFactory;
import dev.edu.javaee.spring.resource.LocalFileResource;
import test.edu.javaee.spring.*;

public class AutowireUtil {

	public static List<BeanDefinition> CreateAutowireBean(
			List<BeanDefinition> leftBeanList,
			List<BeanDefinition> TotalBeanList) {

		List<BeanDefinition> TheReturnList = new ArrayList<BeanDefinition>();
		for (int i = 0; i < leftBeanList.size(); i++) {
			Class<?> beanClass = null;
			beanClass = leftBeanList.get(i).getBeanClass();
			BeanDefinition beanDefinition = leftBeanList.get(i);

			Constructor con = beanClass.getConstructors()[0];

			Object bean = getObjBean(con, TotalBeanList);

			// 设置属性
			Field[] field = bean.getClass().getDeclaredFields();
			setField(field, TotalBeanList);
			// 设置方法
			Method[] method = bean.getClass().getDeclaredMethods();
			setMethod(method, field, bean, TotalBeanList);

			List<PropertyValue> fieldDefinitionList = beanDefinition
					.getPropertyValues().GetPropertyValues();
			for (PropertyValue propertyValue : fieldDefinitionList) {
				BeanUtil.invokeSetterMethod(bean, propertyValue.getName(),
						propertyValue.getValue());
			}
			beanDefinition.setBean(bean);
			TheReturnList.add(beanDefinition);
		}
		return TheReturnList;
	}

	public static Object getObjBean(Constructor con,
			List<BeanDefinition> TotalBeanList) {
		Object bean = null;

		Class[] type = con.getParameterTypes();
		int length = type.length;
		Object obj[] = new Object[length];
		if (con.isAnnotationPresent(Autowired.class)) {
			Iterator it = TotalBeanList.iterator();

			while (it.hasNext()) {
				BeanDefinition bd = (BeanDefinition) it.next();

				for (int k = 0; k < length; k++) {
					if (bd.getBean().getClass().equals(type[k])) {
						obj[k] = bd.getBean();
						continue;
					}
				}
			}

			try {
				bean = con.newInstance(obj);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return bean;

	}

	public static void setField(Field[] field,
			List<BeanDefinition> TotalBeanList) {
		for (int k = 0; k < field.length; k++) {
			field[k].setAccessible(true);
			if (field[k].isAnnotationPresent(Autowired.class)) {

				Autowired auto = field[k].getAnnotation(Autowired.class);
				Iterator ter = TotalBeanList.iterator();

				while (ter.hasNext()) {
					BeanDefinition bean12 = (BeanDefinition) ter.next();

					Class<?> cl = (Class<?>) field[k].getGenericType();

					if (bean12.getBean().getClass().isAssignableFrom(cl)) {
						try {
							field[k].set(auto, bean12.getBean());
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		}
	}

	public static void setMethod(Method[] method, Field[] field, Object bean,
			List<BeanDefinition> TotalBeanList) {
		for (int k = 0; k < method.length; k++) {
			method[k].setAccessible(true);
			if (method[k].isAnnotationPresent(Autowired.class)) {

				Class<?>[] MethodTypeClass = method[k].getParameterTypes();
				if (MethodTypeClass.length > 0) {
					Object obj[] = new Object[MethodTypeClass.length];
					Iterator iter = TotalBeanList.iterator();
					while (iter.hasNext()) {
						BeanDefinition bean123 = (BeanDefinition) iter.next();
						for (int j = 0; j < MethodTypeClass.length; j++) {
							if (bean123.getBean().getClass()
									.isAssignableFrom(MethodTypeClass[j])) {
								obj[j] = bean123.getBean();

							}
						}

					}
					try {
						method[k].invoke(bean, obj);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

	}

}
