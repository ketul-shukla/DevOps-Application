package com.csye6225.spring2018.controller;

import com.csye6225.spring2018.user.User;
import com.csye6225.spring2018.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Map;

@Controller
public class UserRegisterController {

    private final static Logger logger = LoggerFactory.getLogger(UserRegisterController.class);

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/createAccount", method = RequestMethod.POST)
    public String CreateAccount(@RequestParam("emailID") String emailAddress, @RequestParam("password") String password, Map<String, Object> model) {

        String userId = "";
        User user = userRepository.findByEmailID(emailAddress);
        if(user == null) {
            User newUser = new User();
            newUser.setEmailID(emailAddress);
            newUser.setPassword(password);
            logger.info("Email: " + emailAddress);
            logger.info("Password: " + password);
            userRepository.save(newUser);
            model.put("date", new Date());
            return "home";
        }
        else {
            model.put("msg", "Account already exists");
            return "error";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String verifyLogin(@RequestParam("emailID") String emailID, @RequestParam("password") String password, Map<String, Object> model) {
        User findUser = userRepository.findByEmailIDAndPassword(emailID, password);
        if(findUser != null) {
            model.put("date", new Date());
            return "home";
        } else {
            model.put("msg", "Please enter correct credentials");
            return "error";
        }
    }
}
