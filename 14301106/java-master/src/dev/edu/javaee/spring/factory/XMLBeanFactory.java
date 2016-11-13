package dev.edu.javaee.spring.factory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import dev.edu.javaee.spring.bean.BeanDefinition;
import dev.edu.javaee.spring.bean.BeanUtil;
import dev.edu.javaee.spring.bean.PropertyValue;
import dev.edu.javaee.spring.bean.PropertyValues;
import dev.edu.javaee.spring.resource.Resource;
import dev.edu.javaee.spring.annotation.*;

public class XMLBeanFactory extends AbstractBeanFactory {

	

	private Map<String, String> nameRefMapping = new HashMap<String, String>();

	private Map<String, Map<String, String>> refRelationBean = new HashMap<String, Map<String, String>>();

	private List<BeanDefinition> theList = new ArrayList<BeanDefinition>();

	private List<String> beanNameList = new ArrayList<String>();

	public XMLBeanFactory(Resource resource) {

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document document = dbBuilder.parse(resource.getInputStream());
			NodeList beanList = document.getElementsByTagName("bean");

			for (int i = 0; i < beanList.getLength(); i++) {

				Node bean = beanList.item(i);
				BeanDefinition beandef = new BeanDefinition();
				String beanClassName = bean.getAttributes()
						.getNamedItem("class").getNodeValue();
				String beanName = bean.getAttributes().getNamedItem("id")
						.getNodeValue();

				beandef.setBeanClassName(beanClassName);

				try {
					Class<?> beanClass = Class.forName(beanClassName);
					beandef.setBeanClass(beanClass);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				PropertyValues propertyValues = new PropertyValues();

				NodeList propertyList = bean.getChildNodes();
				for (int j = 0; j < propertyList.getLength(); j++) {
					Node property = propertyList.item(j);
					if (property instanceof Element) {
						Element ele = (Element) property;

						String name = ele.getAttribute("name");
						Class<?> type;
						try {

							type = beandef.getBeanClass()
									.getDeclaredField(name).getType();
							Object value = ele.getAttribute("value");
							if (value != null && value.toString().length() > 0) {
								if (type == Integer.class) {
									value = Integer.parseInt((String) value);
								}

								propertyValues
										.AddPropertyValue(new PropertyValue(
												name, value));
							} else {

								String ref = ele.getAttribute("ref");
								Object beanRef = getBean(ref);
								if (beanRef != null) {
									Object beanReference = beanRef;
									propertyValues
											.AddPropertyValue(new PropertyValue(
													name, beanReference));
								} else {
									nameRefMapping.put(name, ref);
									
									refRelationBean.put(beanName,nameRefMapping);
								}

							}

						} catch (NoSuchFieldException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				beandef.setPropertyValues(propertyValues);
				this.registerBeanDefinition(beanName, beandef);
			}
			
			// 创建conponent注解的bean
			this.CreateComponentBean();
			// 依赖注入
			this.CreateRefBean();
			// 解析antowire注解
			this.CreateAutowireBean();
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		

	}

	public void CreateAutowireBean() {
		// 解析antowire注解
		try {
			List<BeanDefinition> TotalBeanList = new ArrayList<BeanDefinition>();
			Map<String, BeanDefinition> beanDefinitionMap = (Map<String, BeanDefinition>) this.getMap();
			Iterator it = beanDefinitionMap.values().iterator();

			while (it.hasNext()) {
				TotalBeanList.add((BeanDefinition) it.next());
			}

			List<BeanDefinition> FinishedLeftDefinition = AutowireUtil
					.CreateAutowireBean(theList, TotalBeanList);

			for (int i = 0; i < FinishedLeftDefinition.size(); i++) {
				beanDefinitionMap.put(beanNameList.get(i),
						FinishedLeftDefinition.get(i));

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected BeanDefinition GetCreatedBean(String beanName,
			BeanDefinition beanDefinition) {

		try {
			// set BeanClass for BeanDefinition

			Class<?> beanClass = beanDefinition.getBeanClass();
			// set Bean Instance for BeanDefinition
			Object bean = null;
			//判断是否有注解
			boolean flag=isAnnotation(beanClass);

			if (beanClass.getDeclaredConstructors()[0].getParameterTypes().length == 0
					&& flag == false) {
				bean = beanClass.newInstance();

				List<PropertyValue> fieldDefinitionList = beanDefinition
						.getPropertyValues().GetPropertyValues();
				for (PropertyValue propertyValue : fieldDefinitionList) {
					BeanUtil.invokeSetterMethod(bean, propertyValue.getName(),
							propertyValue.getValue());
				}

				beanDefinition.setBean(bean);

				return beanDefinition;

			} else {
				theList.add(beanDefinition);
				beanNameList.add(beanName);
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}
	public boolean isAnnotation(Class<?> beanClass){
		Field[] fields = beanClass.getDeclaredFields();
		Method[] methods = beanClass.getMethods();
		boolean flag = false;
		// 取出class中各个方法和属性
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].isAnnotationPresent(Autowired.class)) {
				flag = true;
			}
		}
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].isAnnotationPresent(Autowired.class)) {
				flag = true;
			}
		}
		return flag;
	}
	// 创建conponent注解的bean
	public void CreateComponentBean() {
		Map<String, BeanDefinition> beanDefinitionMap = (Map<String, BeanDefinition>) this
				.getMap();
		ComponentUtil componentCreateBean = new ComponentUtil();
		List<BeanDefinition> autoCreateBeanList = componentCreateBean
				.ComponentCreateBean();

		List<String> autoCreateBeanNameList = componentCreateBean
				.getAutoCreateBeanName();
		for (int i = 0; i < autoCreateBeanList.size(); i++) {
			beanDefinitionMap.put(autoCreateBeanNameList.get(i),
					autoCreateBeanList.get(i));
		}
	}

	public void CreateRefBean() {
		// 依赖注入

		Iterator it = refRelationBean.keySet().iterator();
		while (it.hasNext()) {

			String beanName = it.next().toString();
			BeanDefinition bean = getBeanDefinition(beanName);
			Map<String, String> map = refRelationBean.get(beanName);
			Iterator it1 = map.keySet().iterator();

			while (it1.hasNext()) {
				String propertyname = it1.next().toString();
				bean.getPropertyValues().AddPropertyValue(
						new PropertyValue(propertyname, getBean(propertyname)));
			}

			this.registerBeanDefinition(beanName, bean);
		}
	}
}
