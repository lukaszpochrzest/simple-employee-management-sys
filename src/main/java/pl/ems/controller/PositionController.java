package pl.ems.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pl.ems.dto.PositionDto;
import pl.ems.dto.SavePositionDto;
import pl.ems.service.PositionService;
import pl.ems.util.Util;

import java.util.List;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PositionController {

    private PositionService positionService;

    @RequestMapping(value = "/position", method = POST)
    public ResponseEntity savePosition(UriComponentsBuilder builder,
                                       @RequestBody SavePositionDto savePositionDto) {
        UUID uuid = positionService.savePosition(savePositionDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/position/{id}").buildAndExpand(uuid.toString()).toUri());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/position", method = GET)
    @ResponseBody
    public List<PositionDto> getPositions() {
        return positionService.getPositions();
    }

    @RequestMapping(value = "/position/{id}", method = GET)
    @ResponseBody
    public PositionDto getPosition(@PathVariable("id") String uuid) {
        return positionService.getPosition(Util.parseUUID(uuid));
    }
}
