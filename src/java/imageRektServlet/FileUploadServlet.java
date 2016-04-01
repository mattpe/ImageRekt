package imageRektServlet;

import imageRektDB.Image;
import imageRektDB.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Servlet for uploading files and adding info to db
 */
@WebServlet(name = "upload", urlPatterns = {"/api/v2/upload"})
// save images here, so that they can be easily accessed from outside
@MultipartConfig(location = "/var/www/uploads/")
public class FileUploadServlet extends HttpServlet {
    
    EntityManagerFactory emf;
    EntityManager em;
    private List<Image> imageList;
    Image image;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.addHeader("Access-Control-Allow-Origin", "*");
        
        String filename = "empty-file";
        String fileType = "unknown";
        String mimeType = "unknown";
        
        // Extract data from request
        try {
            filename = request.getPart("file").getSubmittedFileName();
            fileType = request.getParameter("type");
            mimeType = request.getPart("file").getContentType();
        } catch (Exception e) {
            response.setStatus(400);
            out.println("file is missing or corrupted");
            return;
        }
        
        if (filename.length() > 3) {
            filename = someHash(12) + "." + getExtension(filename);
        } else {
            filename = someHash(12) + "." + getExtensionFromMimeType(mimeType);
        }
        
        // Quick and dirty error handling, return (exit) if anything is missing
        boolean badRequest = false;
        List allowedTypes = new ArrayList();
        allowedTypes.add("image");
        allowedTypes.add("audio");
        allowedTypes.add("video");
        if (request.getParameter("title") == null) {
            out.println("title is missing");
            badRequest = true;
        }
        if (request.getParameter("title").length() < 2) {
            out.println("title is too short");
            badRequest = true;
        }
        if (request.getParameter("description") == null) {
            out.println("description is missing");
            badRequest = true;
        }
        if (request.getParameter("description").length() < 2) {
            out.println("description is too short");
            badRequest = true;
        }
        if (!allowedTypes.contains(fileType)) {
            out.println("type is bad or missing");
            badRequest = true;
        }
        if (!fileType.equals(mimeType.substring(0, 5))) {
            out.println("submitted type does not match to mimetype");
            badRequest = true;
        }
        if (badRequest) {
            response.setStatus(400);
            return;
        }

        // Write file data to disk 
        try {
            request.getPart("file").write(filename);
        } catch (IOException | ServletException e) {
            response.setStatus(400);
            out.println("file is corrupted");
            return;
        }

        // Generate thumbnails (& screen capture if video)
        if (!fileType.equals("audio")) {
            BashMediaManipulator.createThumbs(filename, fileType);
        }

        //create a new transaction to add data about the image upload to DB.
        emf = Persistence.createEntityManagerFactory("ImageRektPU");
        em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            User user = (User) em.createNamedQuery("User.findByUid")
                    .setParameter("uid", Integer.parseInt(request.getParameter("user")))
                    .getSingleResult();
            
            Image img = new Image(request.getParameter("title"),
                    request.getParameter("description"),
                    new Date(),
                    filename,
                    user,
                    request.getParameter("type"),
                    mimeType);
            em.persist(img);
            em.getTransaction().commit();
            
            emf = Persistence.createEntityManagerFactory("ImageRektPU");
            em = emf.createEntityManager();
            em.getTransaction().begin();
            this.imageList = em.createNamedQuery("Image.findAll").getResultList();
            image = this.imageList.get(this.imageList.size() - 1);

            //change output to real json object, TODO: fix error handling/messages
            response.setContentType(MediaType.APPLICATION_JSON);
            out.println("{\"fileId\": \"" + image.getIid() + "\"}");
            
            em.getTransaction().commit();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
        emf.close();
    }

    /*
    * Generates some unique filename
    */
    private String someHash(int length) {
        //long timeMillis = System.currentTimeMillis() % 1000;
        //return Long.toString(timeMillis) + "-";
        String hash = RandomStringUtils.randomAlphanumeric(length);
        return hash.toLowerCase();
    }
 
    /*
    * Get file extension from a filename
    */
    private String getExtension(String filename) {
 
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i+1);
        }
        return extension.toLowerCase();
    }

    /*
    * Get file extension from a mimetype
    */
    private String getExtensionFromMimeType(String mime) {
 
        String extension = "";
        int i = mime.lastIndexOf('/');
        if (i > 0) {
            extension = mime.substring(i+1);
        }
        if (extension.equals("jpeg")) {
            extension = "jpg";
        }
        return extension;
    }

}
