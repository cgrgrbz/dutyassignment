package com.cagrigurbuz.kayseriulasim.dutyassignment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Schedule;

public interface ScheduleRepository extends CrudRepository<Schedule, Long> {

    @Query("select s from Schedule s " +
            "order by s.id")
    List<Schedule> findAll();
	
}
