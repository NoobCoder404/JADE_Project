package personFinder;

import java.io.Serializable;

public class Person implements Serializable { // define key information about a
												// person

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String email;

	private String name;

	private int age;

	private String gender;

	private String address;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" [Name] :").append(name).append("\n[Email] :").append(email)
				.append("\n[Gender] :").append(gender).append("\n[Address] :")
				.append(address);
		return sb.toString();

	}

}
