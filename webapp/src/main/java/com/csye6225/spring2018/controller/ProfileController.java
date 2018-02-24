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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String addUploadPicture(@RequestParam("imageFile") MultipartFile file, @RequestParam("aboutMe") String aboutMe, Map<String, Object> model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

            if (!file.isEmpty()) {
                try {
                    String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                    if(fileExtension.equalsIgnoreCase("jpeg") || fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("png")) {
                      Path path = Paths.get(request.getServletContext().getRealPath("image"));
                      String email = request.getSession().getAttribute("emailID").toString();
                      int index = email.indexOf('@');
                      email = email.substring(0,index);
                      File f = new File(path + File.separator + email + ".jpg");
                      String imgPath = path.toUri()+ email+"."+fileExtension;
                      if(f.exists()) {
                          f.delete();
                      }
                      model.put("picUrl", "/image/"+email+".jpg");
//                      model.put("picUrl", imgPath);
                      System.out.println(path.toUri()+email+"."+fileExtension);
                      FileOutputStream fileOutputStream = new FileOutputStream(f);
                      fileOutputStream.write(file.getBytes());
                      fileOutputStream.flush();
                      fileOutputStream.close();
                      return "home";
                    }
                    else {
                        model.put("msg", "Invalid File");
                        return "error";
                    }
                } catch (Exception e) {
                    model.put("msg", e);
                    return "error";
                }

            }
            else {
                return "error";
            }
    }

//    @RequestMapping(value = "/addImg", method = RequestMethod.GET)
//    public String img(Map<String, Object> model, HttpServletRequest request) {
//        String email = request.getSession().getAttribute("emailID").toString();
//        int index = email.indexOf('@');
//        email = email.substring(0,index);
//
//
//        return "home";
//    }


    @RequestMapping(value = "/searchProfile", method = RequestMethod.POST)
    public String fetchPicture(@RequestParam("search") String email, Map<String, Object> model, HttpServletRequest request) {

            try {
                User findUser = userRepository.findByEmailID(email);
                if(findUser == null) {
                    model.put("msg", "User not found");
                    return "error";
                }
                String uploadsDir = "/img";
//                String email = request.getSession().getAttribute("emailID").toString();
                String path = request.getServletContext().getRealPath(uploadsDir);
                File f = new File(path + File.separator + findUser.getEmailID());
                System.out.println(path + " " + findUser.getEmailID());
//                    System.out.println(f.getPath());
//                    System.out.println(f.getAbsolutePath());
                System.out.println(f.exists() + " " + f.isDirectory());
                if(f.exists() && !f.isDirectory()) {
                    model.put("image", f.getAbsolutePath());
                }
                else {
                    model.put("image", path + File.separator + "default.jpg");
                }
                model.put("aboutMe", findUser.getAboutMe());
                model.put("email", findUser.getEmailID());
                return "search";
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
