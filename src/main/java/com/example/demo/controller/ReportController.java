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
		
		return switch (report.getStatus()) {
			case CREATED -> ResponseEntity.accepted()
				.body("Report is still being generated");
			case ERROR -> ResponseEntity.ok("Report generation failed: " + report.getContent());
			case COMPLETED -> ResponseEntity.ok(report.getContent());
		};
	}
}