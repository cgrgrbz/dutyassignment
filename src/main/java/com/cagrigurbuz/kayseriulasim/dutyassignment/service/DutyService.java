package com.cagrigurbuz.kayseriulasim.dutyassignment.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Duty;
import com.cagrigurbuz.kayseriulasim.dutyassignment.repository.DutyRepository;
import com.cagrigurbuz.kayseriulasim.dutyassignment.utils.DutyListXLSXFileIO;


@Service
public class DutyService {

	private final DutyRepository dutyRepository;
	
	private final DutyListXLSXFileIO dutyListXLSXFileIO;
	
    public DutyService(DutyRepository dutyRepository, DutyListXLSXFileIO dutyListXLSXFileIO) {
		super();
		this.dutyRepository = dutyRepository;
		this.dutyListXLSXFileIO = dutyListXLSXFileIO;
	}

	@Transactional
    public List<Duty> getDutyList() {
        return dutyRepository.findAll();
    }

    @Transactional
    public Duty addDuty(Duty duty) {
    	return dutyRepository.save(duty);
    }
    
    @Transactional
    public Duty updateDuty(Duty duty) {
        
    	Duty newDuty = duty;
    	
    	Duty oldDuty = dutyRepository
                .findById(newDuty.getId())
                .orElseThrow(() -> new EntityNotFoundException("Duty entity with ID (" + newDuty.getId() + ") not found."));
    	
    	oldDuty.setName(newDuty.getName());
    	oldDuty.setRegion(newDuty.getRegion());
    	oldDuty.setType(newDuty.getType());
    	
    	oldDuty.setStartDateTime(newDuty.getStartDateTime());
    	oldDuty.setEndDateTime(newDuty.getEndDateTime());
    	
    	oldDuty.setEmployee(newDuty.getEmployee());

    	oldDuty.setLoad(newDuty.getLoad());
        
        return dutyRepository.save(oldDuty);
    }
    
    @Transactional
    public List<Duty> updateDutyList(List<Duty> dutyList) {
        
    	for (Duty duty: dutyList) {
    		updateDuty(duty);
    	}
    	
    	return dutyRepository.findAll();
    }
    
    @Transactional
    public List<Duty> importDutyFromExcel(InputStream excelInputStream) throws IOException {
    	
        List<Duty> excelDutyList = dutyListXLSXFileIO.getDutyListFromExcelFile(excelInputStream);

        final Set<String> addedDutySet = new HashSet<>();
        
        excelDutyList.stream().flatMap(duty -> {
            if (addedDutySet.contains(duty.getId())) {
                // Duplicate Duty; already in the stream
                return Stream.empty();
            }
            // Add duty to the stream
            addedDutySet.add(duty.getName().toLowerCase());
            return Stream.of(duty);
        }).forEach(duty -> {
            Duty oldDuty = dutyRepository.findDutyById(duty.getId());
            if (oldDuty != null) {
            	duty.setId(oldDuty.getId());
                updateDuty(duty);
            } else {
                addDuty(duty);
            }
        });
        
        return getDutyList();
    }
    
 }