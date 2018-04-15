package pl.ems.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.ems.domain.Position;
import pl.ems.dto.PositionDto;
import pl.ems.dto.SavePositionDto;
import pl.ems.exception.PositionNotFoundException;
import pl.ems.exception.PositionWithNameAlreadyExistsException;
import pl.ems.repository.PositionRepository;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PositionServiceImpl implements PositionService {

    private PositionRepository positionRepository;

    @Override
    public UUID savePosition(SavePositionDto savePositionDto) {
        if(positionWithNameAlreadyExists(savePositionDto.getName())) {
            throw new PositionWithNameAlreadyExistsException(savePositionDto.getName());
        }

        Position position = map(savePositionDto);
        return positionRepository.save(position).getUuid();
    }

    @Override
    public List<PositionDto> getPositions() {
        return positionRepository.findAllWithEmployeeCount();
    }

    @Override
    public PositionDto getPosition(UUID uuid) {
        return positionRepository.findByUuidWithEmployeeCount(uuid)
                .orElseThrow(PositionNotFoundException::new);
    }

    private Position map(SavePositionDto savePositionDto) {
        Position position = new Position();
        position.setName(savePositionDto.getName());
        return position;
    }

    private boolean positionWithNameAlreadyExists(String name) {
        return positionRepository.countByName(name) > 0L;
    }
}