package com.cagrigurbuz.kayseriulasim.dutyassignment.domain;

import java.time.LocalDate;

import org.optaplanner.core.api.domain.entity.PinningFilter;

public class DutySelectionFilter implements PinningFilter<Schedule, Duty>{

	@Override
	public boolean accept(Schedule solution, Duty entity) {
		return !entity.isItCurrentDutyToBeAssigned();
	}
}
