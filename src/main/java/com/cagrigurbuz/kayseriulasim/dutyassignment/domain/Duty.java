package com.cagrigurbuz.kayseriulasim.dutyassignment.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@Entity
@PlanningEntity(pinningFilter = DutySelectionFilter.class)
public class Duty {
	
	@PlanningId
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name, region, type;
	
	private Double load, totalWorkingHour;
	
	private int priority;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@PlanningVariable(valueRangeProviderRefs = "employeeRange", nullable = true)
	private Employee employee;
	
	private LocalDateTime startDateTime, endDateTime;
	
	private boolean inCurrentSchedule;
	
	public Duty() {
		
	}

	public Duty(Long id, String name, String region, String type, Double load, Employee employee,
			LocalDateTime startDateTime, LocalDateTime endDateTime, boolean inCurrentSchedule, int priority, Double totalWorkingHour) {
		super();
		this.id = id;
		this.name = name;
		this.region = region;
		this.type = type;
		this.load = load;
		this.employee = employee;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.inCurrentSchedule = inCurrentSchedule;
		this.priority = priority;
		this.totalWorkingHour = totalWorkingHour;
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

	public boolean isInCurrentSchedule() {
		return inCurrentSchedule;
	}

	public void setInCurrentSchedule(boolean inCurrentSchedule) {
		this.inCurrentSchedule = inCurrentSchedule;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Double getTotalWorkingHour() {
		return totalWorkingHour;
	}

	public void setTotalWorkingHour(Double totalWorkingHour) {
		this.totalWorkingHour = totalWorkingHour;
	}

	@Override
	public String toString() {
		return "Duty [id=" + id + ", name=" + name + ", region=" + region + ", type=" + type + ", load=" + load
				+ ", totalWorkingHour=" + totalWorkingHour + ", priority=" + priority + ", employee=" + employee
				+ ", startDateTime=" + startDateTime + ", endDateTime=" + endDateTime + ", isItCurrentDutyToBeAssigned="
				+ inCurrentSchedule + "]";
	}

	public boolean employeeIsInSameRegion() {
		return employee.getRegion() == getRegion();
	}
	
	public int getDutyLengthInMinutes() {
		return (int) startDateTime.until(endDateTime, ChronoUnit.MINUTES);
	}
	
	//we have totalWorkingHours in terms of hours, 7.5 hours for ex.
	//the input always will be .5 or .0 fraction
	//so getting int from that double is not a problem for rounding
	//BUT
	//will probably directly get minute values from data later
	public int totalWorkingHourInMinutes() {
		return (int) (totalWorkingHour*60);
	}
	
	public int getDutyMonthOfYear() {
		return startDateTime.getMonthValue();
	}
	
	public int getDutyWeekOfYear() {
		return startDateTime.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	}
	
	public int getDutyDayOfYear() {
		return startDateTime.get(ChronoField.DAY_OF_YEAR);
	}
	
	public boolean isWeekDay() {
		return startDateTime.get(ChronoField.DAY_OF_WEEK) < 6;
	}	
	
	public boolean isWeekend() {
		return startDateTime.get(ChronoField.DAY_OF_WEEK) > 5;
	}	

	public int getPenalty() {
		return load.intValue()*priority;
	}
	
	public boolean isNotAssigned() {
		return getEmployee() == null;
	}
	
	public boolean isNextDayDuty(Duty duty) {
		return duty.startDateTime.toLocalDate().plusDays(1).equals(this.getStartDateTime().toLocalDate());
	}
	
	public boolean isNextWeekDuty(Duty duty) {
		return duty.startDateTime.toLocalDate().plusDays(7).equals(this.getStartDateTime().toLocalDate());
	}
	
	public int getDayOfWeekValue() {
		return startDateTime.getDayOfWeek().getValue();
	}
	
	public LocalDate getStartDate() {
		return startDateTime.toLocalDate();
	}
}
