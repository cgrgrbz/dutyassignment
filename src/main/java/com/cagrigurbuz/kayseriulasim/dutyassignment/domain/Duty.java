package com.cagrigurbuz.kayseriulasim.dutyassignment.domain;

import java.time.LocalDateTime;
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
	
	private String name, region;
	private int taskCount;
	
	@ManyToOne
	@PlanningVariable(valueRangeProviderRefs = "employeeRange", nullable = true)
	private Employee employee;
	
	private LocalDateTime startDateTime, endDateTime;
	
	public Duty() {
		
	}

	public Duty(Long id, String name, String region, int taskCount, Employee employee, LocalDateTime startDateTime,
			LocalDateTime endDateTime) {
		super();
		this.id = id;
		this.name = name;
		this.region = region;
		this.taskCount = taskCount;
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

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
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
	
	public boolean employeeIsInSameRegion() {
		return employee.getRegion() == getRegion();
	}

	@Override
	public String toString() {
		return "Duty [id=" + id + ", name=" + name + ", region=" + region + ", taskCount=" + taskCount + ", employee="
				+ employee + ", startDateTime=" + startDateTime + ", endDateTime=" + endDateTime + "]";
	}
}
