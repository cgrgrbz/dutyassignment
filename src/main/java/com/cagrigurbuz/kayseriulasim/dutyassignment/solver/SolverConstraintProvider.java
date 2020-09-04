package com.cagrigurbuz.kayseriulasim.dutyassignment.solver;

import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.greaterThan;
import static org.optaplanner.core.api.score.stream.Joiners.lessThan;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.count;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Duty;

public class SolverConstraintProvider implements ConstraintProvider {

	@Override
	public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
        		//requiredRegionOfEmployee(constraintFactory),
        		assignEveryDuty(constraintFactory),
        		oneDutyPerDay(constraintFactory),
        		maxTwoDutyInAWeek(constraintFactory),
        		onlyAndOnlyOneWeekendDuty(constraintFactory),
        		firstHighyPriorityDuties(constraintFactory),
        };
	}
	
	// returns only the assigned duties from all duties
    private static UniConstraintStream<Duty> getAssignedDutyConstraintStream(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(Duty.class)
                .filter(duty -> duty.getEmployee() != null);
    }
       
    //Assign A Employee Only From Same Region
    private Constraint requiredRegionOfEmployee(ConstraintFactory constraintFactory) {
        return getAssignedDutyConstraintStream(constraintFactory)
                .filter(duty -> !duty.employeeIsInSameRegion())
                .penalize("Assign employee from the same region.", HardSoftScore.ofHard(100));
        		
    }
    
   //Try to assign every duty in the Current schedule period
    Constraint assignEveryDuty(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(Duty.class)
                .filter(duty -> duty.getEmployee() == null & duty.isItCurrentDutyToBeAssigned())
                .penalize("Assign every duty.", HardSoftScore.ofSoft(1), duty -> duty.getLoad().intValue() * duty.getPriority());
    }
    
    //Assign high priority duties rather than lower ones
    Constraint firstHighyPriorityDuties(ConstraintFactory constraintFactory) {
    	return getAssignedDutyConstraintStream(constraintFactory)
    			.join(Duty.class,
    					greaterThan(Duty::getPriority, Duty::getPriority)
    					)
    			.filter((duty, otherDuty) -> duty.getEmployee() == null & otherDuty.getEmployee() != null)
    			.penalize("firstHighyPriorityDuties", HardSoftScore.ONE_HARD);
    	
    }
    
    //No overlapping duties for a employee
    Constraint noOverlappingDuties(ConstraintFactory constraintFactory) {
        return getAssignedDutyConstraintStream(constraintFactory)
                .join(Duty.class,
                        equal(Duty::getEmployee),
                        lessThan(Duty::getStartDateTime, Duty::getEndDateTime),
                        greaterThan(Duty::getEndDateTime, Duty::getStartDateTime))
                .filter((duty, otherDuty) -> !Objects.equals(duty, otherDuty) & duty.isItCurrentDutyToBeAssigned())
                .penalize("No Overlapping Duties.", HardSoftScore.ofHard(100));
    }
    
    //at least 12 hours after each assigned duty
    Constraint breakBetweenTwoConsecutiveDutyAtLeast12Hours(ConstraintFactory constraintFactory) {
        return getAssignedDutyConstraintStream(constraintFactory)
                .join(Duty.class,
                        equal(Duty::getEmployee),
                        lessThan(Duty::getEndDateTime, Duty::getStartDateTime))
                .filter((s1, s2) -> !Objects.equals(s1, s2))
                .filter((s1, s2) -> s1.getEndDateTime().until(s2.getStartDateTime(), ChronoUnit.HOURS) < 12)
                .penalize("At least 12 Hours break after the Duty.", HardSoftScore.ONE_SOFT);
    }
    
	//TODO CHECK IT IF CORRECT    
    //Assign same duty to the same employee on weekdays
    Constraint assignSameDutiesForTheSameWeek(ConstraintFactory constraintFactory) {
    	return getAssignedDutyConstraintStream(constraintFactory)
    			.join(Duty.class,
    					equal(Duty::isWeekDay))
    			.filter((duty, otherDuty) -> duty.getName() == otherDuty.getName())
    			.filter((duty, otherDuty) -> duty.getEmployee() != otherDuty.getEmployee())
    	        .penalize("Same duty at weekdays!.", HardSoftScore.ONE_HARD);
    }
    
    
    //Maximum 6 days working for each week   
    Constraint maxSixWorkingDayInAWeek(ConstraintFactory constraintFactory) {
    	return getAssignedDutyConstraintStream(constraintFactory)
    	        .groupBy(
    	        		duty -> Pair.of(duty.getEmployee(), duty.getDutyWeekOfYear()), count())
                .filter((employee, count) -> count > 6)
    	        .penalize("Maximum working of 6 days per week.", HardSoftScore.ONE_HARD);
    }
    
    //Maximum 6 days working for each week   
    Constraint maxTwoDutyInAWeek(ConstraintFactory constraintFactory) {
    	return getAssignedDutyConstraintStream(constraintFactory)
    	        .groupBy(
    	        		duty -> Pair.of(duty.getEmployee(), duty.getDutyWeekOfYear()), count())
                .filter((employee, count) -> count > 2)
    	        .penalize("Maximum 2 duty per week.", HardSoftScore.ONE_HARD);
    }
    
    //Assign two duty per week
    Constraint oneDutyPerDay(ConstraintFactory constraintFactory) {
    	return getAssignedDutyConstraintStream(constraintFactory)
    	        .groupBy(
    	        		duty -> Pair.of(duty.getEmployee(), duty.getDutyDayOfYear()), count())
                .filter((employee, count) -> count > 1)
    	        .penalize("No more than one duty per day.", HardSoftScore.ONE_HARD);
    }
    
    //Assign only one weekend duty for sure
    Constraint onlyAndOnlyOneWeekendDuty(ConstraintFactory constraintFactory) {
    	return getAssignedDutyConstraintStream(constraintFactory)
    			.filter(duty -> !duty.isWeekDay())
    	        .groupBy(
    	        		duty -> Pair.of(duty.getEmployee(), duty.getDutyWeekOfYear()), count())
                .filter((employee, count) -> count > 1)
    	        .penalize("Assign only one weekend duty for sure.", HardSoftScore.ONE_HARD);
    }

    //Assign same dutyType for the following assignment
    
    //Fair dutyType assignment
    
    //Fair duty assignment
    
    //Do not assign same duty consecutive weeks - Soft
    
    //Do not assign NIGHT duty consecutive weeks - Hard
}