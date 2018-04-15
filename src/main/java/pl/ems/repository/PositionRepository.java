package pl.ems.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.ems.domain.Position;
import pl.ems.dto.PositionDto;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    @Query("SELECT new pl.ems.dto.PositionDto(p.uuid, p.name, COUNT(e)) FROM Position p LEFT JOIN p.employees e GROUP BY p.id")
    List<PositionDto> findAllWithEmployeeCount();

    @Query("SELECT new pl.ems.dto.PositionDto(p.uuid, p.name, COUNT(e)) FROM Position p LEFT JOIN p.employees e WHERE p.uuid = :uuid GROUP BY p.id")
    Optional<PositionDto> findByUuidWithEmployeeCount(@Param("uuid") UUID uuid);

    Long countByName(String name);

    Optional<Position> findByName(String name);

    boolean existsByUuid(UUID uuid);

    @Transactional
    void deleteByUuid(UUID uuid);
}