package test.edu.javaee.spring;

import dev.edu.javaee.spring.annotation.Component;

//自动导入car
@Component("car")
public class car {
	private String carId = "777";
	private String carColor = "blue";

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getCarColor() {
		return carColor;
	}

	public void setCarColor(String carColor) {
		this.carColor = carColor;
	}

	public String tostring() {
		return "the car is " + carColor + " with " + carId;
	}
}
