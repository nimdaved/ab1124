package org.nimdaved.toolrent.repository;

import java.util.Optional;
import org.nimdaved.toolrent.domain.RentalAgreement;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RentalAgreement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RentalAgreementRepository extends JpaRepository<RentalAgreement, Long> {
    @Query("SELECT ra.id FROM RentalAgreement ra WHERE ra.rental.id = :rentalId")
    Optional<Long> findIdByRentalId(@Param("rentalId") Long rentalId);

    Optional<RentalAgreement> findByRentalId(Long rentalId);
}
