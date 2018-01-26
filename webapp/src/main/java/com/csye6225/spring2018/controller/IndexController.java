package com.csye6225.spring2018.controller;

import com.csye6225.spring2018.user.User;
import com.csye6225.spring2018.user.UserRepository;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class IndexController {

  private final static Logger logger = LoggerFactory.getLogger(IndexController.class);

  @Autowired
  private UserRepository userRepository;

  @RequestMapping("/")
  public String index() {
    logger.info("Loading home page.");
    return "index";
  }

  @RequestMapping(value = "/createAccount", method = RequestMethod.POST)
  @ResponseBody
  public String CreateAccount(@RequestParam("email") String emailAddress, @RequestParam("pass") String password) {

    String userId = "";
    User user = userRepository.findByEmailID(emailAddress);
    if(user == null) {

    User newUser = new User();
    newUser.setEmailID(emailAddress);
    newUser.setPassword(password);
    logger.info("Email: " + emailAddress);
    logger.info("Password: " + password);
    userRepository.save(newUser);

    return "Account Created Successfully";
    }
    else {
      return "User already exists";
    }
  }
}
