package com.csye6225.spring2018.controller;

import com.csye6225.spring2018.user.User;
import com.csye6225.spring2018.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/uploadPicture", method = RequestMethod.POST)
    public String addUploadPicture(@RequestParam("imageFile") MultipartFile file, Map<String, Object> model, HttpServletRequest request) {

            if (!file.isEmpty()) {
                try {
                    String uploadsDir = "/img";
                    String email = request.getSession().getAttribute("emailID").toString();
                    String path = request.getServletContext().getRealPath(uploadsDir);
                    String filename = file.getOriginalFilename();

                    System.out.println(path+" "+email);

                    byte[] bytes = file.getBytes();
                    BufferedOutputStream stream =new BufferedOutputStream(new FileOutputStream(
                            new File(path + File.separator + email)));
                    stream.write(bytes);
                    stream.flush();
                    stream.close();
                    return "home";
                } catch (Exception e) {
                    model.put("msg", e);
                    return "error";
                }

            }
            else {
                return "error";
            }
        }

    @RequestMapping(value = "/fetchPicture", method = RequestMethod.POST)
    public String fetchPicture(@RequestParam("email") String email, Map<String, Object> model, HttpServletRequest request) {

            try {
                String uploadsDir = "/img";
//                String email = request.getSession().getAttribute("emailID").toString();
                String path = request.getServletContext().getRealPath(uploadsDir);
                File f = new File(path + File.separator + email);
                System.out.println(path + " " + email);
                System.out.println(f.getPath());
                System.out.println(f.getAbsolutePath());
                System.out.println(f.exists() + " " + f.isDirectory());
                if(f.exists() && !f.isDirectory()) {
                    System.out.println("File Exists");
                }
                else {
                    System.out.println("File not found");
                }
                return "home";
            } catch (Exception e) {
                model.put("msg", e);
                return "error";
            }

        }

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
