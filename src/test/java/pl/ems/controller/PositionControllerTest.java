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
import pl.ems.domain.Position;
import pl.ems.dto.PositionDto;
import pl.ems.dto.SavePositionDto;
import pl.ems.repository.PositionRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.ems.TestAssertUtil.assertExactlyMatch;
import static pl.ems.TestCreateUtil.createPositionsWithEmployees;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PositionControllerTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void whenPostPosition_thenStatusCreated() throws Exception {
        // given
        SavePositionDto savePositionDto = new SavePositionDto();
        savePositionDto.setName("Developer");

        // when
        MockHttpServletResponse response = mvc.perform(
                post("/position")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(savePositionDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        //then
        String location = response.getHeader(HttpHeaders.LOCATION);
        assertThat(location, notNullValue());

        UUID createdEmployeeId = extractIdFromPositionLocation(location);
        assertTrue("Position has not been saved in database", positionRepository.existsByUuid(createdEmployeeId));
    }

    @Test
    public void givenPositionsWithEmployees_whenGetPositions_thenStatusOk() throws Exception {
        // given
        List<Position> positions = createPositionsWithEmployees(asList(Pair.of("Developer", 2), Pair.of("Tester", 3)), entityManager);

        // when
        MockHttpServletResponse response = mvc.perform(
                get("/position")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn().getResponse();

        // then
        PositionDto[] positionDtos = mapper.readValue(response.getContentAsString(), PositionDto[].class);

        assertExactlyMatch(positions, asList(positionDtos));
    }

    @Test
    public void givenPosition_whenGetPosition_thenStatusOk() throws Exception {
        // given
        Position position = createPositionsWithEmployees(Collections.singletonList(Pair.of("Developer", 2)), entityManager).get(0);

        // when
        mvc.perform(
                get("/position/" + position.getUuid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(position.getName())))
                .andExpect(jsonPath("$.employeeCount", equalTo(position.getEmployees().size())));
    }


    @Test
    public void whenGetInvalidPosition_thenStatusNotFound() throws Exception {
        UUID invalidPositionId = randomUUID();

        MockHttpServletResponse res = mvc.perform(
                get("/position/" + invalidPositionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
        .andReturn().getResponse();

        System.out.println(res.getBufferSize());
    }

    private UUID extractIdFromPositionLocation(String location) {
        String[] locationSegments = URI.create(location).getPath().split("/");
        Assert.assertEquals(locationSegments.length, 3);
        return UUID.fromString(locationSegments[locationSegments.length - 1]);
    }
}