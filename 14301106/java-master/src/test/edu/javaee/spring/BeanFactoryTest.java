package test.edu.javaee.spring;

import dev.edu.javaee.spring.factory.BeanFactory;
import dev.edu.javaee.spring.factory.XMLBeanFactory;
import dev.edu.javaee.spring.resource.LocalFileResource;

public class BeanFactoryTest {

	public static void main(String[] args) {

		LocalFileResource resource = new LocalFileResource("bean.xml");
		BeanFactory beanFactory = new XMLBeanFactory(resource);

		System.out.println("������¼�����������\n");
		
		
		student student = (student) beanFactory.getBean("student");
		System.out.println(student.tostring());
		
		
		
		
		
		
		father father = student.getFather();
		System.out.println("\nfather�е��ֶ�ֵ�� " + father.getA());

		
		
		
		
		
		
		teacher teacher = (teacher) beanFactory.getBean("teacher");
		System.out.println("\n"+teacher.tostring());

	}
}