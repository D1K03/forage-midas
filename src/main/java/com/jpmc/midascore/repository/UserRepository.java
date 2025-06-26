package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository; // <-- Change this import

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserRecord, Long> { // <-- Change CrudRepository to JpaRepository

    Optional<UserRecord> findByName(String name);
}