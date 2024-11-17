package org.nimdaved.toolrent.repository;

import java.util.function.Supplier;
import org.nimdaved.toolrent.domain.Customer;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Customer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("select c from Customer c where c.email ='anonymous@toolrent.com'")
    Customer findDefaultCustomer();
}
