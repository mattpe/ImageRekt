package imageRektDB;

import imageRektDB.Comment;
import imageRektDB.Image;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-02-12T15:35:15")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, Integer> uid;
    public static volatile SingularAttribute<User, String> upass;
    public static volatile SingularAttribute<User, String> uname;
    public static volatile SingularAttribute<User, Image> iid;
    public static volatile CollectionAttribute<User, Image> imageCollection;
    public static volatile CollectionAttribute<User, Comment> commentCollection;
    public static volatile CollectionAttribute<User, Image> imageCollection1;
    public static volatile SingularAttribute<User, String> uemail;

}