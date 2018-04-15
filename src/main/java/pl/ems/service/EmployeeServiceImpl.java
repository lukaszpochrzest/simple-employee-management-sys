package pl.ems.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.ems.domain.Employee;
import pl.ems.domain.Position;
import pl.ems.dto.EmployeeDto;
import pl.ems.dto.SaveEmployeeDto;
import pl.ems.exception.EmployeeNotFoundException;
import pl.ems.repository.EmployeeRepository;
import pl.ems.repository.PositionRepository;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    private PositionRepository positionRepository;

    @Override
    public UUID saveEmployee(SaveEmployeeDto saveEmployeeDto) {
        final Position position = positionRepository.findByName(saveEmployeeDto.getPosition())
                .orElseGet(() -> createNewPosition(saveEmployeeDto.getPosition()));

        final Employee employee = map(saveEmployeeDto, position);
        return employeeRepository.save(employee).getUuid();
    }

    @Override
    public List<EmployeeDto> getEmployees(String forename, String surname, String email) {
        List<Employee> employees = employeeRepository.findByForenameSurnameEmail(forename, surname, email);

        return employees.stream()
                .map(this::map)
                .collect(toList());
    }

    @Override
    public EmployeeDto getEmployee(UUID uuid) {
        return employeeRepository.findByUuid(uuid)
                .map(this::map)
                .orElseThrow(EmployeeNotFoundException::new);
    }

    @Override
    public void removeEmployee(UUID uuid) {
        if (!employeeRepository.existsByUuid(uuid)) {
           throw new EmployeeNotFoundException();
        }

        employeeRepository.deleteByUuid(uuid);
    }

    private Position createNewPosition(String name) {
        Position position = new Position();
        position.setName(name);
        return positionRepository.save(position);
    }

    private Employee map(SaveEmployeeDto saveEmployeeDto, Position position) {
        Employee employee = new Employee();
        employee.setEmail(saveEmployeeDto.getEmail());
        employee.setForename(saveEmployeeDto.getForename());
        employee.setSurname(saveEmployeeDto.getSurname());
        employee.setPosition(position);
        return employee;
    }

    private EmployeeDto map(Employee employee) {
        return new EmployeeDto(
                employee.getUuid().toString(),
                employee.getForename(),
                employee.getSurname(),
                employee.getEmail(),
                employee.getPosition().getName()
        );
    }
}
