package com.mycompany.myapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Agency.
 */
@Entity
@Table(name = "agency")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Agency implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "contact")
    private String contact;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "email", nullable = false)
    private String email;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Agency id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Agency name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public Agency address(String address) {
        this.address = address;
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return this.contact;
    }

    public Agency contact(String contact) {
        this.contact = contact;
        return this;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return this.email;
    }

    public Agency email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Agency)) {
            return false;
        }
        return id != null && id.equals(((Agency) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Agency{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", address='" + getAddress() + "'" +
            ", contact='" + getContact() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
