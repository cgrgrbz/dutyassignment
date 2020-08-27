package com.cagrigurbuz.kayseriulasim.dutyassignment.domain;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	
	@ProblemFactCollectionProperty
	@ValueRangeProvider(id = "employeeRange")
	@ElementCollection(targetClass=Employee.class)
	private List<Employee> employeeList;

	@ElementCollection(targetClass=Duty.class)
	@PlanningEntityCollectionProperty
	private List<Duty> dutyList;
	
	@PlanningScore
	private HardSoftScore score;

	public Schedule() {

	}

	public Schedule(List<Employee> employeeList, List<Duty> dutyList, HardSoftScore score) {
		super();
		this.employeeList = employeeList;
		this.dutyList = dutyList;
		this.score = score;
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
}
