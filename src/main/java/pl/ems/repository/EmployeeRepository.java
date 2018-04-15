package pl.ems.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.ems.domain.Employee;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.position p WHERE (:forename IS NULL OR e.forename = :forename) AND (:surname IS NULL OR e.surname = :surname) AND (:email IS NULL OR e.email = :email)")
    List<Employee> findByForenameSurnameEmail(@Param("forename") String forename, @Param("surname") String surname, @Param("email") String email);

    boolean existsByUuid(UUID uuid);

    Optional<Employee> findByUuid(UUID uuid);

    @Transactional
    void deleteByUuid(UUID uuid);
}
