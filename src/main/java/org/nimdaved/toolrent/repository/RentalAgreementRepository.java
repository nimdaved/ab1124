package org.nimdaved.toolrent.repository;

import org.nimdaved.toolrent.domain.RentalAgreement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RentalAgreement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RentalAgreementRepository extends JpaRepository<RentalAgreement, Long> {}
