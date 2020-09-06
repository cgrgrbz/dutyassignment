package com.cagrigurbuz.kayseriulasim.dutyassignment.solver;

import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.*;

import java.time.temporal.ChronoUnit;
import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Duty;
import com.google.common.base.Objects;

public class SolverConstraintProvider implements ConstraintProvider {

	@Override
	public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
        		assignFromSameRegion(constraintFactory),
        		assignEveryDuty(constraintFactory),
        		oneDutyPerDay(constraintFactory),
        		assignSameDutiesForTheSameWeek(constraintFactory),
        		breakBetweenTwoConsecutiveDutyAtLeast12Hours(constraintFactory),
        		consecutiveDutyShouldBeSameType(constraintFactory),
        		noTwoConsecutiveEveningDuties(constraintFactory),
        		noSameDutyConsecutiveWeek(constraintFactory),
        		weekdayFairDutyNameAssignmentByCount(constraintFactory),
        		weekendFairDutyNameAssignmentByCount(constraintFactory),
        		weekdayFairDutyTypeAssignmentByCount(constraintFactory),
        		weekendFairDutyTypeAssignmentByCount(constraintFactory),
        		noMoreThanMaximumWorkingHour(constraintFactory),
        };
	}

	
	//TODO
	//solver should only look for previous assignments UPTO the schedule start date, no more
	//BUT only penalize the ones in current schedule
	//OR?? with another domain definition, count them not directly, not in the constraint
	
	// returns only the assigned duties from all duties
    private static UniConstraintStream<Duty> getAssignedDutyConstraintStream(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(Duty.class)
                .filter(duty -> !duty.isNotAssigned());
    }
    
    //OK
    //Monthly sum of totalWorkingHour of employee duties CANNOT exceed the employee's maxMonthlyWorkingHour
	private Constraint noMoreThanMaximumWorkingHour(ConstraintFactory constraintFactory) {
		return getAssignedDutyConstraintStream(constraintFactory)
				.groupBy(
						duty -> duty.getEmployee(),
						duty -> duty.getDutyMonthOfYear(),
						sum((duty -> duty.totalWorkingHourInMinutes())))
				.filter((employee, month, totalAssignedDutyWorkingHourForMonth) -> totalAssignedDutyWorkingHourForMonth > employee.getMaxMonthlyWorkingInMinutes())
				.penalizeLong("Employee monthly working hour cannot exceed the employee's max hour!", 
						HardMediumSoftLongScore.ONE_MEDIUM,(employee, month, totalAssignedDutyWorkingHourForMonth) -> totalAssignedDutyWorkingHourForMonth - employee.getMaxMonthlyWorkingInMinutes()); //
	}
	
    ///////////////////////////
    ///FAIR DUTY ASSIGNMENTS///
    ///////////////////////////
    ///////////START///////////
    ///////////////////////////
	
	//TODO
	//COUNTIN ONLY PREVIOUS DUTIES, not in current Schedule
	//BUT PENALIZE THE NEW DUTIES

	//OK
	//SAY THIS IS FOR WEEKDAYS
	//fairly assign duties by the count of duty type
	private Constraint weekdayFairDutyTypeAssignmentByCount(ConstraintFactory constraintFactory) {
		return constraintFactory.fromUnfiltered(Duty.class)
    	        .filter(duty -> duty.isWeekDay())
				.groupBy(Duty::getEmployee, Duty::getType, count()) //count duty types in previous assignments
    	        .filter((employee, dutyType, weekdayDutyTypeCount) -> weekdayDutyTypeCount/5 > 1 ) //no need to penalize all duties at first assignments
    	        .penalizeLong("Assign duties fairly on weekdays by dutyType assignment count", HardMediumSoftLongScore.ONE_SOFT, (employee, dutyType, weekdayDutyTypeCount) -> weekdayDutyTypeCount/5);
    }
	
	//OK
	//SAY THIS IS FOR WEEKDAYS
	//fairly assign duties by the count of duty type
	private Constraint weekendFairDutyTypeAssignmentByCount(ConstraintFactory constraintFactory) {
		return constraintFactory.fromUnfiltered(Duty.class)
    	        .filter(duty -> duty.isWeekend())
				.groupBy(Duty::getEmployee, Duty::getType, count()) //count duty types in previous assignments
    	        .filter((employee, dutyType, weekendDutyTypeCount) -> weekendDutyTypeCount > 1 ) //no need to penalize all duties at first assignments
    	        .penalizeLong("Assign duties fairly on weekends by dutyType assignment count", HardMediumSoftLongScore.ONE_SOFT, (employee, dutyType, weekendDutyTypeCount) -> weekendDutyTypeCount);
    }

	//OK
	//fairly assign duties on Weekdays by the count of them
    private Constraint weekdayFairDutyNameAssignmentByCount(ConstraintFactory constraintFactory) {
		return getAssignedDutyConstraintStream(constraintFactory)
				.filter(duty -> duty.isWeekDay())
				.groupBy(Duty::getEmployee, Duty::getName, count()) //count duty types in previous assignments
    	        .filter((employee, dutyName, weekdayDutyNameCount) -> weekdayDutyNameCount/5 > 1) //no need to penalize all duties at first assignments
    	        .penalizeLong("Assign duties fairly on weekdays by dutyName assignment count", HardMediumSoftLongScore.ONE_SOFT, (employee, dutyName, weekdayDutyNameCount) -> weekdayDutyNameCount/5);
    }
    
	//OK
	//fairly assign duties on Weekends by the count of them
    private Constraint weekendFairDutyNameAssignmentByCount(ConstraintFactory constraintFactory) {
		return getAssignedDutyConstraintStream(constraintFactory)
				.filter(duty -> duty.isWeekend())
				.groupBy(Duty::getEmployee, Duty::getName, count()) //count duty types in previous assignments
    	        .filter((employee, dutyName, weekendDutyNameCount) -> weekendDutyNameCount > 1) //no need to penalize all duties at first assignments
    	        .penalizeLong("Assign duties fairly on weekends by dutyName assignment count", HardMediumSoftLongScore.ONE_SOFT, (employee, dutyName, weekendDutyNameCount) -> weekendDutyNameCount);
    }

    ///////////////////////////
    ///FAIR DUTY ASSIGNMENTS///
    ///////////////////////////
    ////////////END////////////
    ///////////////////////////
    
    //OK
	//Do not assign same duty consecutive weeks
	private Constraint noSameDutyConsecutiveWeek(ConstraintFactory constraintFactory) {
		return getAssignedDutyConstraintStream(constraintFactory)
				.join(Duty.class,
						Joiners.equal(Duty::getEmployee),
						Joiners.equal(Duty::getName),
						Joiners.equal(Duty::isInCurrentSchedule)) //only the ones in new schedule time period
				.filter((d1, d2) -> d2.isNextWeekDuty(d1))
				.penalizeLong("No consecutive week same duty assignment", HardMediumSoftLongScore.ONE_MEDIUM, (d1, d2) -> d2.getDutyLengthInMinutes());
	}

	//OK
	//no two consecutive week NIGHT duty assignment
	//may I convert it to enum later, I should but will see
	//A is Evening, S is Morning, and SA is middle day (half morning, half evening) duty.
	//it penalizes not the first match but second
	private Constraint noTwoConsecutiveEveningDuties(ConstraintFactory constraintFactory) {
		return getAssignedDutyConstraintStream(constraintFactory)
				.join(Duty.class,
						Joiners.equal(Duty::getEmployee),
						Joiners.equal(Duty::isInCurrentSchedule)) //only the ones in new schedule time period
				.filter((d1, d2) -> d2.isNextWeekDuty(d1))
				.filter((d1, d2) -> d1.getType() == "A" && d2.getType() == "A")
				//.filter((d1, d2) -> d1.getType() == d2.getType())
				.penalizeLong("No consecutive week night shift assignment", HardMediumSoftLongScore.ONE_SOFT, (d1, d2) -> d2.getDutyLengthInMinutes());
	}

	//OK
	//if you assigning the employee two consecutive days, duty types should be same
	//is for ex. d1 morning and d2 Evening duty, or vice versa, it's NOT appropriate but acceptable
	//in that case, 12 hour rule still active, so seems no problem for now
	//but we'll later limit Evening duty to morning duty hardly!
	private Constraint consecutiveDutyShouldBeSameType(ConstraintFactory constraintFactory) {
		return getAssignedDutyConstraintStream(constraintFactory)
				.join(Duty.class,
						Joiners.equal(Duty::getEmployee),
						Joiners.equal(Duty::isInCurrentSchedule)) //only the ones in new schedule time period
				.filter((d1, d2) -> d2.isNextDayDuty(d1))
				.filter((d1, d2) -> !d1.getName().equals(d2.getName()))
				.filter((d1, d2) -> d1.getType()!= d2.getType())
				.penalizeLong("Assigned consecutive duty types should be same", HardMediumSoftLongScore.ONE_SOFT, (d1, d2) -> d2.getDutyLengthInMinutes());
	}

	//OK
	//assign employee from same region!
	private Constraint assignFromSameRegion(ConstraintFactory constraintFactory) {
		return getAssignedDutyConstraintStream(constraintFactory)
				.filter(
						(duty) -> !duty.getEmployee().getRegion().equals(duty.getRegion())
				)
				.penalizeLong("Assign employee from same region", HardMediumSoftLongScore.ONE_MEDIUM, duty-> duty.getDutyLengthInMinutes()*duty.getPenalty());
	}
    
    //OK
    //Try to assign every duty in the Current schedule period
    Constraint assignEveryDuty(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(Duty.class)
        		.filter(duty -> duty.isNotAssigned() && duty.isInCurrentSchedule())
                .penalizeLong("Assign every duty.", HardMediumSoftLongScore.ONE_MEDIUM, duty-> duty.getDutyLengthInMinutes()*duty.getPenalty()+duty.getLoad().intValue() + 1);
    }
    
    //OK
    //Assign one duty per day
