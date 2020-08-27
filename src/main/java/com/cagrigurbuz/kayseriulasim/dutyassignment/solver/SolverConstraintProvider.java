package com.cagrigurbuz.kayseriulasim.dutyassignment.solver;

import java.util.Objects;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.greaterThan;
import static org.optaplanner.core.api.score.stream.Joiners.lessThan;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Duty;

public class SolverConstraintProvider implements ConstraintProvider {

	@Override
	public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
        		requiredRegionOfEmployee(constraintFactory),
        		assignEveryDuty(constraintFactory),
        		noOverlappingDuties(constraintFactory),
        };
	}
	
	//TODO
	//max working day
	//no conflicting duty for a employee
	//minimum rest after duty for next duty assignment
    
    private static UniConstraintStream<Duty> getAssignedDutyConstraintStream(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(Duty.class)
                .filter(duty -> duty.getEmployee() != null);
    }
    
    //Assign A Employee Only From Same Region
    private Constraint requiredRegionOfEmployee(ConstraintFactory constraintFactory) {
        return getAssignedDutyConstraintStream(constraintFactory)
                .filter(duty -> !duty.employeeIsInSameRegion())
                .penalize("Assign employee from the same region.", HardSoftScore.ONE_HARD);
        		
    }
    
    Constraint assignEveryDuty(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(Duty.class)
                .filter(duty -> duty.getEmployee() == null)
                .penalize("Assign every duty.", HardSoftScore.ONE_SOFT);
    }
    
    Constraint noOverlappingDuties(ConstraintFactory constraintFactory) {
        return getAssignedDutyConstraintStream(constraintFactory)
                .join(Duty.class,
                        equal(Duty::getEmployee),
                        lessThan(Duty::getStartDateTime, Duty::getEndDateTime),
                        greaterThan(Duty::getEndDateTime, Duty::getStartDateTime))
                .filter((duty, otherDuty) -> !Objects.equals(duty, otherDuty))
                .penalize("No Overlapping Duties.", HardSoftScore.ofHard(100));
                //.penalizeConfigurableLong("No Overlapping Duties.", (duty, otherDuty) -> otherDuty.dutyLengthInMinutes());
    }
    
}