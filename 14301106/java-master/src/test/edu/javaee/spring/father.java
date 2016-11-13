package test.edu.javaee.spring;

import dev.edu.javaee.spring.annotation.*;
	
@Component("father")
public class father {
	private int a=200;
	
	public int getA() {
		return a;
	}
	
	public void setA(int a) {
		this.a = a;
	}
		
}
