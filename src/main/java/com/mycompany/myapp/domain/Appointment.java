package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.Status;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Appointment.
 */
@Entity
@Table(name = "appointment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "created", nullable = false)
    private Instant created;

    @NotNull
    @Column(name = "appointement_date", nullable = false)
    private LocalDate appointementDate;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Size(max = 15)
    @Column(name = "title", length = 15)
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @NotNull
    @Column(name = "status_change_date", nullable = false)
    private Instant statusChangeDate;

    @Column(name = "commentary")
    private String commentary;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user", "appointments" }, allowSetters = true)
    private Manager manager;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user", "appointments", "agency" }, allowSetters = true)
    private Advisor advisor;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Appointment id(Long id) {
        this.id = id;
        return this;
    }

    public Instant getCreated() {
        return this.created;
    }

    public Appointment created(Instant created) {
        this.created = created;
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public LocalDate getAppointementDate() {
        return this.appointementDate;
    }

    public Appointment appointementDate(LocalDate appointementDate) {
        this.appointementDate = appointementDate;
        return this;
    }

    public void setAppointementDate(LocalDate appointementDate) {
        this.appointementDate = appointementDate;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public Appointment startDate(Instant startDate) {
        this.startDate = startDate;
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public Appointment endDate(Instant endDate) {
        this.endDate = endDate;
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return this.title;
    }

    public Appointment title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Appointment description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return this.status;
    }

    public Appointment status(Status status) {
        this.status = status;
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getStatusChangeDate() {
        return this.statusChangeDate;
    }

    public Appointment statusChangeDate(Instant statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
        return this;
    }

    public void setStatusChangeDate(Instant statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }

    public String getCommentary() {
        return this.commentary;
    }

    public Appointment commentary(String commentary) {
        this.commentary = commentary;
        return this;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public Manager getManager() {
        return this.manager;
    }

    public Appointment manager(Manager manager) {
        this.setManager(manager);
        return this;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public Advisor getAdvisor() {
        return this.advisor;
    }

    public Appointment advisor(Advisor advisor) {
        this.setAdvisor(advisor);
        return this;
    }

    public void setAdvisor(Advisor advisor) {
        this.advisor = advisor;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Appointment)) {
            return false;
        }
        return id != null && id.equals(((Appointment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Appointment{" +
            "id=" + getId() +
            ", created='" + getCreated() + "'" +
            ", appointementDate='" + getAppointementDate() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", statusChangeDate='" + getStatusChangeDate() + "'" +
            ", commentary='" + getCommentary() + "'" +
            "}";
    }
}
