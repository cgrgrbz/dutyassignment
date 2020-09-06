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

public class SolverConstraintProvider implements ConstraintProvider {

	@Override
	public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
        		assignFromSameRegion(constraintFactory), //OK - HARD
        		assignEveryDuty(constraintFactory), //OK
        		oneDutyPerDay(constraintFactory), //OK
        		assignSameDutiesForTheSameWeek(constraintFactory), //OK - HARD
        		breakBetweenTwoConsecutiveDutyAtLeast12Hours(constraintFactory), //OK
        		consecutiveDutyShouldBeSameType(constraintFactory), //OK
        		noTwoConsecutiveEveningDuties(constraintFactory), //OK - HARD
        		noSameDutyConsecutiveWeek(constraintFactory), //OK
        		fairDutyAssignmentByCount(constraintFactory), //OK
        		fairDutyTypeAssignmentByCount(constraintFactory), //OK
        		noMoreThanMaximumWorkingHour(constraintFactory), //OK
        };
	}

	// returns only the assigned duties from all duties
    private static UniConstraintStream<Duty> getAssignedDutyConstraintStream(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(Duty.class)
                .filter(duty -> !duty.isNotAssigned())
                .filter(duty -> duty.getType() != "G"); //-> G is garage duties, which realized everyday, so will add another constraint for it separately
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
		
	//OK
	//fairly assign duties by the count of duty type
	private Constraint fairDutyTypeAssignmentByCount(ConstraintFactory constraintFactory) {
		return getAssignedDutyConstraintStream(constraintFactory)
    	        .groupBy(
    	        		duty -> Pair.of(duty.getEmployee(), duty.getType()), count())
    	        .filter((employee, dutyTypeCount) -> dutyTypeCount>1) //no need to penalize all duties at first assignments
    	        .penalizeLong("Assign duties fairly by dutyType assignment count", HardMediumSoftLongScore.ONE_SOFT, (employee, dutyTypeCount) -> dutyTypeCount);
    }

	//OK
	//fairly assign duties by the count of them
    private Constraint fairDutyAssignmentByCount(ConstraintFactory constraintFactory) {
		return getAssignedDutyConstraintStream(constraintFactory)
    	        .groupBy(
    	        		duty -> Pair.of(duty.getEmployee(), duty.getName()), count())
    	        .filter((employee, dutyCount) -> dutyCount>1) //no need to penalize all duties at first assignments
    	        .penalizeLong("Assign duties fairly by duty assignment count", HardMediumSoftLongScore.ONE_SOFT, (employee, dutyCount) -> dutyCount);
    }

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
				.penalizeLong("No consecutive week night shift assignment", HardMediumSoftLongScore.ONE_MEDIUM, (d1, d2) -> d2.getDutyLengthInMinutes());
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
				.filter((d1, d2) -> d1.getType()!= d2.getType())
				.penalizeLong("Assigned consecutive duty types should be same", HardMediumSoftLongScore.ONE_MEDIUM, (d1, d2) -> d2.getDutyLengthInMinutes());
	}

	//OK
	//assign employee from same region!
	private Constraint assignFromSameRegion(ConstraintFactory constraintFactory) {
		return getAssignedDutyConstraintStream(constraintFactory)
				.filter((duty) -> !duty.employeeIsInSameRegion())
				.penalizeLong("Assign employee from same region", HardMediumSoftLongScore.ONE_MEDIUM, duty-> duty.getDutyLengthInMinutes()*duty.getPenalty());
	}
    
    //OK
    //Try to assign every duty in the Current schedule period
    Constraint assignEveryDuty(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(Duty.class)
        		.filter(duty -> duty.isNotAssigned() && duty.isInCurrentSchedule())
                .penalizeLong("Assign every duty.", HardMediumSoftLongScore.ONE_MEDIUM, duty -> duty.getPenalty() + duty.getTotalWorkingHour().intValue());
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
    					Joiners.equal(Duty::getEmployee, Duty::getEmployee),
    					Joiners.equal(Duty::getDutyDayOfYear, Duty::getDutyDayOfYear),
    					Joiners.equal(Duty::isInCurrentSchedule, Duty::isInCurrentSchedule))
    			.filter((d1, d2) -> !d1.equals(d2))
    	        .penalize("No more than one duty per day.", HardMediumSoftLongScore.ONE_HARD);
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
                .penalizeLong("At least 12 Hours break after the Duty.", HardMediumSoftLongScore.ONE_SOFT, (d1, d2) -> (int) (720 - d1.getEndDateTime().until(d2.getStartDateTime(), ChronoUnit.MINUTES)));
    }
    
    //OK
    //Assign same weekday duties in same week
    Constraint assignSameDutiesForTheSameWeek(ConstraintFactory constraintFactory) {
    	return getAssignedDutyConstraintStream(constraintFactory)
    			.join(Duty.class,
    					Joiners.equal(Duty::getName),
    					Joiners.equal(Duty::isWeekDay),
    					Joiners.equal(Duty::getDutyWeekOfYear))
    			.filter((duty, otherDuty) -> !duty.equals(otherDuty))
    			.filter((duty, otherDuty) -> duty.getEmployee() != otherDuty.getEmployee())
    	        .penalizeLong("Same duty at weekdays!.", HardMediumSoftLongScore.ONE_MEDIUM, (duty, otherDuty) -> duty.getDutyLengthInMinutes()*duty.getDutyLengthInMinutes());
    }
}