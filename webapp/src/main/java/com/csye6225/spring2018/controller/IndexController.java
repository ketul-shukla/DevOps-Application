package com.csye6225.spring2018.controller;

import com.csye6225.spring2018.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;

@Controller
public class IndexController {

  private final static Logger logger = LoggerFactory.getLogger(IndexController.class);

  @Autowired
  private UserRepository userRepository;

  @RequestMapping("/")
  public String index(HttpServletRequest request, Map<String, Object> model) {
    HttpSession session = request.getSession(false);
    if(session == null) {
      logger.info("Loading home page.");
      return "index";
    } else {
      model.put("date", new Date());
      return "home";
    }
  }
}
