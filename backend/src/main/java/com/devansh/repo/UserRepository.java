package com.devansh.repo;

import com.devansh.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.fullname LIKE CONCAT('%', :query, '%') OR u.email LIKE CONCAT('%', :query, '%')")
    public List<User> searchUser(@Param("query") String query);


}
