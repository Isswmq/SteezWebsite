package org.website.steez.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.website.steez.model.user.ShoppingSession;
import org.website.steez.model.user.User;

import java.util.Optional;

@Repository
public interface ShoppingSessionRepository extends JpaRepository<ShoppingSession, Long> {

    Optional<ShoppingSession> findByUser(User user);
}
