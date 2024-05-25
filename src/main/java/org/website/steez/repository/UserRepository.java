package org.website.steez.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.website.steez.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findAll(Pageable pageable);

    Optional<User> findById(Long id);

    @Modifying
    @Query("UPDATE User u SET u.isAccountNonLocked = :isAccountNonLocked WHERE u.id = :id")
    void blockUserById(@Param("id") Long id, @Param("isAccountNonLocked") boolean isAccountNonLocked);
}
