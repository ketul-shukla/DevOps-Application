package com.csye6225.spring2018.controller;

import com.csye6225.spring2018.user.User;
import com.csye6225.spring2018.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

public class ProfileController {

    @RequestMapping(value = "/addUploadPicture", method = RequestMethod.POST)
    public String addUploadPicture(@RequestParam("file") MultipartFile file, Map<String, Object> model, HttpServletRequest request) {
        try{
            HttpSession session = request.getSession();
            String  email = session.getAttribute("emailID").toString();
            String name = file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            Path path = Paths.get(".//webapp//img//" + email);
            Files.write(path, bytes);

                return "home";
        } catch(Exception e){
            model.put("msg", "Please enter correct credentials");
            return "error";
        }

    }

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/updateAboutMe", method = RequestMethod.POST)
    public String updateAboutMe(@RequestParam ("aboutMe") String aboutMe, Map<String, Object> model, HttpServletRequest request) {
        try{
            HttpSession session = request.getSession();
            String  email = session.getAttribute("emailID").toString();
            User findUser = userRepository.findByEmailID(email);
            findUser.setAboutMe(aboutMe);
            userRepository.save(findUser);
            return "home";
        } catch(Exception e){
            model.put("msg", "Please enter correct credentials");
            return "error";
        }

    }
}
