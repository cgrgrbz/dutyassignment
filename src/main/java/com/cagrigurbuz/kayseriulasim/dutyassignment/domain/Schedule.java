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
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.solver.SolverStatus;

@PlanningSolution
@Entity
public class Schedule {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
    private SolverStatus solverStatus;
	
	private LocalDate scheduleStartDate;
	
	private LocalDate scheduleEndDate;
	
	@PlanningScore
	private HardMediumSoftLongScore score;
	
	@ProblemFactCollectionProperty
	@OneToMany
	@ValueRangeProvider(id = "employeeRange")
	private List<Employee> employeeList;

	@OneToMany
	@PlanningEntityCollectionProperty
	private List<Duty> dutyList;

	public Schedule() {

	}
	
	public Schedule(Long id, List<Employee> employeeList, List<Duty> dutyList) {
		super();
		this.id = id;
		this.employeeList = employeeList;
		this.dutyList = dutyList;
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

	public HardMediumSoftLongScore getScore() {
		return score;
	}

	public void setScore(HardMediumSoftLongScore score) {
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
	
	public LocalDate getScheduleEndDate() {
		return scheduleEndDate;
	}

	public void setScheduleEndDate(LocalDate scheduleEndDate) {
		this.scheduleEndDate = scheduleEndDate;
	}

	public SolverStatus getSolverStatus() {
		return solverStatus;
	}

	public void setSolverStatus(SolverStatus solverStatus) {
		this.solverStatus = solverStatus;
	}

	public int getScheduleWeekOfYear() {
		return scheduleStartDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	}
	
	public void setCurrentDutyFlag() {
		
		//will do it streams, for performance on big datasets.
		for(Duty duty : dutyList) {
			
			LocalDate dutyStartDate = duty.getStartDateTime().toLocalDate();
			
			//if schedule not before the given startDate
			//AND
			//if before the end date not including endDate
			//for example, solve from the date X for Y days
			if (!dutyStartDate.isBefore(scheduleStartDate) && dutyStartDate.isBefore(scheduleEndDate)){
				duty.setInCurrentSchedule(true);
			}
		}
	}
}
