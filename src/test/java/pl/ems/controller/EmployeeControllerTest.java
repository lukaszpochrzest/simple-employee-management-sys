package pl.ems.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.ems.domain.Employee;
import pl.ems.domain.Position;
import pl.ems.dto.SaveEmployeeDto;
import pl.ems.repository.EmployeeRepository;
import pl.ems.repository.PositionRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.ems.TestCreateUtil.createPositionsWithEmployees;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EmployeeControllerTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void whenPostEmployee_thenStatusCreated() throws Exception {
        // given
        Position position = createPosition();

        SaveEmployeeDto saveEmployeeDto = random(SaveEmployeeDto.class);
        saveEmployeeDto.setPosition(position.getName());

        // when
        MockHttpServletResponse response = mvc.perform(
                post("/employee")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(saveEmployeeDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse();


        String location = response.getHeader(HttpHeaders.LOCATION);
        assertThat(location, notNullValue());

        UUID createdEmployeeId = extractIdFromEmployeeLocation(location);
        assertTrue("Employee has not been saved in database", employeeRepository.existsByUuid(createdEmployeeId));
    }

    @Test
    public void givenEmployees_whenGetEmployees_thenStatusOk() throws Exception {
        // given
        createPositionsWithEmployees(singletonList(Pair.of("Developer", 2)), entityManager);

        // when
        mvc.perform(
                get("/employee")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void givenEmployee_whenGetEmployee_thenStatusOk() throws Exception {
        // given
        Position position = createPositionsWithEmployees(singletonList(Pair.of("Developer", 2)), entityManager).get(0);
        Employee validEmployee = position.getEmployees().get(0);

        // when
        mvc.perform(
                get("/employee/" + validEmployee.getUuid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.forename", equalTo(validEmployee.getForename())))
                .andExpect(jsonPath("$.surname", equalTo(validEmployee.getSurname())))
                .andExpect(jsonPath("$.email", equalTo(validEmployee.getEmail())))
                .andExpect(jsonPath("$.position", equalTo(validEmployee.getPosition().getName())));
    }


    @Test
    public void whenGetInvalidEmployee_thenStatusNotFound() throws Exception {
        mvc.perform(
                get("/employee/" + randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenDeleteEmployee_thenStatusOk() throws Exception {
        // given
        Position position = createPositionsWithEmployees(singletonList(Pair.of("Developer", 2)), entityManager).get(0);
        Employee validEmployee = position.getEmployees().get(0);

        // when
        mvc.perform(
                delete("/employee/" + validEmployee.getUuid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertFalse("Deleted employee is still in database", employeeRepository.existsByUuid(validEmployee.getUuid()));
    }

    private UUID extractIdFromEmployeeLocation(String location) {
        String[] locationSegments = URI.create(location).getPath().split("/");
        Assert.assertEquals(locationSegments.length, 3);
        return UUID.fromString(locationSegments[locationSegments.length - 1]);
    }

    private Position createPosition() {
        Position position = new Position();
        position.setName("Developer");

        return positionRepository.save(position);
    }
}