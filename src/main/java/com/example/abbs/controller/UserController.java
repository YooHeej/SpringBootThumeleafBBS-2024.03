package com.example.abbs.controller;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.abbs.entity.User;
import com.example.abbs.service.UserService;
import com.example.abbs.util.ImageUtil;

@Controller
@RequestMapping("/user")
public class UserController {
	private final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired private UserService uSvc;
	@Autowired private ImageUtil imageUtil;
	@Value("${spring.servlet.multipart.location}") private String uploadDir; 
	
	@GetMapping("/register")
	public String registerForm() {
		return "user/register";
	}
	
	@PostMapping("/register")
	public String registerProc(MultipartHttpServletRequest req, Model model, String uid,
			String pwd, String pwd2, String uname, String email, String github, String insta, String location) {
		String filename = null; 
		MultipartFile filePart = req.getFile("profile");
		
		if (uSvc.getUserByUid(uid) != null) {
			model.addAttribute("msg", "사용자 ID가 중복되었습니다.");
			model.addAttribute("url", "/abbs/user/register");
			return "common/alertMsg";
		}
	   if (pwd.equals(pwd2) && pwd !=null) {
		   if (filePart.getContentType().contains("image")) {
			   filename = filePart.getOriginalFilename();
			   String path = uploadDir + "profile/" + filename;
			   try {
				    filePart.transferTo(new File(path));
			   	} catch (Exception e) {
				e.printStackTrace(); 
				}
			   filename = imageUtil.squareImage(uid, filename);
			   
		   }
		   User user = new User(uid, pwd, uname, email, filename, github, insta, location);
		   uSvc.registerUser(user);
		   model.addAttribute("msg", "등록을 마쳤다. 로그인");
		   model.addAttribute("url", "/abbs/user/login");
		   return "redirect:/user/login";
		} else {
			model.addAttribute("msg", "패스워드 틀렸다.");
			model.addAttribute("url", "/abbs/user/register");
			return "common/alertMsg";
		}
	}	
	
	@GetMapping("/login")
	public String loginForm() {
		return "user/login";
	}
}
