package pl.ems;

import org.springframework.data.util.Pair;
import pl.ems.domain.Employee;
import pl.ems.domain.Position;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

public class TestCreateUtil {

    public static List<Position> createPositionsWithEmployees(List<Pair<String, Integer>> positions, EntityManager entityManager) {
        return positions.stream()
                .map(position -> createPositionWithEmployeeCount(position.getFirst(), position.getSecond(), entityManager))
                .collect(Collectors.toList());

    }

    private static Position createPositionWithEmployeeCount(String positionName, Integer employeeCount, EntityManager entityManager) {
        if (employeeCount < 0) {
            throw new IllegalArgumentException();
        }

        Position position = new Position();
        position.setName(positionName);

        List<Employee> employees = new ArrayList<>(employeeCount);

        IntStream.range(0, employeeCount)
                .forEach(i -> employees.add(createTestEmployee(position)));

        position.setEmployees(employees);

        entityManager.persist(position);
        employees.forEach(entityManager::persist);
        return position;
    }

    private static Employee createTestEmployee(Position position) {
        Employee someEmployee = random(Employee.class, "id", "position");
        someEmployee.setPosition(position);
        return someEmployee;
    }

}
