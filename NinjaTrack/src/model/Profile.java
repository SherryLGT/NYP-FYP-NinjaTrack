package model;

import java.sql.Date;

public class Profile {

	private int id, contactNo;
	private String name, email;
	private Date age, startDate;
	
	public Profile() {};
	
	public Profile(int id, String name, Date age, int contactNo, String email, Date startDate) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.contactNo = contactNo;
		this.email = email;
		this.startDate = startDate;
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

	public Date getAge() {
		return age;
	}

	public void setAge(Date age) {
		this.age = age;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
