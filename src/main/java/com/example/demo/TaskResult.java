package com.example.demo;

public record TaskResult<T>(T result, long executionTime) {}