
package com.app.ecom.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.ecom.model.CartItem;
import com.app.ecom.model.Product;
import com.app.ecom.model.User;

public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByUserAndProduct(
            User user, Product product);

    List<CartItem> findByUser(User user);

    void deleteByUserAndProduct(
            User user, Product product);
}
