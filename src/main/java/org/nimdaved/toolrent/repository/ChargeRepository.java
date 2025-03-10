package org.nimdaved.toolrent.repository;

import java.util.Optional;
import org.nimdaved.toolrent.domain.Charge;
import org.nimdaved.toolrent.domain.enumeration.ToolType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Charge entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChargeRepository extends JpaRepository<Charge, Long> {
    Optional<Charge> findOneByToolType(ToolType toolType);
}
