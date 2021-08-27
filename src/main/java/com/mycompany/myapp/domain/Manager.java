package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.Gender;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Manager.
 */
@Entity
@Table(name = "manager")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Manager implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @NotNull
    @Size(max = 20)
    @Column(name = "telephone", length = 20, nullable = false)
    private String telephone;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "manager")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "manager", "advisor" }, allowSetters = true)
    private Set<Appointment> appointments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Manager id(Long id) {
        this.id = id;
        return this;
    }

    public Gender getGender() {
        return this.gender;
    }

    public Manager gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public Manager telephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public User getUser() {
        return this.user;
    }

    public Manager user(User user) {
        this.setUser(user);
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Appointment> getAppointments() {
        return this.appointments;
    }

    public Manager appointments(Set<Appointment> appointments) {
        this.setAppointments(appointments);
        return this;
    }

    public Manager addAppointments(Appointment appointment) {
        this.appointments.add(appointment);
        appointment.setManager(this);
        return this;
    }

    public Manager removeAppointments(Appointment appointment) {
        this.appointments.remove(appointment);
        appointment.setManager(null);
        return this;
    }

    public void setAppointments(Set<Appointment> appointments) {
        if (this.appointments != null) {
            this.appointments.forEach(i -> i.setManager(null));
        }
        if (appointments != null) {
            appointments.forEach(i -> i.setManager(this));
        }
        this.appointments = appointments;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Manager)) {
            return false;
        }
        return id != null && id.equals(((Manager) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Manager{" +
            "id=" + getId() +
            ", gender='" + getGender() + "'" +
            ", telephone='" + getTelephone() + "'" +
            "}";
    }
}
