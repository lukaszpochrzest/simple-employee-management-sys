package pl.ems;

import pl.ems.domain.Position;
import pl.ems.dto.PositionDto;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAssertUtil {

    public static void assertExactlyMatch(List<Position> createdPositions, List<PositionDto> foundPositions) {
        assertEquals(createdPositions.size(), foundPositions.size());

        boolean allMatch = createdPositions.stream()
                .allMatch(createdPosition -> existsMatching(createdPosition, foundPositions));

        assertTrue("Some of the found positions do not match created positions or are missing", allMatch);
    }

    private static boolean existsMatching(Position createdPosition, List<PositionDto> foundPositions) {
        return foundPositions.stream()
                .anyMatch(foundPosition -> matches(createdPosition, foundPosition));
    }

    private static boolean matches(Position position, PositionDto positionDto) {
        return positionDto.getName().equals(position.getName()) && positionDto.getEmployeeCount().equals(Integer.toUnsignedLong(position.getEmployees().size()));
    }
}
