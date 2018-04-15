package pl.ems.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit4.SpringRunner;
import pl.ems.domain.Employee;
import pl.ems.domain.Position;

import javax.persistence.EntityManager;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static pl.ems.TestCreateUtil.createPositionsWithEmployees;

@RunWith(SpringRunner.class)
@DataJpaTest
public class EmployeeRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void givenEmployee_whenFindByForename_thenReturnEmployee() {

        // given
        Position position = createPositionsWithEmployees(singletonList(Pair.of("Developer", 2)), entityManager).get(0);
        Employee employee = position.getEmployees().get(0);

        // when
        List<Employee> found = employeeRepository.findByForenameSurnameEmail(employee.getForename(), null, null);

        // then
        assertThat(found, hasSize(1));
        assertEquals(employee.getForename(), employee.getForename());
        assertEquals(employee.getSurname(), employee.getSurname());
        assertEquals(employee.getEmail(), employee.getEmail());
        assertEquals(employee.getPosition().getId(), employee.getPosition().getId());
    }

    @Test
    public void givenEmployee_whenFindBySurname_thenReturnEmployee() {

        // given
        Position position = createPositionsWithEmployees(singletonList(Pair.of("Developer", 2)), entityManager).get(0);
        Employee employee = position.getEmployees().get(1);

        // when
        List<Employee> found = employeeRepository.findByForenameSurnameEmail(null, employee.getSurname(), null);

        // then
        assertThat(found, hasSize(1));
        assertEquals(employee.getForename(), employee.getForename());
        assertEquals(employee.getSurname(), employee.getSurname());
        assertEquals(employee.getEmail(), employee.getEmail());
        assertEquals(employee.getPosition().getId(), employee.getPosition().getId());
    }

    @Test
    public void givenEmployee_whenFindByEmail_thenReturnEmployee() {

        // given
        Position position = createPositionsWithEmployees(singletonList(Pair.of("Developer", 2)), entityManager).get(0);
        Employee employee = position.getEmployees().get(0);

        // when
        List<Employee> found = employeeRepository.findByForenameSurnameEmail(null, null, employee.getEmail());

        // then
        assertThat(found, hasSize(1));
        assertEquals(employee.getForename(), employee.getForename());
        assertEquals(employee.getSurname(), employee.getSurname());
        assertEquals(employee.getEmail(), employee.getEmail());
        assertEquals(employee.getPosition().getId(), employee.getPosition().getId());
    }

    @Test
    public void givenEmployee_whenFindByForenameAndSurname_thenReturnEmployee() {

        // given
        Position position = createPositionsWithEmployees(singletonList(Pair.of("Developer", 2)), entityManager).get(0);
        Employee employee = position.getEmployees().get(1);

        // when
        List<Employee> found = employeeRepository.findByForenameSurnameEmail(employee.getForename(), employee.getSurname(), null);

        // then
        assertThat(found, hasSize(1));
        assertEquals(employee.getForename(), employee.getForename());
        assertEquals(employee.getSurname(), employee.getSurname());
        assertEquals(employee.getEmail(), employee.getEmail());
        assertEquals(employee.getPosition().getId(), employee.getPosition().getId());
    }

    @Test
    public void givenEmployee_whenFindByInvalidForename_thenReturnNothing() {

        // given
        String invalidForname = "invalidForename";

        // when
        List<Employee> found = employeeRepository.findByForenameSurnameEmail(invalidForname, null, null);

        // then
        assertThat(found, hasSize(0));
    }
}