package org.nimdaved.toolrent.repository;

import org.nimdaved.toolrent.domain.ToolInventory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ToolInventory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ToolInventoryRepository extends JpaRepository<ToolInventory, Long> {}
