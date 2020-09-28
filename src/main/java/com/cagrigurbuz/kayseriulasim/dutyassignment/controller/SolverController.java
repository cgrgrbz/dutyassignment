package com.cagrigurbuz.kayseriulasim.dutyassignment.controller;

import java.time.LocalDate;

import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Schedule;
import com.cagrigurbuz.kayseriulasim.dutyassignment.service.DutyService;
import com.cagrigurbuz.kayseriulasim.dutyassignment.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/solver")
@Api(tags = "Solver")
public class SolverController {
	
	public static final Long scheduleId = 1L;
	
	@Autowired
	private SolverManager<Schedule, Long> solverManager;

	@Autowired
	private ScoreManager<Schedule> scoreManager;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private DutyService dutyService;

	@ApiOperation("Solve for schedule")
	@PostMapping("/solve")
	public ResponseEntity<String> solve(@RequestParam("scheduleStartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleStartDate, @RequestParam("scheduleDayLength") int scheduleDayLength) {
		
		Schedule schedule = getSchedule(scheduleId);
		
		schedule.setScheduleStartDate(scheduleStartDate);
		
		schedule.setScheduleEndDate(scheduleStartDate.plusDays(scheduleDayLength));
		schedule.setCurrentDutyFlag();
		
		dutyService.updateDutyList(schedule.getDutyList());
		
		saveSchedule(schedule);
		
		solverManager.solveAndListen(scheduleId, this::getSchedule, this::saveSchedule);
		
		return new ResponseEntity<>(
				"Solver started and schedule set from " + schedule.getScheduleStartDate() +  " to " + schedule.getScheduleEndDate() + ".",HttpStatus.ACCEPTED);
	}
	
	@ApiOperation("Terminate Solver")
	@PostMapping("/terminate")
	public ResponseEntity<String> terminateSolver() {
		if (solverManager.getSolverStatus(scheduleId) == SolverStatus.SOLVING_ACTIVE) {
			
			solverManager.terminateEarly(scheduleId);

			return new ResponseEntity<>("Solver terminated", HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("No running solver found!", HttpStatus.BAD_REQUEST);
		}
	}
	

	
	
	public Schedule getSchedule(Long id) {
		
      Schedule schedule = new Schedule(scheduleId, employeeService.getEmployeeList(), dutyService.getDutyList());
      //scheduleService.addSchedule(schedule);

      return schedule;		
	}
	
	public void saveSchedule(Schedule solution) {
		dutyService.updateDutyList(solution.getDutyList());
	}

	@ApiOperation("Explain the solution")
	@GetMapping("/explain")
	public String explain() {
		return scoreManager.explainScore(getSchedule(scheduleId));
	}
}
