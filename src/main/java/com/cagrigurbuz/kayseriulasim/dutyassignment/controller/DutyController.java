package com.cagrigurbuz.kayseriulasim.dutyassignment.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Duty;
import com.cagrigurbuz.kayseriulasim.dutyassignment.service.DutyService;
import com.cagrigurbuz.kayseriulasim.dutyassignment.utils.CurrentScheduleExportXLSXIO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/duty")
@CrossOrigin
@Validated
@Api(tags = "Duty")
public class DutyController {

	private final DutyService dutyService;

	public DutyController(DutyService dutyService) {
		super();
		this.dutyService = dutyService;
	}
	
	@ApiOperation("Get a list of all duties")
	@GetMapping("/")
	public ResponseEntity<List<Duty>> getDutyList() {
		return new ResponseEntity<>(dutyService.getDutyList(), HttpStatus.OK);
	}
	
	@ApiOperation("Add a new duty")
	@PostMapping("/add")
	public ResponseEntity<Duty> addDuty(@RequestBody @Valid Duty duty) {
		return new ResponseEntity<>(dutyService.addDuty(duty), HttpStatus.OK);
	}

	@ApiOperation("Import duties from an Excel file")
	@PostMapping("/import")
	public ResponseEntity<List<Duty>> addDutyFromExcelFile(@RequestParam("file") MultipartFile excelDataFile) throws IOException {
		return new ResponseEntity<>(dutyService.importDutyFromExcel(excelDataFile.getInputStream()),
				HttpStatus.OK);
	}
	
	@ApiOperation("Get the duties only from the current schedule period, which is determined by starting solver.")
	@GetMapping("/current")
	public ResponseEntity<List<Duty>> getCurrentList() {
		return new ResponseEntity<>(dutyService.getCurrentDutyList(), HttpStatus.OK);
	}
	
    @ApiOperation("Get the current duty list in a excel file")
    @GetMapping(value = "/current/excel",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> getCurrentScheduleAsExcel() {

    	List<Duty> currentScheduleDutyList = dutyService.getCurrentDutyList();
    	
        if (currentScheduleDutyList.size() == 0) {
            return new ResponseEntity<>(new byte[] {},
                    HttpStatus.BAD_REQUEST);
        }
        
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    //.cacheControl(CacheControl.noCache())
                    .header("Content-Disposition", "attachment; filename=" + "CurrentSchedule.xlsx")
                    .body(CurrentScheduleExportXLSXIO.getExcelCurrentDutyFile(currentScheduleDutyList));
        } catch (IOException e) {
            return new ResponseEntity<>(new byte[] {},
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
