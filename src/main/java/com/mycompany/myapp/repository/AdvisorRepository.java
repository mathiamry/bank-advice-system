package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Advisor;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Advisor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdvisorRepository extends JpaRepository<Advisor, Long> {}
