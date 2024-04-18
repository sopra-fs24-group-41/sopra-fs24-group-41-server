package ch.uzh.ifi.hase.soprafs24.entity;

import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "WORD")
public class Word implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column()
    private String name;

    @OneToMany(mappedBy = "result")
    private List<Combination> combinations;

    public Word() {
    }

    public Word(String name) {
        name = name.replaceAll("[^A-Za-z0-9]","");
        name = name.toLowerCase();
        this.name = name;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Word that = (Word) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name.replaceAll("[^A-Za-z0-9]","");
        name = name.toLowerCase();
        this.name = name;
    }

    public List<Combination> getCombinations() {
        return combinations;
    }
}
