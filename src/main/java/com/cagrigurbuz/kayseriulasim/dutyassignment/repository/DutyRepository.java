package com.cagrigurbuz.kayseriulasim.dutyassignment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Duty;

public interface DutyRepository extends CrudRepository<Duty, Long> {
	
    @Query("select d from Duty d " +
            "order by d.id")
    List<Duty> findAll();
	    
    @Query("select d from Duty d " +
            "where d.id = :dutyId")
    Duty findDutyById(@Param("dutyId") Long id);
    
}