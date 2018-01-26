package com.csye6225.spring2018.controller;

import com.csye6225.spring2018.user.User;
import com.csye6225.spring2018.user.UserRepository;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    User user = new User();
    user.setEmailID(emailAddress);
    user.setPassword(password);
    userRepository.save(user);
    return "Account Created Successfully";
  }

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  @ResponseBody
  public String verifyLogin(@RequestParam("email") String emailID, @RequestParam("pass") String password) {
    User findUser = userRepository.findByEmailID(emailID);
    if(findUser.getEmailID().equals(emailID) && findUser.getPassword().equals(password)) {
      Date date = new Date();
      return date.toString();
    } else {
      return "Please enter correct credentials.";
    }
  }

}
