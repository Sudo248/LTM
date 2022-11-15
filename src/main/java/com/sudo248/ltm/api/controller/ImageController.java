package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.image.Image;
import com.sudo248.ltm.api.utils.ImageUtils;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@WsController(path = "/upload/images")
public class ImageController implements WebSocketController<Request<Image>, Response<String>> {

    private Logger logger = LoggerFactory.getLogger(ImageController.class.getSimpleName());
    private static final String uploadFolder = "src/main/webapp/WEB-INF/images";
    @Override
    public void onGet(Request<Image> request, Response<String> response) {

    }

    @Override
    public void onPost(Request<Image> request, Response<String> response) {
       try {
           Image image = request.getPayload();
           BufferedImage bufImage = ImageIO.read(new ByteArrayInputStream((image.getContent())));
           File fileImage = new File(uploadFolder+"/"+System.currentTimeMillis()+"_"+image.getName());
           ImageIO.write(bufImage, getExtension(image.getName()), fileImage);
//           String imageUrl = ImageUtils.getUrlImage(fileImage.getName());
           String imageUrl = fileImage.getName();
           logger.info("Received image url: " + imageUrl);
            response.setCode(200);
            response.setMessage("Upload image: " + fileImage.getName() + " success");
            response.setPayload(imageUrl);
       } catch (Exception e) {
           e.printStackTrace();
           logger.error(e.getMessage());
           response.setCode(401);
           response.setMessage(e.getMessage());
       }
    }

    private String getExtension(String path) {
        return path.substring(path.lastIndexOf('.')+1);
    }
}
