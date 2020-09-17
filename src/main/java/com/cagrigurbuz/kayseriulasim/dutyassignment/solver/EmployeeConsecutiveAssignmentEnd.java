package com.cagrigurbuz.kayseriulasim.dutyassignment.solver;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Duty;
import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Employee;

public class EmployeeConsecutiveAssignmentEnd implements Comparable<EmployeeConsecutiveAssignmentEnd> {

    private static final Comparator<EmployeeConsecutiveAssignmentEnd> COMPARATOR = Comparator
            .comparing(EmployeeConsecutiveAssignmentEnd::getEmployee)
            .thenComparing(EmployeeConsecutiveAssignmentEnd::getDutyDate);

    private Employee employee;
    private Duty duty;

    public EmployeeConsecutiveAssignmentEnd(Employee employee, Duty duty) {
        this.employee = employee;
        this.duty = duty;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Duty getDuty() {
		return duty;
	}

	public LocalDate getDutyDate() {
        return duty.getStartDate();
    }

    public void setDuty(Duty duty) {
        this.duty = duty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmployeeConsecutiveAssignmentEnd other = (EmployeeConsecutiveAssignmentEnd) o;
        return Objects.equals(employee, other.employee) &&
                Objects.equals(duty.getStartDate(), other.duty.getStartDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, duty.getStartDate());
    }

    @Override
    public int compareTo(EmployeeConsecutiveAssignmentEnd other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return employee + " ... - " + duty;
    }

    public int getDutyDateDayIndex() {
        return duty.getStartDate().getDayOfWeek().getValue();
    }
    
}