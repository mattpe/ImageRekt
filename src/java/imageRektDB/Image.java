/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageRektDB;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Asus
 * 
 * Image = any media file (image, audio, video)
 */
@Entity
@Table(name = "IMAGE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Image.findAll", query = "SELECT i FROM Image i"),
    @NamedQuery(name = "Image.findByType", query = "SELECT i FROM Image i WHERE i.type = :type"),
    @NamedQuery(name = "Image.findByIid", query = "SELECT i FROM Image i WHERE i.iid = :iid"),
    @NamedQuery(name = "Image.findByTitle", query = "SELECT i FROM Image i WHERE i.title = :title"),
    @NamedQuery(name = "Image.findByDescription", query = "SELECT i FROM Image i WHERE i.description = :description"),
    @NamedQuery(name = "Image.findByUploadtime", query = "SELECT i FROM Image i WHERE i.uploadtime = :uploadtime"),
    @NamedQuery(name = "Image.findByPath", query = "SELECT i FROM Image i WHERE i.path = :path"),
    @NamedQuery(name = "Image.findByDescriptionWild", query = "SELECT i FROM Image i WHERE i.description LIKE ?1 ORDER BY i.iid ASC"),
    @NamedQuery(name = "Image.findByTitleWild", query="SELECT i FROM Image i WHERE i.title LIKE ?1 ORDER BY i.iid ASC")
})


public class Image implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IID")
    private Integer iid;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "TITLE")
    private String title;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 140)
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Column(name = "UPLOADTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadtime;
    
    @Column(name = "TYPE")
    private String type;

    @Column(name = "MIMETYPE")
    private String mimeType;

    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "PATH")
    private String path;
    
    @ManyToMany(mappedBy = "imageCollection")
    private Collection<User> userCollection;
    
    @ManyToMany(mappedBy = "imageCollection")
    private Collection<Tag> tagCollection;
    
    @OneToMany(mappedBy = "iid")
    private Collection<Comment> commentCollection;
    
    @JoinColumn(name = "UID", referencedColumnName = "UID")
    @ManyToOne
    private User uid;
    
    @OneToMany(mappedBy = "iid")
    private Collection<User> userCollection1;

    public Image() {
    }

    public Image(Integer iid) {
        this.iid = iid;
    }

    public Image(Integer iid, String title, String description, String path) {
        this.iid = iid;
        this.title = title;
        this.description = description;
        this.path = path;
    }

    public Image(String title, String description, Date uploadtime, String path, User uid) {
        this.title = title;
        this.description = description;
        this.uploadtime = uploadtime;
        this.path = path;
        this.uid = uid;
    }

    public Image(String title, String description, Date uploadtime, String path, User uid, String type) {
        this.title = title;
        this.description = description;
        this.uploadtime = uploadtime;
        this.path = path;
        this.uid = uid;
        this.type = type;
    }
    
    public Image(String title, String description, Date uploadtime, String path, User uid, String type, String mimeType) {
        this.title = title;
        this.description = description;
        this.uploadtime = uploadtime;
        this.path = path;
        this.uid = uid;
        this.type = type;
        this.mimeType = mimeType;
    }

    
    public Integer getIid() {
        return iid;
    }

    public void setIid(Integer iid) {
        this.iid = iid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType () {
        if (type == null)
            return "";
        return type;
    }

    public void setType (String type) {
        this.type = type;
    }

    public String getMimeType () {
        if (mimeType == null)
            return "";
        return mimeType;
    }

    public void setMimeType (String mimeType) {
        this.mimeType = mimeType;
    }

    
    public Date getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(Date uploadtime) {
        this.uploadtime = uploadtime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @XmlTransient
    public Collection<User> getUserCollection() {
        return userCollection;
    }

    public void setUserCollection(Collection<User> userCollection) {
        this.userCollection = userCollection;
    }

    @XmlTransient
    public Collection<Tag> getTagCollection() {
        return tagCollection;
    }

    public void setTagCollection(Collection<Tag> tagCollection) {
        this.tagCollection = tagCollection;
    }

    @XmlTransient
    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    public User getUid() {
        return uid;
    }
    
    // return 0 if null
    public int getUidInt() {
        if (uid == null)
            return 0;
        return uid.getUid();
    }

    public void setUid(User uid) {
        this.uid = uid;
    }

    @XmlTransient
    public Collection<User> getUserCollection1() {
        return userCollection1;
    }

    public void setUserCollection1(Collection<User> userCollection1) {
        this.userCollection1 = userCollection1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iid != null ? iid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Image)) {
            return false;
        }
        Image other = (Image) object;
        if ((this.iid == null && other.iid != null) || (this.iid != null && !this.iid.equals(other.iid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "imageRektDB.Image[ iid=" + iid + " ]";
    }
    
}
