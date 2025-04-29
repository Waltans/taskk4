package com.example.demo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationAndLoginTest {
	
	private WebDriver driver;
	private WebDriverWait wait;
	private final String baseUrl = "http://localhost:8080";
	private final String testUsername = "user_" + UUID.randomUUID().toString().substring(0, 8);
	private final String testPassword = "TestPass123!";
	private final String testStreet = "Test Street 123";
	
	@BeforeEach
	public void setUp() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		driver.manage().window().maximize();
	}
	
	@Test
	public void testSuccessfulRegistrationAndLogin() {
		// 1. Регистрация нового пользователя
		registerNewUser();
		
		// 2. Вход с только что созданными учетными данными
		loginWithCredentials(testUsername, testPassword);
		
		// 3. Проверка успешного входа
		assertTrue(isUserLoggedIn(), "Пользователь не смог войти после регистрации");
	}
	
	private void registerNewUser() {
		// Переход на страницу регистрации
		driver.get(baseUrl + "/register");
		assertEquals("Registration", driver.getTitle(), "Не удалось загрузить страницу регистрации");
		
		// Заполнение формы регистрации
		WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(
			By.xpath("//input[@type='text' and @th\\:field='*{name}']")));
		WebElement passwordField = driver.findElement(By.xpath("//input[@type='password' and @th\\:field='*{password}']"));
		WebElement streetField = driver.findElement(By.name("street"));
		WebElement registerButton = driver.findElement(By.xpath("//button[text()='Register']"));
		
		usernameField.sendKeys(testUsername);
		passwordField.sendKeys(testPassword);
		streetField.sendKeys(testStreet);
		registerButton.click();
		
		// Проверка перенаправления на страницу входа
		wait.until(ExpectedConditions.titleIs("Login"));
	}
	
	private void loginWithCredentials(String username, String password) {
		// Убедимся, что мы на странице входа
		if (!driver.getTitle().equals("Login")) {
			driver.get(baseUrl + "/login");
		}
		
		// Заполнение формы входа
		WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(
			By.name("username")));
		WebElement passwordField = driver.findElement(By.name("password"));
		WebElement loginButton = driver.findElement(By.xpath("//button[text()='Login']"));
		
		usernameField.sendKeys(username);
		passwordField.sendKeys(password);
		loginButton.click();
	}
	
	private boolean isUserLoggedIn() {
		try {
			// Проверка появления элемента, который виден только после входа
			// Замените на актуальный для вашего приложения
			return wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//*[contains(text(), 'Welcome') or contains(text(), 'Dashboard')]"))).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}
	
	@AfterEach
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}
}