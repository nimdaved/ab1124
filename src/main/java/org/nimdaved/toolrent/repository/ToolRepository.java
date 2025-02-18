package org.nimdaved.toolrent.repository;

import java.util.Optional;
import org.nimdaved.toolrent.domain.Tool;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Tool entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ToolRepository extends JpaRepository<Tool, String> {
    @Query(
        "SELECT t FROM Tool t " +
        "JOIN ToolInventory ti ON t.toolInventory.id = ti.id " +
        "WHERE t.code = :toolCode AND (ti.stockCount - ti.checkedOutCount - ti.onHoldCount) > 0"
    )
    Optional<Tool> findByToolCodeWithStockCountInInventory(@Param("toolCode") String toolCode);
}
