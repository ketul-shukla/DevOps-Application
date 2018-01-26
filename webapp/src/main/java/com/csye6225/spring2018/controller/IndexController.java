package com.csye6225.spring2018.controller;

import com.csye6225.spring2018.user.User;
import com.csye6225.spring2018.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class IndexController {

  private final static Logger logger = LoggerFactory.getLogger(IndexController.class);
  
  @RequestMapping("/")
  public String index() {
    logger.info("Loading home page.");
    return "index";
  }

  @Autowired
  private UserRepository userRepository;

  @RequestMapping(value = "/createAccount", method = RequestMethod.POST)
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
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
