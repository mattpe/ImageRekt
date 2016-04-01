package imageRektServlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Manipulate media files using bash, imagemagick & ffmpeg
 * 
 * @author mattpe
 */
public final class BashMediaManipulator {

    private static final String mediaPath = "/var/www/uploads/";
    
    /**
     * Generate thumbnails.
     * @param filename
     * @param type "image" or "video"
     * @return
     */
    public static String createThumbs(String filename, String type) {
        
        ArrayList<String> commands = new ArrayList<String>();
        String path = mediaPath;
        String cmdOutput = "";
     
        ArrayList<String> imageCommands = new ArrayList<String>();
        imageCommands.add("convert -thumbnail 640 " +path+filename+ " "+path+"thumbs/tn640_" + filename);
        imageCommands.add("convert -thumbnail 320 "+path+filename+" "+path+"thumbs/tn320_" + filename);
        imageCommands.add("convert -thumbnail 160 "+path+filename+" "+path+"thumbs/tn160_" + filename);
        
        ArrayList<String> videoCommands = new ArrayList<String>();
        videoCommands.add("ffmpeg -i "+path+filename+" -ss 00:00:01.000 -vframes 1 "+path+"thumbs/sc_"+filename+".png");
        videoCommands.add("convert -thumbnail 640 "+path+"thumbs/sc_"+filename+".png "+path+"thumbs/tn640_"+filename+".png");
        videoCommands.add("convert -thumbnail 320 "+path+"thumbs/sc_"+filename+".png "+path+"thumbs/tn320_"+filename+".png");
        videoCommands.add("convert -thumbnail 160 "+path+"thumbs/sc_"+filename+".png "+path+"thumbs/tn160_"+filename+".png");
 
        if (type.equals("image")) {
            commands = imageCommands;
        } else if (type.equals("video")) {
            commands = videoCommands;
        } else {
            return "wrong file type";
        }

        for (String command : commands) {
            cmdOutput += executeCommand(command);
        }
        
        return cmdOutput;
    }

    /**
     * Make large images smaller.
     * @param filename
     * @param size max width or height in pixels
     * @return
     */
    public static boolean shrinkImage(String filename, String size) {
        String command = mediaPath + "mogrify -resize "+size+" "+filename;
        try {
            executeCommand(command);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    
    private static String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch (IOException | InterruptedException e) {
            return e.getMessage();
        }
        
        return output.toString();
    }

}
