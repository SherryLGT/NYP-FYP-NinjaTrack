package model;

import java.util.GregorianCalendar;

public class Profile {

	private int id, contactNo;
	private String name, email, image;
	private GregorianCalendar age, startDate;
	
	public Profile() {};
	
	public Profile(String name, GregorianCalendar age, int contactNo, String email, GregorianCalendar startDate, String image) {
		this.name = name;
		this.age = age;
		this.contactNo = contactNo;
		this.email = email;
		this.startDate = startDate;
		this.image = image;
	}
	
	public Profile(int id, String name, GregorianCalendar age, int contactNo, String email, GregorianCalendar startDate, String image) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.contactNo = contactNo;
		this.email = email;
		this.startDate = startDate;
		this.image = image;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getContactNo() {
		return contactNo;
	}

	public void setContactNo(int contactNo) {
		this.contactNo = contactNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public GregorianCalendar getAge() {
		return age;
	}

	public void setAge(GregorianCalendar age) {
		this.age = age;
	}

	public GregorianCalendar getStartDate() {
		return startDate;
	}

	public void setStartDate(GregorianCalendar startDate) {
		this.startDate = startDate;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
