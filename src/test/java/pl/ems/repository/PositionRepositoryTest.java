package pl.ems.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit4.SpringRunner;
import pl.ems.domain.Employee;
import pl.ems.domain.Position;
import pl.ems.dto.PositionDto;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pl.ems.TestAssertUtil.assertExactlyMatch;
import static pl.ems.TestCreateUtil.createPositionsWithEmployees;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PositionRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PositionRepository positionRepository;

    @Test
    public void givenPositions_whenFindAllWithEmployeeCount_thenReturn() {
        // given
        List<Position> createdPositions = createPositionsWithEmployees(asList(Pair.of("Developer", 2), Pair.of("Tester", 3)), entityManager);

        // when
        List<PositionDto> foundPositions = positionRepository.findAllWithEmployeeCount();

        //then
        assertExactlyMatch(createdPositions, foundPositions);
    }

    @Test
    public void givenPosition_whenFindWithEmployeeCount_thenReturn() {
        // given
        Position position = createPositionsWithEmployees(singletonList(Pair.of("Developer", 2)), entityManager).get(0);
        List<Employee> employees = position.getEmployees();

        // when
        Optional<PositionDto> found = positionRepository.findByUuidWithEmployeeCount(position.getUuid());

        //then
        assertTrue(found.isPresent());
        assertEquals(found.get().getName(), position.getName());
        assertTrue("There should be " + employees.size() + "employees with " + position.getName() + " position", found.get().getEmployeeCount() == position.getEmployees().size());
    }
}