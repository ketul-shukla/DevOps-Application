package com.csye6225.spring2018.controller;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.csye6225.spring2018.user.User;
import com.csye6225.spring2018.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

@Controller
public class UserRegisterController {

    private final static Logger logger = LoggerFactory.getLogger(UserRegisterController.class);

    private HttpSession session;

    @Autowired
    private UserRepository userRepository;

    @Value("${aws.cloudformation.bucket.name}")
    String bucketName;

    @Value("${spring.profiles.active}")
    String profile;

    @RequestMapping(value = "/createAccount", method = RequestMethod.GET)
    public String createNewAccount() {
        return "signup";
    }

    @RequestMapping(value = "/createAccount", method = RequestMethod.POST)
    public String createAccount(@RequestParam("emailID") String emailAddress, @RequestParam("password") String password, Map<String, Object> model, HttpServletRequest request) throws IOException {
        User user = userRepository.findByEmailID(emailAddress);
        if(user == null) {
            session = request.getSession();
            password = hashPassword(password);
            User newUser = new User();
            newUser.setEmailID(emailAddress);
            newUser.setPassword(password);
            logger.info("Email: " + emailAddress);
            logger.info("Password: " + password);
            userRepository.save(newUser);
            model.put("date", new Date());
            session.setAttribute("emailID", emailAddress);
            if(profile.equals("aws")) {
                String amazonFileUploadLocationOriginal = bucketName + "/" + "img";
              AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                        .withCredentials(new InstanceProfileCredentialsProvider(false))
                        .build();
                URL s = s3Client.getUrl(amazonFileUploadLocationOriginal, "default.jpg");
                System.out.println(s);
                model.put("image", s);
            }
            else {
                model.put("image", "/image/default.jpg");
            }
            return "home";
        }
        else {
            model.put("msg", "Account already exists");
            return "error";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String verifyLogin(@RequestParam("emailID") String emailID, @RequestParam("password") String password, Map<String, Object> model, HttpServletRequest request) {
        try{
            User findUser = userRepository.findByEmailID(emailID);
            boolean passwordVerification = BCrypt.checkpw(password, findUser.getPassword());
            if(passwordVerification) {

                session = request.getSession();
                session.setAttribute("emailID", findUser.getEmailID());
                String aboutMe = findUser.getAboutMe();
                model.put("date", new Date());

                int index = emailID.indexOf('@');
                String email = emailID.substring(0,index);

                System.out.println(profile);

                if(profile.equals("aws")) {

                    logger.info("BucketName : " + bucketName);
                    logger.info("Profile : " + profile);

                    String keyName = email + ".jpg";
                    String amazonFileUploadLocationOriginal = bucketName + "/" + "img";
                  AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                            .withCredentials(new InstanceProfileCredentialsProvider(false))
                            .build();
                    System.out.println(s3Client.doesObjectExist(bucketName, keyName));
                    if(!s3Client.doesObjectExist(amazonFileUploadLocationOriginal, keyName)) {
                        URL imageUrl = s3Client.getUrl(amazonFileUploadLocationOriginal, "default.jpg");
                        System.out.println(imageUrl);
                        model.put("image", imageUrl);
                    }
                    else {
                        URL imageUrl = s3Client.getUrl(amazonFileUploadLocationOriginal, keyName);
                        System.out.println(imageUrl);
                        model.put("image", imageUrl);
                    }
                }
                else {
                    Path path = Paths.get(request.getServletContext().getRealPath("image"));
                    File f = new File(path + File.separator + email + ".jpg");

                    if (f.exists() && !f.isDirectory()) {
                        model.put("image", "/image/"+email+".jpg");
                    } else {
                        model.put("image", "/image/default.jpg");
                    }
                }
                model.put("aboutMe", aboutMe);
                return "home";
            } else {
                model.put("msg", "Please enter correct credentials");
                return "error";
            }
        } catch(Exception e){
            model.put("msg", "Please enter correct credentials");
            e.printStackTrace();
            return "error";
        }

    }

    @RequestMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "index";
    }

    public static String hashPassword(String password) {
        String salt = BCrypt.gensalt(12);
        String hashed_password = BCrypt.hashpw(password, salt);

        return(hashed_password);
    }

}
