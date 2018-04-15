package pl.ems.service;

import pl.ems.dto.EmployeeDto;
import pl.ems.dto.SaveEmployeeDto;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    UUID saveEmployee(SaveEmployeeDto saveEmployeeDto);

    List<EmployeeDto> getEmployees(String forename, String surname, String email);

    EmployeeDto getEmployee(UUID id);

    void removeEmployee(UUID id);

}
