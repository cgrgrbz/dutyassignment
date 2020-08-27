package com.cagrigurbuz.kayseriulasim.dutyassignment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Schedule;
import com.cagrigurbuz.kayseriulasim.dutyassignment.service.ScheduleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/schedule")
@CrossOrigin
@Validated
@Api(tags = "Schedule")
public class ScheduleController {

	private final ScheduleService scheduleService;

	public ScheduleController(ScheduleService scheduleService) {
		super();
		this.scheduleService = scheduleService;
	}
	
	@ApiOperation("Get a list of all schedules")
	@GetMapping("/")
	public ResponseEntity<List<Schedule>> getScheduleList() {
		return new ResponseEntity<>(scheduleService.getScheduleList(), HttpStatus.OK);
	}
	

}
