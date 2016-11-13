package test.edu.javaee.spring;

import dev.edu.javaee.spring.annotation.Autowired;



public class student {
	private office office;
	private car car;

	
	@Autowired
	private static teacher teacher;

	private father father;

	
	@Autowired
	public student(car car, office office) {
		this.car = car;
		this.office = office;
	}

	public father getFather() {
		return father;
	}

	
	@Autowired
	public void setFather(father father) {
		this.father = father;
		
	}

	public String tostring() {

		return "this student has " + car.tostring() + " and  " + office.tostring();
	}
}
