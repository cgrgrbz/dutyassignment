package com.cagrigurbuz.kayseriulasim.dutyassignment.solver;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Objects;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Employee;

public class EmployeeWorkSequence implements Comparable<EmployeeWorkSequence> {

    private static final Comparator<EmployeeWorkSequence> COMPARATOR = Comparator.comparing(EmployeeWorkSequence::getEmployee)
    		.thenComparing(EmployeeWorkSequence::getFirstDayDate)
            .thenComparing(EmployeeWorkSequence::getLastDayDate);

    private Employee employee;
    private LocalDate firstDayDate;
    private LocalDate lastDayDate;

    public EmployeeWorkSequence(Employee employee, LocalDate firstDayDate, LocalDate lastDayDate) {
        this.employee = employee;
        this.firstDayDate = firstDayDate;
        this.lastDayDate = lastDayDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getFirstDayDate() {
		return firstDayDate;
	}

	public void setFirstDayDate(LocalDate firstDayDate) {
		this.firstDayDate = firstDayDate;
	}

	public LocalDate getLastDayDate() {
		return lastDayDate;
	}

	public void setLastDayDate(LocalDate lastDayDate) {
		this.lastDayDate = lastDayDate;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmployeeWorkSequence other = (EmployeeWorkSequence) o;
        return Objects.equals(employee, other.employee) &&
                firstDayDate == other.firstDayDate &&
                lastDayDate == other.lastDayDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, firstDayDate, lastDayDate);
    }

    @Override
    public int compareTo(EmployeeWorkSequence other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return employee + " is working between " + firstDayDate + " - " + lastDayDate;
    }

    public int getDayLength() {
        return (int) ChronoUnit.DAYS.between(firstDayDate,lastDayDate) + 1;
    }
}