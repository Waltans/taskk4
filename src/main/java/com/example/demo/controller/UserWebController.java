package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserWebController {
	
	private final UserService userService;
	
	public UserWebController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping
	public String getAllUsers(Model model) {
		model.addAttribute("users", userService.findAll());
		return "users/list";
	}
}