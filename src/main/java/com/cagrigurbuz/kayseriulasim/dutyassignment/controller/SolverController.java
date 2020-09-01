package com.cagrigurbuz.kayseriulasim.dutyassignment.controller;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Schedule;
import com.cagrigurbuz.kayseriulasim.dutyassignment.service.DutyService;
import com.cagrigurbuz.kayseriulasim.dutyassignment.service.EmployeeService;
import com.cagrigurbuz.kayseriulasim.dutyassignment.service.ScheduleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/solver")
@Api(tags = "Solver")
public class SolverController {

	@Autowired
	private SolverManager<Schedule, UUID> solverManager;

	@Autowired
	private ScoreManager<Schedule> scoreManager;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private DutyService dutyService;

	@Autowired
	private ScheduleService scheduleService;

	UUID problemId = UUID.randomUUID();

	Schedule problem = new Schedule();

	Schedule solution = null;

	@ApiOperation("Solve for schedule")
	@PostMapping("/solve")
	public Schedule solve(@RequestParam("scheduleStartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleStartDate) {
		
		problem.setScheduleStartDate(scheduleStartDate);

		problem.setDutyList(dutyService.getDutyList());
		problem.setEmployeeList(employeeService.getEmployeeList());
		
		//TODO
		//Because we getting the date from post request, we want the solver solve only the current duties equal or grater then the given date
		//I flagging the duties manually with looping all the in memory duties, but will add a parameterization stuff for the schedule so we can filter accordingly
		problem.setCurrentDutyFlag(scheduleStartDate);		
		dutyService.updateDutyList(problem.getDutyList());
		
		SolverJob<Schedule, UUID> solverJob = solverManager.solve(problemId, problem);

		try {
			// Wait until the solving ends
			solution = solverJob.getFinalBestSolution();
			scoreManager.updateScore(solution);
		} catch (InterruptedException | ExecutionException e) {
			throw new IllegalStateException("Solving failed.", e);
		}

		dutyService.updateDutyList(solution.getDutyList());
		
		//delete the previous schedule.. then save the new one.
		scheduleService.clearSchedule();
		
		return scheduleService.addSchedule(solution);
	}

	@ApiOperation("Explain the solution")
	@GetMapping("/explain")
	public String explain() {
		return scoreManager.explainScore(solution);
	}
}
