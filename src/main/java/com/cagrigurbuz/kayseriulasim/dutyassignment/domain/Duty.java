package com.cagrigurbuz.kayseriulasim.dutyassignment.domain;

import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Duty {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name, region;
	private int taskCount;
	
	@ManyToOne
	private Employee employee;
	
	private LocalTime startTime, endTime;
	private int startDayIndex, endDayIndex;
	
	public Duty() {
		
	}

	public Duty(Long id, String name, String region, int taskCount, Employee employee, LocalTime startTime,
			LocalTime endTime, int startDayIndex, int endDayIndex) {
		super();
		this.id = id;
		this.name = name;
		this.region = region;
		this.taskCount = taskCount;
		this.employee = employee;
		this.startTime = startTime;
		this.endTime = endTime;
		this.startDayIndex = startDayIndex;
		this.endDayIndex = endDayIndex;
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

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public int getStartDayIndex() {
		return startDayIndex;
	}

	public void setStartDayIndex(int startDayIndex) {
		this.startDayIndex = startDayIndex;
	}

	public int getEndDayIndex() {
		return endDayIndex;
	}

	public void setEndDayIndex(int endDayIndex) {
		this.endDayIndex = endDayIndex;
	}

}
