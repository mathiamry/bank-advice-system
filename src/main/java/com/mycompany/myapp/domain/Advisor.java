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
 * A Advisor.
 */
@Entity
@Table(name = "advisor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Advisor implements Serializable {

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

    @OneToMany(mappedBy = "advisor")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "manager", "advisor" }, allowSetters = true)
    private Set<Appointment> appointments = new HashSet<>();

    @ManyToOne
    private Agency agency;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Advisor id(Long id) {
        this.id = id;
        return this;
    }

    public Gender getGender() {
        return this.gender;
    }

    public Advisor gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public Advisor telephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public User getUser() {
        return this.user;
    }

    public Advisor user(User user) {
        this.setUser(user);
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Appointment> getAppointments() {
        return this.appointments;
    }

    public Advisor appointments(Set<Appointment> appointments) {
        this.setAppointments(appointments);
        return this;
    }

    public Advisor addAppointments(Appointment appointment) {
        this.appointments.add(appointment);
        appointment.setAdvisor(this);
        return this;
    }

    public Advisor removeAppointments(Appointment appointment) {
        this.appointments.remove(appointment);
        appointment.setAdvisor(null);
        return this;
    }

    public void setAppointments(Set<Appointment> appointments) {
        if (this.appointments != null) {
            this.appointments.forEach(i -> i.setAdvisor(null));
        }
        if (appointments != null) {
            appointments.forEach(i -> i.setAdvisor(this));
        }
        this.appointments = appointments;
    }

    public Agency getAgency() {
        return this.agency;
    }

    public Advisor agency(Agency agency) {
        this.setAgency(agency);
        return this;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Advisor)) {
            return false;
        }
        return id != null && id.equals(((Advisor) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Advisor{" +
            "id=" + getId() +
            ", gender='" + getGender() + "'" +
            ", telephone='" + getTelephone() + "'" +
            "}";
    }
}
