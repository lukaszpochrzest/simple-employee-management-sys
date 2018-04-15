package pl.ems.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pl.ems.dto.EmployeeDto;
import pl.ems.dto.SaveEmployeeDto;
import pl.ems.service.EmployeeService;
import pl.ems.util.Util;

import java.util.List;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EmployeeController {

    private EmployeeService employeeService;

    @RequestMapping(value = "/employee", method = POST)
    public ResponseEntity saveEmployee(UriComponentsBuilder builder,
                                       @RequestBody SaveEmployeeDto saveEmployeeDto) {
        UUID uuid = employeeService.saveEmployee(saveEmployeeDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/employee/{id}").buildAndExpand(uuid.toString()).toUri());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/employee", method = GET)
    @ResponseBody
    public List<EmployeeDto> getEmployees(@RequestParam(value = "forename", required = false) String forename,
                                          @RequestParam(value = "surname", required = false) String surname,
                                          @RequestParam(value = "email", required = false) String email) {
        return employeeService.getEmployees(forename, surname, email);
    }

    @RequestMapping(value = "/employee/{id}", method = GET)
    @ResponseBody
    public EmployeeDto getEmployee(@PathVariable("id") String uuid) {
        return employeeService.getEmployee(Util.parseUUID(uuid));
    }

    @RequestMapping(value = "/employee/{id}", method = DELETE)
    public void removeEmployee(@PathVariable("id") String uuid) {
        employeeService.removeEmployee(Util.parseUUID(uuid));
    }

}
