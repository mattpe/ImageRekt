package imageRektDB;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PostPersist;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 * Responses should follow: https://google.github.io/styleguide/jsoncstyleguide.xml
 */
@Path("v2")
public class GenericResource {

    EntityManagerFactory emf;
    EntityManager em;
    JsonArray jsonImageArray;
    JsonArray emptyJsonArray;
    JsonObject jsonImageObject;
    JsonObjectBuilder jsonObjectBuilder;
    private int userSearchTerm = 0;
    private int imageRating = 0;
    private int userRating;
    private int searchUID;
    private int searchIID;
    private int searchTID;
    private int randomIID;
    private String imageDesc = "";
    private String uploadTime = "";
    private String parsedUploadTime = "";
    private Random randomno;
    private ArrayList<Image> userImageArray;
    private ArrayList<String> userArrayList;
    private ArrayList<String> galleryImages;
    private Collection<User> userCollection;
    private Collection<Image> imageCollection;
    private Collection<Tag> tagCollection;
    private List<Image> imageList;
    private List<Image> imageQuery;
    private List<Rate> rateList;
    private List<User> userList;
    private List<Tag> tagList;
    private List<Tag> newTagList;
    private List<String> tagContentList;
    private User newUser;
    private User queryUser;
    private Image queryImage;
    private Rate newRate;
    private Comment newComment;
    private Tag newTag;
    private Tag queryTag;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
    }

    /**
     * USERS
     */

    // check if username already exists
    @POST
    @Path("user/exists")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/json")
    public JsonObject checkUsername(@FormParam("username") String username) {
        createTransaction();
        this.userList = em.createNamedQuery("User.findByUname").setParameter("uname", username).getResultList();
        endTransaction();
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("userFound", !this.userList.isEmpty());
        return job.build();
    }

    // gets username and password and checks if matching user is found
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/json")
    public Response checkUserLogin(
            @FormParam("username") String username,
            @FormParam("password") String password) {
        JsonObjectBuilder job = Json.createObjectBuilder();
        createTransaction();
        this.userList = em.createNamedQuery("User.findAll").getResultList();
        for (User u : this.userList) {
            if (u.getUname().equals(username) && u.getUpass().equals(password)) {
                job.add("userId", u.getUid());
                job.add("status", "login ok");
                endTransaction();
                return Response
                        .ok(job.build())
                        .build();
            }
        }
        endTransaction();
        job.add("error", "wrong username or password");
        return Response
                .accepted(job.build())
                .build();
    }

    // register a new user
    @POST
    @Path("register")
    //@Consumes(MediaType.APPLICATION_JSON) //-> does not work without some tricks:
    //http://stackoverflow.com/questions/17970969/get-simple-json-parameter-from-a-json-request-in-jax-rs
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/json")
    public Response registerUser(
            @FormParam("username") String username,
            @FormParam("email") String email,
            @FormParam("password") String password) {

        createTransaction();
        JsonObjectBuilder job = Json.createObjectBuilder();
        this.userList = em.createNamedQuery("User.findAll").getResultList();
        for (User u : this.userList) {
            if (u.getUname().equalsIgnoreCase(username)) {
                endTransaction();
                job.add("error", "username already exists");
                return Response
                        .accepted(job.build())
                        .build();
                   
            }
        }
        newUser = new User();
        newUser.setUname(username);
        newUser.setUpass(password);
        newUser.setUemail(email);
        em.persist(newUser);
        endTransaction();
        // what if the user creation failed
        job.add("status", "ok");
        job.add("message", "user " + username + " added");
        return Response
                .ok(job.build())
                .build();
    }

    // Get user by id
    @GET
    @Path("user/{id}")
    @Produces("application/json")
    public Response getUserById(@PathParam("id") String uid) {

        Response response;
        jsonObjectBuilder = Json.createObjectBuilder();
        this.userSearchTerm = Integer.parseInt(uid);
        createTransaction();
        try {
            User u = (User) em.createNamedQuery("User.findByUid")
                    .setParameter("uid", this.userSearchTerm)
                    .getSingleResult();
            jsonObjectBuilder.add("username", u.getUname());
            jsonObjectBuilder.add("email", u.getUemail());
            jsonObjectBuilder.add("userId", u.getUid());
            response = Response.ok(jsonObjectBuilder.build()).build();
        } catch (NoResultException e) {
            jsonObjectBuilder.add("error", "not found");
            response = Response.status(404).entity(jsonObjectBuilder.build()).build();
        }
        endTransaction();
        return response;
    }

    // Get all users
    @GET
    @Path("users")
    @Produces("application/json")
    public JsonArray getUsers() {
        JsonArray response;
        jsonObjectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        createTransaction();
        try {
            List<User> userQuery = em.createNamedQuery("User.findAll").getResultList();
            for (User u : userQuery) {
                jsonObjectBuilder.add("username", u.getUname());
                jsonObjectBuilder.add("email", u.getUemail());
                jsonObjectBuilder.add("userId", u.getUid());
                jsonArrayBuilder.add(jsonObjectBuilder.build());
            }
            response = jsonArrayBuilder.build();
        } catch (Exception e) {
            jsonObjectBuilder.add("error", e.getMessage());
            jsonArrayBuilder.add(jsonObjectBuilder.build());
            response = jsonArrayBuilder.build();
        }
        endTransaction();
        return response;
    }

    /**
     * FILES
     */

    // get a list of all files
    // returns an empty array if no files available
    @GET
    @Path("files")
    @Produces("application/json")
    public JsonArray getFiles() {

        createTransaction();
        List<Image> images = em.createNamedQuery("Image.findAll").getResultList();
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonImageArrayBuilder = Json.createArrayBuilder();
        Collections.reverse(images);

        for (Image i : images) {
            jsonObjectBuilder.add("path", i.getPath());
            jsonObjectBuilder.add("title", i.getTitle());
            jsonObjectBuilder.add("fileId", i.getIid());
            jsonObjectBuilder.add("type", i.getType());
            jsonObjectBuilder.add("mimeType", i.getMimeType());
            jsonObjectBuilder.add("thumbNails",
                    buildThumbsPathArray(i.getPath(), i.getType()));
            jsonObjectBuilder.add("userId", i.getUidInt());
            jsonImageArrayBuilder.add(jsonObjectBuilder.build());
        }
        endTransaction();
        return jsonImageArrayBuilder.build();
    }

    // get list of latest files
    // returns an empty array if no files available
    // quick'n'dirty for small scale testing
    @GET
    @Path("files/latest/{number}")
    @Produces("application/json")
    public JsonArray getNewestFiles(@PathParam("number") int number) {

        createTransaction();
        List<Image> images = em.createNamedQuery("Image.findAll").getResultList();
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonImageArrayBuilder = Json.createArrayBuilder();
        Collections.reverse(images);
        
        if (number < images.size()) {
            images = images.subList(0, number);
        }

        for (Image i : images) {
            jsonObjectBuilder.add("path", i.getPath());
            jsonObjectBuilder.add("title", i.getTitle());
            jsonObjectBuilder.add("fileId", i.getIid());
            jsonObjectBuilder.add("type", i.getType());
            jsonObjectBuilder.add("mimeType", i.getMimeType());
            jsonObjectBuilder.add("thumbNails",
                    buildThumbsPathArray(i.getPath(), i.getType()));
            jsonObjectBuilder.add("userId", i.getUidInt());
            jsonImageArrayBuilder.add(jsonObjectBuilder.build());
        }
        endTransaction();
        return jsonImageArrayBuilder.build();
    }

    // get a list of files of defined type
    @GET
    @Path("files/type/{type}")
    @Produces("application/json")
    public JsonArray getFilesByType(@PathParam("type") String type) {
        createTransaction();
        List<Image> images = em.createNamedQuery("Image.findByType")
                .setParameter("type", type).getResultList();
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        Collections.reverse(images);

        for (Image i : images) {
            jsonObjectBuilder.add("path", i.getPath());
            jsonObjectBuilder.add("title", i.getTitle());
            jsonObjectBuilder.add("fileId", i.getIid());
            jsonObjectBuilder.add("userId", i.getUidInt());
            jsonObjectBuilder.add("mimeType", i.getMimeType());
            jsonObjectBuilder.add("thumbNails",
                    buildThumbsPathArray(i.getPath(), type));
            jsonArrayBuilder.add(jsonObjectBuilder.build());
        }
        endTransaction();
        return jsonArrayBuilder.build();
    }

    // Get a file by file id
    @GET
    @Path("file/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getFileByIID(@PathParam("id") String iid) {
        this.searchIID = Integer.parseInt(iid);
        Response response;
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        createTransaction();
        try {
            this.queryImage = (Image) em.createNamedQuery("Image.findByIid").setParameter("iid", this.searchIID).getSingleResult();
            jsonObjectBuilder.add("path", this.queryImage.getPath());
            jsonObjectBuilder.add("title", this.queryImage.getTitle());
            jsonObjectBuilder.add("description", this.queryImage.getDescription());
            jsonObjectBuilder.add("uploadTime", this.queryImage.getUploadtime().toString());
            jsonObjectBuilder.add("type", this.queryImage.getType());
            jsonObjectBuilder.add("mimeType", this.queryImage.getMimeType());
            jsonObjectBuilder.add("thumbNails",
                    buildThumbsPathArray(this.queryImage.getPath(), this.queryImage.getType()));
            jsonObjectBuilder.add("userId", this.queryImage.getUidInt());
            // build the JsonObject and the response object
            response = Response
                    .ok(jsonObjectBuilder.build())
                    .build();
        } catch (NoResultException e) {
            jsonObjectBuilder.add("error", "not found");
            response = Response.status(404)
                    .entity(jsonObjectBuilder.build())
                    .build();
        }
        endTransaction();
        return response;
    }

    // Get a list of files uploaded by a user (uid)
    // returns an empty array, if user exists but does not have any uploads
    // returns an error object in array if user not found
    @GET
    @Path("files/user/{user}")
    @Produces("application/json")
    public JsonArray getFilesByUser(@PathParam("user") String uid) {
        JsonArray response;
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        this.userImageArray = new ArrayList();
        this.searchUID = Integer.parseInt(uid);
        createTransaction();
        try {
            this.queryUser = (User) em.createNamedQuery("User.findByUid").setParameter("uid", this.searchUID).getSingleResult();
            this.imageQuery = em.createNamedQuery("Image.findAll").getResultList();
            for (Image img : imageQuery) {
                if (img.getUid() == this.queryUser) {
                    this.userImageArray.add(img);
                }
            }
            Collections.reverse(this.userImageArray);
            // loop through all the images in users fav image collection and add them into the JsonObject
            for (Image i : this.userImageArray) {
                jsonObjectBuilder.add("path", i.getPath());
                jsonObjectBuilder.add("title", i.getTitle());
                jsonObjectBuilder.add("fileId", i.getIid());
                jsonObjectBuilder.add("type", i.getType());
                jsonObjectBuilder.add("mimeType", i.getMimeType());
                jsonObjectBuilder.add("thumbNails",
                        buildThumbsPathArray(i.getPath(), i.getType()));
                jsonArrayBuilder.add(jsonObjectBuilder.build());
            }
            response = jsonArrayBuilder.build();
        } catch (NoResultException e) {
            jsonObjectBuilder.add("error", "user not found");
            jsonObjectBuilder.add("message", e.getMessage());
            jsonArrayBuilder.add(jsonObjectBuilder.build());
            response = jsonArrayBuilder.build();
        }
        endTransaction();
        return response;
    }

    // Search file(s) by title
    // returns an empty array if not found any
    @POST
    @Path("files/search/title")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/json")
    public JsonArray findFilesByName(@FormParam("title") String title) {

        JsonArray response;
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        createTransaction();
        this.imageQuery = em.createNamedQuery("Image.findByTitleWild").setParameter("1", "%" + title + "%").getResultList();
        if (!this.imageQuery.isEmpty()) {
            // loop through all the images in users fav image collection and add them into the JsonObject
            for (Image i : this.imageQuery) {
                jsonObjectBuilder.add("path", i.getPath());
                jsonObjectBuilder.add("title", i.getTitle());
                jsonObjectBuilder.add("fileId", i.getIid());
                jsonObjectBuilder.add("type", i.getType());
                jsonObjectBuilder.add("mimeType", i.getMimeType());
                jsonObjectBuilder.add("thumbNails",
                        buildThumbsPathArray(i.getPath(), i.getType()));
                jsonObjectBuilder.add("userId", i.getUidInt());
                jsonArrayBuilder.add(jsonObjectBuilder.build());
            }
            response = jsonArrayBuilder.build();
        } else {
            response = Json.createArrayBuilder().build();
        }
        endTransaction();
        return response;
    }

    // Search files with description
    // returns an empty array if not found any
    @POST
    @Path("files/search/desc")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/json")
    public JsonArray findFilesByDesc(@FormParam("desc") String desc) {
        createTransaction();
        this.imageQuery = em.createNamedQuery("Image.findByDescriptionWild")
                .setParameter("1", "%" + desc + "%").getResultList();
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonImageArrayBuilder = Json.createArrayBuilder();

        // loop through all the images and add them into the JsonObject
        for (Image i : this.imageQuery) {
            jsonObjectBuilder.add("path", i.getPath());
            jsonObjectBuilder.add("title", i.getTitle());
            jsonObjectBuilder.add("description", i.getDescription());
            jsonObjectBuilder.add("type", i.getType());
            jsonObjectBuilder.add("mimeType", i.getMimeType());
            jsonObjectBuilder.add("thumbNails",
                    buildThumbsPathArray(i.getPath(), i.getType()));
            jsonObjectBuilder.add("fileId", i.getIid());
            jsonObjectBuilder.add("userId", i.getUidInt());
            jsonImageArrayBuilder.add(jsonObjectBuilder.build());
        }
        endTransaction();
        return jsonImageArrayBuilder.build();
    }

    // Get a random file
    @GET
    @Path("file/random")
    @Produces("application/json")
    public JsonObject getRandomImage() {
        createTransaction();
        this.imageList = em.createNamedQuery("Image.findAll").getResultList();
        this.randomno = new Random();
        this.randomIID = this.randomno.nextInt(this.imageList.size());
        Image i = this.imageList.get(randomIID);

        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("path", i.getPath());
        jsonObjectBuilder.add("title", i.getTitle());
        jsonObjectBuilder.add("description", i.getDescription());
        jsonObjectBuilder.add("type", i.getType());
        jsonObjectBuilder.add("mimeType", i.getMimeType());
        jsonObjectBuilder.add("thumbNails",
                buildThumbsPathArray(i.getPath(), i.getType()));
        jsonObjectBuilder.add("fileId", i.getIid());
        jsonObjectBuilder.add("userId", i.getUidInt());
        endTransaction();
        return jsonObjectBuilder.build();
    }

    /**
     * LIKES
     *
     */
    // Like a file
    @GET
    @Path("like/{file}/{user}")
    @Produces("application/json")
    public JsonObject favouriteImage(@PathParam("file") String iid,
            @PathParam("user") String uid) {
        this.searchIID = Integer.parseInt(iid);
        this.searchUID = Integer.parseInt(uid);
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

        createTransaction();
        this.queryUser = (User) em.createNamedQuery("User.findByUid").setParameter("uid", this.searchUID).getSingleResult();
        this.queryImage = (Image) em.createNamedQuery("Image.findByIid").setParameter("iid", this.searchIID).getSingleResult();

        if (this.queryUser.getImageCollection().contains(this.queryImage)) {
            endTransaction();
            jsonObjectBuilder.add("status", "liked already");
            return jsonObjectBuilder.build();
        }

        // set the users fav images into the imageCollection
        // add the current image to that collection
        this.imageCollection = this.queryUser.getImageCollection();
        this.imageCollection.add(this.queryImage);
        // set the images users who've fav it to userCollection
        // add the current user to that collection        
        this.userCollection = this.queryImage.getUserCollection();
        this.userCollection.add(this.queryUser);
        // set the updated fav lists for both user and image
        this.queryImage.setUserCollection(this.userCollection);
        this.queryUser.setImageCollection(this.imageCollection);

        endTransaction();
        jsonObjectBuilder.add("status", "liked now");
        return jsonObjectBuilder.build();
    }

    // Unlike a file
    @GET
    @Path("unlike/{file}/{user}")
    @Produces("application/json")
    public JsonObject unfavouriteImage(@PathParam("file") String iid,
            @PathParam("user") String uid) {
        this.searchIID = Integer.parseInt(iid);
        this.searchUID = Integer.parseInt(uid);
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        createTransaction();
        this.queryImage = (Image) em.createNamedQuery("Image.findByIid").setParameter("iid", this.searchIID).getSingleResult();
        this.queryUser = (User) em.createNamedQuery("User.findByUid").setParameter("uid", this.searchUID).getSingleResult();

        // get the imageCollection from user and remove the image, then set it again
        this.imageCollection = this.queryUser.getImageCollection();
        this.imageCollection.remove(this.queryImage);
        this.queryUser.setImageCollection(this.imageCollection);

        // get the userCollection from the image and remove the user, then set it again
        this.userCollection = this.queryImage.getUserCollection();
        this.userCollection.remove(this.queryUser);
        this.queryImage.setUserCollection(this.userCollection);
        endTransaction();
        jsonObjectBuilder.add("status", "unliked");
        return jsonObjectBuilder.build();
    }

    // Find favourite images of an user
    @GET
    @Path("likes/user/{id}")
    @Produces("application/json")
    public JsonArray getFavouritesByUser(@PathParam("id") String uid) {
        this.searchUID = Integer.parseInt(uid);
        createTransaction();
        try {
            this.queryUser = (User) em.createNamedQuery("User.findByUid").setParameter("uid", this.searchUID).getSingleResult();
        } catch (Exception e) {
            endTransaction();
            return this.emptyJsonArray = Json.createArrayBuilder().add("User not found " + e).build();
        }
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        // loop through all the images in users fav image collection and add them into the JsonObject
        for (Image i : this.queryUser.getImageCollection()) {
            jsonObjectBuilder.add("path", i.getPath());
            jsonObjectBuilder.add("title", i.getTitle());
            jsonObjectBuilder.add("fileId", i.getIid());
            jsonObjectBuilder.add("type", i.getType());
            jsonObjectBuilder.add("mimeType", i.getMimeType());
            jsonObjectBuilder.add("thumbNails",
                   buildThumbsPathArray(i.getPath(), i.getType()));
            jsonObjectBuilder.add("userId", i.getUidInt());
            jsonArrayBuilder.add(jsonObjectBuilder.build());
        }
        endTransaction();
        return jsonArrayBuilder.build();
    }

    
    
    /**
     * FILE COMMENTS
     */

    // Get all comments
    @GET
    @Path("comments")
    @Produces("application/json")
    public JsonArray getComments() {

        createTransaction();
        List<Comment> comments = em.createNamedQuery("Comment.findAll").getResultList();
        JsonObjectBuilder job = Json.createObjectBuilder();
        JsonArrayBuilder jab = Json.createArrayBuilder();
        Collections.reverse(comments);

        for (Comment c : comments) {
            job.add("comment", c.getContents());
            job.add("username", c.getUid().getUname());
            job.add("userId", c.getUid().getUid());
            job.add("fileId", c.getIid().getIid());
            job.add("time", c.getCommenttime().toString());
            jab.add(job.build());
        }
        endTransaction();
        return jab.build();
    }
    
    // Add comment to file
    @POST
    @Path("comment/file/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/json")
    public Response commentImage(
            @FormParam("comment") String comment,
            @PathParam("id") String iid,
            @FormParam("user") String uid) {
        
        JsonObjectBuilder job = Json.createObjectBuilder();
 
        if ( (comment == null) || (comment.length() < 2) ) {
            job.add("error", "comment is missing or too short");
            return Response.status(400).entity(job.build()).build();
        }
        this.searchIID = Integer.parseInt(iid);
        this.searchUID = Integer.parseInt(uid);       
        createTransaction();
        
        try {
            this.queryUser = (User) em.createNamedQuery("User.findByUid")
                    .setParameter("uid", this.searchUID)
                    .getSingleResult();
        } catch (NoResultException e) {
            endTransaction();
            job.add("error", "user not found");
            return Response.status(404).entity(job.build()).build();
        }
        try {
            this.queryImage = (Image) em.createNamedQuery("Image.findByIid")
                    .setParameter("iid", this.searchIID)
                    .getSingleResult();
        } catch (NoResultException e) {
            endTransaction();
            job.add("error", "file not found");
            return Response.status(404).entity(job.build()).build();
        }
        newComment = new Comment(new Date(), comment, this.queryImage, this.queryUser);
        em.persist(newComment);
        endTransaction();
        job.add("status", "comment added");
        return Response.ok(job.build()).build();
    }
    
    // Get file comments
    @GET
    @Path("comments/file/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/json")
    public Response getImageComments(@PathParam("id") String IID) {
        this.searchIID = Integer.parseInt(IID);
        JsonObjectBuilder job = Json.createObjectBuilder();

        createTransaction();
        try {
            this.queryImage = (Image) em.createNamedQuery("Image.findByIid")
                    .setParameter("iid", this.searchIID)
                    .getSingleResult();
        } catch (NoResultException e) {
            job.add("error", "file not found");
            return Response.status(404).entity(job.build()).build();
        } finally {
            endTransaction();
        }

        JsonArrayBuilder jab = Json.createArrayBuilder();
        // loop through all the comments in the img and add them into the JsonObject
        for (Comment c : this.queryImage.getCommentCollection()) {
            job.add("comment", c.getContents());
            job.add("username", c.getUid().getUname());
            job.add("userId", c.getUid().getUid());
            job.add("time", c.getCommenttime().toString());
            jab.add(job.build());
        }
        return Response.ok(jab.build()).build();
    }


    /**
     * Helpers
     */
    public void createTransaction() {
        emf = Persistence.createEntityManagerFactory("ImageRektPU");
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    public void endTransaction() {
        em.getTransaction().commit();
        emf.close();
    }
    
    
    private JsonObject buildThumbsPathArray (String filename, String type) {
 
        JsonObjectBuilder job = Json.createObjectBuilder();
         if (type.equals("video")) {
            job.add("screenCapture" , "thumbs/sc_" + filename + ".png");
            job.add("small" , "thumbs/tn160_" + filename + ".png");
            job.add("medium" , "thumbs/tn320_" + filename + ".png");
            job.add("large" , "thumbs/tn640_" + filename + ".png");
         }
         if (type.equals("image")) {
            job.add("small" , "thumbs/tn160_" + filename);
            job.add("medium" , "thumbs/tn320_" + filename);
            job.add("large" , "thumbs/tn640_" + filename);
         }
         return job.build();
    } 

}
