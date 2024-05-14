package ch.uzh.ifi.hase.soprafs24.entity;

import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "ACHIEVEMENT")
public class Achievement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column()
    private String name;

    @Column
    private String description;

    @Column
    private String profilePicture;

    public Achievement() {
    }

    public Achievement(String name) {
    }

    public Achievement(String name, String description, String profilePicture) {
        this.name = name;
        this.description = description;
        this.profilePicture =   profilePicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
