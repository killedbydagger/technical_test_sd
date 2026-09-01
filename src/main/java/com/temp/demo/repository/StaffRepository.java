package com.temp.demo.repository;

import com.temp.demo.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    Optional<Staff> findByUsername(String username);

    @Query(nativeQuery = true, value = "SELECT a.name " +
            "FROM staff_authority AS sa LEFT JOIN `authority` AS a ON sa.authority_id = a.id " +
            "WHERE sa.staff_id =:id ")
    Set<String> getAuthorities(@Param("id") int staffId);
}
