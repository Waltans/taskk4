package com.example.demo.service;

import com.example.demo.TaskResult;
import com.example.demo.entity.Report;
import com.example.demo.entity.User;
import com.example.demo.repository.ReportRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class ReportService {
	
	private final ReportRepository reportRepository;
	private final UserRepository userRepository;
	private final Executor executor;
	
	public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
		this.reportRepository = reportRepository;
		this.userRepository = userRepository;
		this.executor = Executors.newFixedThreadPool(2);
	}
	
	public Report createReport() {
		Report report = new Report();
		return reportRepository.save(report);
	}
	
	public Report getReport(Long id) {
		return reportRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Report not found"));
	}
	
	public CompletableFuture<Void> generateReportAsync(Long reportId) {
		return CompletableFuture.runAsync(() -> {
			Report report = reportRepository.findById(reportId)
				.orElseThrow(() -> new RuntimeException("Report not found"));
			
			try {
				LocalDateTime reportStartTime = LocalDateTime.now();
				
				CompletableFuture<TaskResult<Long>> userCountFuture = CompletableFuture.supplyAsync(() -> {
					long startTime = System.currentTimeMillis();
					long count = userRepository.count();
					long elapsed = System.currentTimeMillis() - startTime;
					return new TaskResult<>(count, elapsed);
				}, executor);
				
				CompletableFuture<TaskResult<List<User>>> usersFuture = CompletableFuture.supplyAsync(() -> {
					long startTime = System.currentTimeMillis();
					List<User> users = userRepository.findAll();
					long elapsed = System.currentTimeMillis() - startTime;
					return new TaskResult<>(users, elapsed);
				}, executor);
				
				CompletableFuture.allOf(userCountFuture, usersFuture)
					.exceptionally(ex -> {
						report.setStatus(Report.ReportStatus.ERROR);
						report.setContent("Error generating report: " + ex.getMessage());
						return null;
					})
					.thenRun(() -> {
						if (report.getStatus() != Report.ReportStatus.ERROR) {
							TaskResult<Long> userCountResult = userCountFuture.join();
							TaskResult<List<User>> usersResult = usersFuture.join();
							
							String htmlReport = generateHtmlReport(
								userCountResult.result(),
								userCountResult.executionTime(),
								usersResult.result(),
								usersResult.executionTime(),
								Duration.between(reportStartTime, LocalDateTime.now()).toMillis()
							);
							
							reportSetContent(report, htmlReport);
						}
					})
					.join();
			} catch (Exception e) {
				report.setStatus(Report.ReportStatus.ERROR);
				report.setContent("Error generating report: " + e.getMessage());
			}
			
			reportRepository.save(report);
		});
	}
	
	private static void reportSetContent(Report report, String htmlReport) {
		report.setContent(htmlReport);
		report.setStatus(Report.ReportStatus.COMPLETED);
		report.setCompletedAt(LocalDateTime.now());
	}
	
	private String generateHtmlReport(long userCount, long userCountTime,
		List<User> users, long addressListTime,
		long totalTime) {
		StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE html><html><head><title>Application Report</title>");
		html.append("<style>table {border-collapse: collapse;} th, td {border: 1px solid black; padding: 8px;}</style>");
		html.append("</head><body>");
		html.append("<h1>Application Statistics Report</h1>");
		
		html.append("<h2>User Statistics</h2>");
		html.append("<p>Total registered users: ").append(userCount).append("</p>");
		html.append("<p>Time taken to count users: ").append(userCountTime).append(" ms</p>");
		
		html.append("<h2>User List</h2>");
		html.append("<p>Time taken to fetch users: ").append(addressListTime).append(" ms</p>");
		html.append("<table><tr><th>ID</th><th>Username</th><th>Address</th></tr>");
		
		for (User user : users) {
			html.append("<tr>");
			html.append("<td>").append(user.getId()).append("</td>");
			html.append("<td>").append(user.getName()).append("</td>");
			html.append("<td>").append(user.getAddress() != null ? user.getAddress().getStreet() : "N/A").append("</td>");
			html.append("</tr>");
		}
		
		html.append("</table>");
		
		html.append("<h2>Summary</h2>");
		html.append("<p>Total report generation time: ").append(totalTime).append(" ms</p>");
		
		html.append("</body></html>");
		return html.toString();
	}
}