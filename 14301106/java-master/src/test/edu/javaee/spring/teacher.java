package test.edu.javaee.spring;

public class teacher {
	private office office;
	private car car;

	public office getOffice() {
		return office;
	}

	public void setOffice(office office) {
		this.office = office;
	}

	public car getCar() {
		return car;
	}

	public void setCar(car car) {
		this.car = car;
	}

	public String tostring() {
		return "this teacher has " + car.tostring() + " and in " + office.tostring();
	}
}