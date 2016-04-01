package imageRektDB;

import imageRektDB.Image;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-02-12T15:35:15")
@StaticMetamodel(Tag.class)
public class Tag_ { 

    public static volatile CollectionAttribute<Tag, Image> imageCollection;
    public static volatile SingularAttribute<Tag, String> tcontent;
    public static volatile SingularAttribute<Tag, Integer> tid;

}