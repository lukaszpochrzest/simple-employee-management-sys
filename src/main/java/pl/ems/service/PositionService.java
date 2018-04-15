package pl.ems.service;

import pl.ems.dto.PositionDto;
import pl.ems.dto.SavePositionDto;

import java.util.List;
import java.util.UUID;

public interface PositionService {

    UUID savePosition(SavePositionDto savePositionDto);

    List<PositionDto> getPositions();

    PositionDto getPosition(UUID uuid);
}
