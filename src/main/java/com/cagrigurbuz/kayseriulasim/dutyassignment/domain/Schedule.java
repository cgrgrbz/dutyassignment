package com.cagrigurbuz.kayseriulasim.dutyassignment.domain;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@PlanningSolution
@Entity
public class Schedule {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private LocalDate scheduleStartDate;
	
	@PlanningScore
	private HardSoftScore score;
	
	@ProblemFactCollectionProperty
	@OneToMany
	@ValueRangeProvider(id = "employeeRange")
	private List<Employee> employeeList;

	@OneToMany
	@PlanningEntityCollectionProperty
	private List<Duty> dutyList;

	public Schedule() {

	}
	
	public Schedule(Long id, List<Employee> employeeList, List<Duty> dutyList, HardSoftScore score,
			LocalDate scheduleStartDate) {
		super();
		this.id = id;
		this.employeeList = employeeList;
		this.dutyList = dutyList;
		this.score = score;
		this.scheduleStartDate = scheduleStartDate;
	}

	public List<Employee> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}

	public List<Duty> getDutyList() {
		return dutyList;
	}

	public void setDutyList(List<Duty> dutyList) {
		this.dutyList = dutyList;
	}

	public HardSoftScore getScore() {
		return score;
	}

	public void setScore(HardSoftScore score) {
		this.score = score;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getScheduleStartDate() {
		return scheduleStartDate;
	}

	public void setScheduleStartDate(LocalDate scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;
	}
	
	public int getScheduleWeekOfYear() {
		return scheduleStartDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	}
	
	public void setCurrentDutyFlag(LocalDate scheduleStartDate) {
		
		for(Duty duty : dutyList) {
			
			LocalDate dutyStartDate = duty.getStartDateTime().toLocalDate();
			
			if (dutyStartDate.isAfter(scheduleStartDate) || dutyStartDate.isEqual(scheduleStartDate) ) {
				duty.setItCurrentDutyToBeAssigned(true);
			}
		}
	}
}