//    Constraint oneDutyPerDay(ConstraintFactory constraintFactory) {
//    	return getAssignedDutyConstraintStream(constraintFactory)
//    	        .groupBy(
//    	        		duty -> Pair.of(duty.getEmployee(), duty.getDutyDayOfYear()), count())
//                .filter((employee, count) -> count > 1)
//    	        .penalize("No more than one duty per day.", HardMediumSoftLongScore.ONE_HARD);
//    }
    
  //Assign one duty per day
    Constraint oneDutyPerDay(ConstraintFactory constraintFactory) {
    	return getAssignedDutyConstraintStream(constraintFactory)
    			.join(Duty.class,
    					Joiners.equal(Duty::getEmployee),
    					Joiners.equal(Duty::getDutyDayOfYear),
    					Joiners.equal(Duty::isInCurrentSchedule))
    			.filter((d1, d2) -> !d1.equals(d2))
    	        .penalizeLong("No more than one duty per day.", HardMediumSoftLongScore.ONE_HARD, (d1, d2) -> d2.getDutyLengthInMinutes());
    }
    
    //OK
    //at least 12 hours after each assigned duty
    Constraint breakBetweenTwoConsecutiveDutyAtLeast12Hours(ConstraintFactory constraintFactory) {
        return getAssignedDutyConstraintStream(constraintFactory)
                .join(Duty.class,
                        Joiners.equal(Duty::getEmployee),
                        Joiners.lessThan(Duty::getEndDateTime, Duty::getStartDateTime))
                .filter((d1, d2) -> !d1.equals(d2))
                .filter((d1, d2) -> d1.getEndDateTime().until(d2.getStartDateTime(), ChronoUnit.HOURS) < 12)
                .penalizeLong("At least 12 Hours break after the Duty.", HardMediumSoftLongScore.ONE_MEDIUM, (d1, d2) -> (int) (720 - d1.getEndDateTime().until(d2.getStartDateTime(), ChronoUnit.MINUTES)));
    }
    
    //OK
    //Assign same weekday duties in same week
    Constraint assignSameDutiesForTheSameWeek(ConstraintFactory constraintFactory) {
    	return getAssignedDutyConstraintStream(constraintFactory)
    			.join(Duty.class,
    					Joiners.equal(Duty::getName),
    					Joiners.equal(Duty::getDutyWeekOfYear))
    			.filter((duty, otherDuty) -> !Objects.equal(duty, otherDuty))
    			.filter((duty, otherDuty) -> duty.getEmployee() != otherDuty.getEmployee())
    	        .penalizeLong("Same duty at weekdays!.", HardMediumSoftLongScore.ONE_SOFT, (duty, otherDuty) -> duty.getDutyLengthInMinutes()*duty.getDutyLengthInMinutes());
    }
}