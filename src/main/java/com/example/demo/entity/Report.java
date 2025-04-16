package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private ReportStatus status;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	private LocalDateTime createdAt;
	private LocalDateTime completedAt;
	
	// Constructors
	public Report() {
		this.status = ReportStatus.CREATED;
		this.createdAt = LocalDateTime.now();
	}
	
	// Getters and Setters
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public ReportStatus getStatus() {
		return status;
	}
	
	public void setStatus(ReportStatus status) {
		this.status = status;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public LocalDateTime getCompletedAt() {
		return completedAt;
	}
	
	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}
	
	public enum ReportStatus {
		CREATED, COMPLETED, ERROR
	}
}