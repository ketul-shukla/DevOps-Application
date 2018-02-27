package com.csye6225.spring2018.controller;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.csye6225.spring2018.user.User;
import com.csye6225.spring2018.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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

    @Value("${server}")
    String server;

    @Value("${accessKey}")
    String accessKey;

    @Value("${secretKey}")
    String secretKey;

    @Value("${bucketName}")
    String bucketName;

    @RequestMapping(value = "/uploadPicture", method = RequestMethod.POST)
    public String addUploadPicture(@RequestParam("imageFile") MultipartFile file, @RequestParam("aboutMe") String aboutMe, Map<String, Object> model, HttpServletRequest request) {
        if (!file.isEmpty()) {
            try {
                String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                String email = request.getSession().getAttribute("emailID").toString();
                if (fileExtension.equalsIgnoreCase("jpeg") || fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("png")) {
                    int index = email.indexOf('@');
                    email = email.substring(0, index);
                }
                else {
                    throw new InvalidObjectException("Invalid image file");
                }

                System.out.println(server);
                if(server.equals("s3bucket")) {
                    String keyName = email + ".jpg";
                    String amazonFileUploadLocationOriginal = bucketName + "/" + "img";

                    String credentials = new String("secretKey=" + secretKey + "\n" + "accessKey=" + accessKey);
                    AmazonS3Client s3Client = new AmazonS3Client(new PropertiesCredentials(new ByteArrayInputStream(credentials.getBytes())));

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
        int index = email.indexOf('@');
        email = email.substring(0, index);

        if(server.equals("s3bucket")) {
            String keyName = email + ".jpg";

            String amazonFileUploadLocationOriginal = bucketName + "/" + "img";

            String credentials = new String("secretKey=" + secretKey + "\n" + "accessKey=" + accessKey);
            AmazonS3Client s3Client = new AmazonS3Client(new PropertiesCredentials(new ByteArrayInputStream(credentials.getBytes())));
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(amazonFileUploadLocationOriginal, keyName);
            s3Client.deleteObject(deleteObjectRequest);
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



//    @RequestMapping(value = "/searchProfile", method = RequestMethod.POST)
//    public String fetchPicture(@RequestParam("search") String email, Map<String, Object> model, HttpServletRequest request) {
//
//        try {
//            User findUser = userRepository.findByEmailID(email);
//            if (findUser == null) {
//                model.put("msg", "User not found");
//                return "error";
//            }
//            String uploadsDir = "/img";
////                String email = request.getSession().getAttribute("emailID").toString();
//            String path = request.getServletContext().getRealPath(uploadsDir);
//            File f = new File(path + File.separator + findUser.getEmailID());
//            System.out.println(path + " " + findUser.getEmailID());
////                    System.out.println(f.getPath());
////                    System.out.println(f.getAbsolutePath());
//            System.out.println(f.exists() + " " + f.isDirectory());
//            if (f.exists() && !f.isDirectory()) {
//                model.put("image", f.getAbsolutePath());
//            } else {
//                model.put("image", path + File.separator + "default.jpg");
//            }
//            model.put("aboutMe", findUser.getAboutMe());
//            model.put("email", findUser.getEmailID());
//            return "search";
//        } catch (Exception e) {
//            model.put("msg", e);
//            return "error";
//        }
//
//    }

    @RequestMapping(value = "/updateAboutMe", method = RequestMethod.POST)
    public String updateAboutMe(@RequestParam("aboutMe") String aboutMe, Map<String, Object> model, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            String email = session.getAttribute("emailID").toString();
            User findUser = userRepository.findByEmailID(email);
            findUser.setAboutMe(aboutMe);
            userRepository.save(findUser);
            return "home";
        } catch (Exception e) {
            model.put("msg", "Please enter correct credentials");
            return "error";
        }

    }
}