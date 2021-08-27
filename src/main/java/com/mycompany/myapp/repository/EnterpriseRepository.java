package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Enterprise;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Enterprise entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {}
