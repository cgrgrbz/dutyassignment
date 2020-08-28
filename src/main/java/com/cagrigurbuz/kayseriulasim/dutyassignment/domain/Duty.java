package com.cagrigurbuz.kayseriulasim.dutyassignment.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@Entity
@PlanningEntity
public class Duty {
	
	@PlanningId
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name, region, type;
	
	private Double load;
	
	@ManyToOne
	@PlanningVariable(valueRangeProviderRefs = "employeeRange", nullable = true)
	private Employee employee;
	
	private LocalDateTime startDateTime, endDateTime;
		
	public Duty() {
		
	}
	
	public Duty(Long id, String name, String region, String type, Double load, Employee employee,
			LocalDateTime startDateTime, LocalDateTime endDateTime) {
		super();
		this.id = id;
		this.name = name;
		this.region = region;
		this.type = type;
		this.load = load;
		this.employee = employee;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getLoad() {
		return load;
	}

	public void setLoad(Double load) {
		this.load = load;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}

	@Override
	public String toString() {
		return "Duty [id=" + id + ", name=" + name + ", region=" + region + ", type=" + type + ", load=" + load
				+ ", employee=" + employee + ", startDateTime=" + startDateTime + ", endDateTime=" + endDateTime + "]";
	}

	public boolean employeeIsInSameRegion() {
		return employee.getRegion() == getRegion();
	}
	
	public Long dutyLengthInMinutes() {
		return startDateTime.until(endDateTime, ChronoUnit.MINUTES);
	}

}
