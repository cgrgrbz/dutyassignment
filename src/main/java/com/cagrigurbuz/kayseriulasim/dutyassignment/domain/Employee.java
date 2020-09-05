package com.cagrigurbuz.kayseriulasim.dutyassignment.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.optaplanner.core.api.domain.lookup.PlanningId;

@Entity
public class Employee {
	
	@PlanningId
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String code;
	private String name;
	private String region;
	
	private double maxMonthlyWorkingHour;
	
	public Employee() {
		
	}
	
	public Employee(Long id, String code, String name, String region, double maxMonthlyWorkingHour) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.region = region;
		this.maxMonthlyWorkingHour = maxMonthlyWorkingHour;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getMaxMonthlyWorkingHour() {
		return maxMonthlyWorkingHour;
	}

	public void setMaxMonthlyWorkingHour(double maxMonthlyWorkingHour) {
		this.maxMonthlyWorkingHour = maxMonthlyWorkingHour;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", code=" + code + ", name=" + name + ", region=" + region + "]";
	}

}
