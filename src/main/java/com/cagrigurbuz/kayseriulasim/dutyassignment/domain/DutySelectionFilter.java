package com.cagrigurbuz.kayseriulasim.dutyassignment.domain;

import java.time.LocalDate;

import org.optaplanner.core.api.domain.entity.PinningFilter;

public class DutySelectionFilter implements PinningFilter<Schedule, Duty>{

	@Override
	public boolean accept(Schedule solution, Duty entity) {
		LocalDate scheduleStartDate = solution.getScheduleStartDate();
		LocalDate dutyStartDate = entity.getStartDateTime().toLocalDate();
		return dutyStartDate.isBefore(scheduleStartDate); //accept only the entities that be considered in the current schedule, comparing the scheduleStartDate
		//////////?? how
	}
}
