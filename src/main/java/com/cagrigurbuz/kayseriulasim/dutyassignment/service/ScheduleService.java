package com.cagrigurbuz.kayseriulasim.dutyassignment.service;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Schedule;
import com.cagrigurbuz.kayseriulasim.dutyassignment.repository.DutyRepository;
import com.cagrigurbuz.kayseriulasim.dutyassignment.repository.EmployeeRepository;
import com.cagrigurbuz.kayseriulasim.dutyassignment.repository.ScheduleRepository;

@Service
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;

	public ScheduleService(ScheduleRepository scheduleRepository, DutyRepository dutyRepository,
			EmployeeRepository employeeRepository) {
		super();
		this.scheduleRepository = scheduleRepository;
	}

	@Transactional
	public List<Schedule> getScheduleList() {
		return scheduleRepository.findAll();
	}

	@Transactional
	public Schedule addSchedule(Schedule schedule) {

		Schedule oldSchedule = scheduleRepository.findScheduleById(schedule.getId());

		if (oldSchedule != null) {
			return schedule;
		}

		return scheduleRepository.save(schedule);
	}

	public void clearSchedule() {
		scheduleRepository.deleteAll();
	}

	@Transactional
	public Schedule updateSchedule(Schedule schedule) {

		Schedule newSchedule = schedule;

		Schedule oldSchedule = scheduleRepository.findById(newSchedule.getId()).orElseThrow(
				() -> new EntityNotFoundException("Schedule entity with ID (" + newSchedule.getId() + ") not found."));

		oldSchedule.setDutyList(newSchedule.getDutyList());
		oldSchedule.setEmployeeList(newSchedule.getEmployeeList());
		oldSchedule.setScore(newSchedule.getScore());

		return scheduleRepository.save(oldSchedule);
	}
}
