package pl.ems.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.ems.domain.Employee;
import pl.ems.domain.Position;
import pl.ems.dto.EmployeeDto;
import pl.ems.dto.SaveEmployeeDto;
import pl.ems.exception.EmployeeNotFoundException;
import pl.ems.repository.EmployeeRepository;
import pl.ems.repository.PositionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceImplTest {

    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PositionRepository positionRepository;

    @Before
    public void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository, positionRepository);
        reset(employeeRepository, positionRepository);
    }

    @Test
    public void givenSaveEmployeeDtoWithExistingPosition_whenSaveEmployee_thenReturn() {
        // given
        Position position = random(Position.class);
        SaveEmployeeDto saveEmployeeDto = random(SaveEmployeeDto.class);
        saveEmployeeDto.setPosition(position.getName());

        when(positionRepository.findByName(position.getName())).thenReturn(Optional.of(position));
        when(employeeRepository.save(any())).thenReturn(new Employee());

        // when
        employeeService.saveEmployee(saveEmployeeDto);

        // then
        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);

        verify(employeeRepository, times(1)).save(employeeCaptor.capture());
        assertEquals(employeeCaptor.getValue().getForename(), saveEmployeeDto.getForename());
        assertEquals(employeeCaptor.getValue().getSurname(), saveEmployeeDto.getSurname());
        assertEquals(employeeCaptor.getValue().getEmail(), saveEmployeeDto.getEmail());
        assertEquals(employeeCaptor.getValue().getPosition(), position);
    }

    @Test
    public void givenSaveEmployeeDtoWithNotExistingPosition_whenSaveEmployee_thenReturn() {
        // given
        SaveEmployeeDto saveEmployeeDto = random(SaveEmployeeDto.class);

        when(positionRepository.findByName(saveEmployeeDto.getPosition())).thenReturn(Optional.empty());
        when(employeeRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(positionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // when
        employeeService.saveEmployee(saveEmployeeDto);

        // then
        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);

        verify(employeeRepository, times(1)).save(employeeCaptor.capture());
        assertEquals(employeeCaptor.getValue().getForename(), saveEmployeeDto.getForename());
        assertEquals(employeeCaptor.getValue().getSurname(), saveEmployeeDto.getSurname());
        assertEquals(employeeCaptor.getValue().getEmail(), saveEmployeeDto.getEmail());
        assertEquals(employeeCaptor.getValue().getPosition().getName(), saveEmployeeDto.getPosition());
    }

    @Test
    public void givenEmployees_whenFindByForenameSurnameEmail_thenReturn() {
        // given
        List<Employee> employees = randomListOf(2, Employee.class);

        when(employeeRepository.findByForenameSurnameEmail(any(), any(), any())).thenReturn(employees);

        // when
        List<EmployeeDto> employeeDtos = employeeService.getEmployees(null, null, null);

        // then
        assertThat(employeeDtos, hasSize(employees.size()));
    }

    @Test(expected = EmployeeNotFoundException.class)
    public void whenGetEmployeeUsingInvalidId_thenThrow() {
        // given
        UUID invalidId = randomUUID();

        // when
        employeeService.getEmployee(invalidId);
    }

    @Test
    public void givenEmployee_whenGetEmployee_thenReturn() {
        // given
        Employee employee = random(Employee.class);

        when(employeeRepository.findByUuid(employee.getUuid())).thenReturn(Optional.of(employee));

        // when
        EmployeeDto employeeDto = employeeService.getEmployee(employee.getUuid());

        // then
        assertEquals(employeeDto.getId(), employee.getUuid().toString());
        assertEquals(employeeDto.getForename(), employee.getForename());
        assertEquals(employeeDto.getSurname(), employee.getSurname());
        assertEquals(employeeDto.getEmail(), employee.getEmail());
        assertEquals(employeeDto.getPosition(), employee.getPosition().getName());
    }

    @Test
    public void givenEmployee_whenRemoveEmployee_thenReturn() {
        // given
        Employee employee = random(Employee.class);

        when(employeeRepository.existsByUuid(employee.getUuid())).thenReturn(true);

        // when
        employeeService.removeEmployee(employee.getUuid());

        // then
        verify(employeeRepository, times(1)).deleteByUuid(employee.getUuid());
    }

    @Test(expected = EmployeeNotFoundException.class)
    public void whenRemoveEmployeeUsingInvalidId_thenThrow() {
        // given
        UUID invalidId = randomUUID();

        // when
        employeeService.removeEmployee(invalidId);
    }
}