package com.csye6225.spring2018.controller;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.csye6225.spring2018.user.User;
import com.csye6225.spring2018.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Value("${aws.cloudformation.bucket.name}")
    String bucketName;

    @Value("${spring.profiles.active}")
    String profile;

    @RequestMapping(value = "/uploadPicture", method = RequestMethod.POST)
    public String addUploadPicture(@RequestParam("imageFile") MultipartFile file, Map<String, Object> model, HttpServletRequest request) {
        if (!file.isEmpty()) {
            try {
                String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                String email = request.getSession().getAttribute("emailID").toString();
                model.put("email", email);
                User findUser = userRepository.findByEmailID(email);
                model.put("aboutMe", findUser.getAboutMe());
                if (fileExtension.equalsIgnoreCase("jpeg") || fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("png")) {
                    int index = email.indexOf('@');
                    email = email.substring(0, index);
                }
                else {
                    throw new InvalidObjectException("Invalid image file");
                }

                if(profile.equals("aws")) {
                    String keyName = email + ".jpg";
                    String amazonFileUploadLocationOriginal = bucketName + "/" + "img";

                   AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                            .withCredentials(new InstanceProfileCredentialsProvider(false))
                            .build();

                    FileInputStream stream = (FileInputStream) file.getInputStream();
                    ObjectMetadata objectMetadata = new ObjectMetadata();
                    PutObjectRequest putObjectRequest = new PutObjectRequest(amazonFileUploadLocationOriginal, keyName, stream, objectMetadata);
                    putObjectRequest.setCannedAcl(CannedAccessControlList.PublicReadWrite);
                    PutObjectResult result = s3Client.putObject(putObjectRequest);
                    System.out.println("Etag:" + result.getETag() + "-->" + result);

                    URL imageUrl = s3Client.getUrl(amazonFileUploadLocationOriginal, keyName);
                    System.out.println(imageUrl);
                    model.put("image", imageUrl);
                }
                else {
                    Path path = Paths.get(request.getServletContext().getRealPath("image"));
                    File f = new File(path + File.separator + email + ".jpg");
                    model.put("image", "/image/"+email+".jpg");
                    FileOutputStream fileOutputStream = new FileOutputStream(f);
                    fileOutputStream.write(file.getBytes());
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                return "home";
            } catch (Exception e) {
                model.put("msg", e);
                e.printStackTrace();
                return "error";
            }
        } else {
            return "error";
        }
    }

    @RequestMapping(value = "/deletePicture", method = RequestMethod.POST)
    public String addUploadPicture(Map<String, Object> model, HttpServletRequest request) throws IOException {
        String email = request.getSession().getAttribute("emailID").toString();
        model.put("email", email);
        User findUser = userRepository.findByEmailID(email);
        model.put("aboutMe", findUser.getAboutMe());
        int index = email.indexOf('@');
        email = email.substring(0, index);

        if(profile.equals("aws")) {
            String keyName = email + ".jpg";

            String amazonFileUploadLocationOriginal = bucketName + "/" + "img";

          AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new InstanceProfileCredentialsProvider(false))
                    .build();
            if(s3Client.doesObjectExist(amazonFileUploadLocationOriginal, keyName)){
                DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(amazonFileUploadLocationOriginal, keyName);
                s3Client.deleteObject(deleteObjectRequest);
            }
            URL imageUrl = s3Client.getUrl(amazonFileUploadLocationOriginal, "default.jpg");
            System.out.println(imageUrl);
            model.put("image", imageUrl);
        }
        else {
            Path path = Paths.get(request.getServletContext().getRealPath("image"));
            File f = new File(path + File.separator + email + ".jpg");
            if(f.exists()) {
                f.delete();
            }
            model.put("image", "/image/default.jpg");
        }
        return "home";
    }

    @RequestMapping(value = "/updateAboutMe", method = RequestMethod.POST)
    public String deletePicture(@RequestParam("aboutMe") String aboutMe, Map<String, Object> model, HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        String  email = session.getAttribute("emailID").toString();
        User findUser = userRepository.findByEmailID(email);
        findUser.setAboutMe(aboutMe);
        userRepository.save(findUser);

        model.put("email", email);
        model.put("aboutMe", findUser.getAboutMe());
        if(profile.equals("aws")) {
            String keyName = email + ".jpg";

            String amazonFileUploadLocationOriginal = bucketName + "/" + "img";

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new InstanceProfileCredentialsProvider(false))
                    .build();
            System.out.println(s3Client.doesObjectExist(amazonFileUploadLocationOriginal, keyName));
            if(s3Client.doesObjectExist(amazonFileUploadLocationOriginal, keyName)) {
                URL s = s3Client.getUrl(amazonFileUploadLocationOriginal, keyName);
                System.out.println(s);
                model.put("image", s);
            }
            else {
                URL s = s3Client.getUrl(amazonFileUploadLocationOriginal, "default.jpg");
                System.out.println(s);
                model.put("image", s);
            }
            return "home";

        }
        else {
            int index = email.indexOf('@');
            email = email.substring(0, index);
            Path path = Paths.get(request.getServletContext().getRealPath("image"));
            File f = new File(path + File.separator + email + ".jpg");
            if(f.exists()) {
                model.put("image", "/image/"+email+".jpg");
            }
            else {
                model.put("image", "/image/default.jpg");
            }
            return "home";
        }
    }

    @RequestMapping(value = "/searchProfile", method = RequestMethod.POST)
    public String fetchPicture(@RequestParam("search") String email, Map<String, Object> model, HttpServletRequest request) throws IOException {

        try {
            User findUser = userRepository.findByEmailID(email);
            if (findUser == null) {
                model.put("msg", "User not found");
                return "error";
            }

            model.put("email", email);
            model.put("aboutMe", findUser.getAboutMe());

            int index = email.indexOf('@');
            email = email.substring(0, index);

            if(profile.equals("aws")) {
                String keyName = email + ".jpg";

                String amazonFileUploadLocationOriginal = bucketName + "/" + "img";
                AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                        .withCredentials(new InstanceProfileCredentialsProvider(false))
                        .build();

                System.out.println(s3Client.doesObjectExist(amazonFileUploadLocationOriginal, keyName));
                if (s3Client.doesObjectExist(amazonFileUploadLocationOriginal, keyName)) {
                    URL s = s3Client.getUrl(amazonFileUploadLocationOriginal, keyName);
                    System.out.println(s);
                    model.put("image", s);
                } else {
                    URL s = s3Client.getUrl(amazonFileUploadLocationOriginal, "default.jpg");
                    System.out.println(s);
                    model.put("image", s);
                }
            }
            else {
                Path path = Paths.get(request.getServletContext().getRealPath("image"));
                File f = new File(path + File.separator + email + ".jpg");
                if(f.exists()) {
                    model.put("image", "/image/"+email+".jpg");
                }
                else {
                    model.put("image", "/image/default.jpg");
                }
            }
            return "search";

        } catch (Exception e) {
            model.put("msg", e);
            return "error";
        }

    }
}