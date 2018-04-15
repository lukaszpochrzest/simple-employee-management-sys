package pl.ems.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.ems.domain.Position;
import pl.ems.dto.PositionDto;
import pl.ems.dto.SavePositionDto;
import pl.ems.exception.PositionNotFoundException;
import pl.ems.exception.PositionWithNameAlreadyExistsException;
import pl.ems.repository.PositionRepository;

import java.util.Optional;
import java.util.UUID;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PositionServiceImplTest {

    private PositionService positionService;

    @Mock
    private PositionRepository positionRepository;

    @Before
    public void setUp() {
        positionService = new PositionServiceImpl(positionRepository);
        reset(positionRepository);
    }

    @Test
    public void givenSavePositionDto_whenSavePosition_thenReturn() {
        // given
        SavePositionDto savePositionDto = random(SavePositionDto.class);
        Position position = new Position();
        position.setId(1L);
        position.setName(savePositionDto.getName());

        when(positionRepository.countByName(savePositionDto.getName())).thenReturn(0L);
        when(positionRepository.save(any())).thenReturn(position);

        // when
        positionService.savePosition(savePositionDto);

        // then
        ArgumentCaptor<Position> positionCaptor = ArgumentCaptor.forClass(Position.class);

        verify(positionRepository, times(1)).save(positionCaptor.capture());
        assertEquals(savePositionDto.getName(), positionCaptor.getValue().getName());
        assertNull(positionCaptor.getValue().getEmployees());
    }

    @Test(expected = PositionWithNameAlreadyExistsException.class)
    public void givenSavePositionDtoWithAlreadyExistingPositonName_whenSavePosition_thenThrow() {
        // given
        SavePositionDto savePositionDto = random(SavePositionDto.class);

        when(positionRepository.countByName(savePositionDto.getName())).thenReturn(1L);

        // when
        positionService.savePosition(savePositionDto);
    }

    @Test
    public void givenPosition_whenGetPosition_thenReturn() {
        // given
        Position position = random(Position.class);
        PositionDto expectedResult = new PositionDto(position.getUuid().toString(), position.getName(), Integer.toUnsignedLong(position.getEmployees().size()));

        when(positionRepository.findByUuidWithEmployeeCount(position.getUuid())).thenReturn(Optional.of(expectedResult));

        // when
        PositionDto result = positionService.getPosition(position.getUuid());

        // then
        assertEquals(expectedResult, result);
    }

    @Test(expected = PositionNotFoundException.class)
    public void whenGetPositionUsingInvalidId_thenThrow() {
        // given
        UUID invalidId = randomUUID();

        // when
        positionService.getPosition(invalidId);
    }
}