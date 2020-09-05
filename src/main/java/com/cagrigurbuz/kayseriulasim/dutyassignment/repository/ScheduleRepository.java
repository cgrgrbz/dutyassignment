package com.cagrigurbuz.kayseriulasim.dutyassignment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Employee;
import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Schedule;

public interface ScheduleRepository extends CrudRepository<Schedule, Long> {

    @Query("select s from Schedule s " +
            "order by s.id")
    List<Schedule> findAll();
	
    @Query("select s from Schedule s " +
            "where s.id = :scheduleId")
    Schedule findScheduleById(@Param("scheduleId") Long id);
    
}
