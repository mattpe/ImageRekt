package imageRektDB;

import imageRektDB.Image;
import imageRektDB.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-02-12T15:35:15")
@StaticMetamodel(Comment.class)
public class Comment_ { 

    public static volatile SingularAttribute<Comment, User> uid;
    public static volatile SingularAttribute<Comment, Date> commenttime;
    public static volatile SingularAttribute<Comment, String> contents;
    public static volatile SingularAttribute<Comment, Image> iid;
    public static volatile SingularAttribute<Comment, Integer> cid;

}