package com.cagrigurbuz.kayseriulasim.dutyassignment.solver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Duty;

public class SolverConstraintProvider implements ConstraintProvider {

	@Override
	public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
        		requiredRegionOfEmployee(constraintFactory),
        		assignEveryDuty(constraintFactory),
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
    
}