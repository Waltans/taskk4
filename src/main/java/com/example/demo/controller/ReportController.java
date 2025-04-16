package com.example.demo.controller;

import com.example.demo.entity.Report;
import com.example.demo.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
	
	private final ReportService reportService;
	
	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}
	
	@PostMapping
	public ResponseEntity<Long> createReport() {
		Report report = reportService.createReport();
		reportService.generateReportAsync(report.getId());
		return ResponseEntity.ok(report.getId());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<String> getReport(@PathVariable Long id) {
		Report report = reportService.getReport(id);
		
		switch (report.getStatus()) {
		case CREATED:
			return ResponseEntity.accepted().body("Report is still being generated");
		case ERROR:
			return ResponseEntity.ok("Report generation failed: " + report.getContent());
		case COMPLETED:
			return ResponseEntity.ok(report.getContent());
		default:
			return ResponseEntity.internalServerError().body("Unknown report status");
		}
	}
}