package imageRektDB;

import imageRektDB.Comment;
import imageRektDB.Tag;
import imageRektDB.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-02-12T15:35:15")
@StaticMetamodel(Image.class)
public class Image_ { 

    public static volatile SingularAttribute<Image, String> path;
    public static volatile SingularAttribute<Image, User> uid;
    public static volatile CollectionAttribute<Image, Tag> tagCollection;
    public static volatile SingularAttribute<Image, Integer> iid;
    public static volatile CollectionAttribute<Image, User> userCollection;
    public static volatile CollectionAttribute<Image, Comment> commentCollection;
    public static volatile CollectionAttribute<Image, User> userCollection1;
    public static volatile SingularAttribute<Image, String> description;
    public static volatile SingularAttribute<Image, String> mimeType;
    public static volatile SingularAttribute<Image, String> title;
    public static volatile SingularAttribute<Image, String> type;
    public static volatile SingularAttribute<Image, Date> uploadtime;

}