package com.cagrigurbuz.kayseriulasim.dutyassignment.solver;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Duty;
import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Employee;

public class EmployeeConsecutiveAssignmentStart implements Comparable<EmployeeConsecutiveAssignmentStart> {

    private static final Comparator<EmployeeConsecutiveAssignmentStart> COMPARATOR = Comparator
            .comparing(EmployeeConsecutiveAssignmentStart::getEmployee)
            .thenComparing(EmployeeConsecutiveAssignmentStart::getDutyDate);

    private Employee employee;
    private Duty duty;

    public EmployeeConsecutiveAssignmentStart(Employee employee, Duty duty) {
        this.employee = employee;
        this.duty = duty;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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
        final EmployeeConsecutiveAssignmentStart other = (EmployeeConsecutiveAssignmentStart) o;
        return Objects.equals(employee, other.employee) &&
                Objects.equals(duty.getStartDate(), other.duty.getStartDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, duty.getStartDate());
    }

    @Override
    public int compareTo(EmployeeConsecutiveAssignmentStart other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return employee + " " + duty.getStartDate() + " - ...";
    }
    
}