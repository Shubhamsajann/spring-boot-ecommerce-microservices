
package com.app.ecom.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.ecom.model.Order;
import com.app.ecom.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find all orders belonging to a particular user
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Optional<Order> findByIdAndUser(Long id, User user);

}
