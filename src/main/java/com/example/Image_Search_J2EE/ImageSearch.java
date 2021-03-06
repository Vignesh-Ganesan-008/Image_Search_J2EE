package com.example.Image_Search_J2EE;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.json.JSONArray;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;;

@WebServlet(name = "ImageSearchServlet", value = "/image_search-servlet")
public class ImageSearch extends HttpServlet {
    final String postEndpoint = "http://127.0.0.1:8000/";
    public void init() {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String url = request.getParameter("url");
        String k =  request.getParameter("k");
        String inputJson = "{ \"url\":\"" + url + "\", \"k\":"+k+" }";
        FileUtils.cleanDirectory(new File("C:\\Users\\Vicky\\Documents\\Projects\\Image_Search_J2EE\\src\\main\\webapp\\images"));

        var image_request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson))
                .build();
        var client = HttpClient.newHttpClient();

        try {
            var image_response = client.send(image_request, HttpResponse.BodyHandlers.ofString());
            System.out.println(image_response.statusCode());

            //Random random = new Random();
            //String randomNumber = String.valueOf(random.nextInt(10000));

            JSONArray json_data = new JSONArray(image_response.body());
            JSONObject object = json_data.getJSONObject(0);
            for(int i=0; i < Integer.parseInt(k); i++) {
                String encoded_image = object.getJSONArray("Images").getString(i);
                byte[] s = encoded_image.getBytes(StandardCharsets.US_ASCII);
                Base64.Decoder decoder = Base64.getDecoder();
                FileUtils.writeByteArrayToFile(new File("C:\\Users\\Vicky\\Documents\\Projects\\Image_Search_J2EE\\src\\main\\webapp\\images\\"+i+".jpg"), decoder.decode(s));
            }

            List imageUrlList = new ArrayList();
            File imageDir = new File("C:\\Users\\Vicky\\Documents\\Projects\\Image_Search_J2EE\\src\\main\\webapp\\images");
            for(File imageFile : imageDir.listFiles()){
                String imageFileName = imageFile.getName();

                // add this images name to the list we are building up
                imageUrlList.add("images/"+imageFileName);

            }
            System.out.println(imageUrlList);
            request.setAttribute("imageUrlList", imageUrlList);
            //request.setAttribute("randomNumber", randomNumber);
            Thread.sleep(3000);
            RequestDispatcher req = request.getRequestDispatcher("MainPage.jsp");
            req.include(request, response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
            }
            System.out.println("Cookie deleted");
        }
        RequestDispatcher req = request.getRequestDispatcher("index.jsp");
        req.include(request, response);
    }
}
