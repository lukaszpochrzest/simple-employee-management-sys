package pl.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionDto {

    public PositionDto(UUID id, String name, Long employeeCount) {
        this.id = id.toString();
        this.name = name;
        this.employeeCount = employeeCount;
    }

    private String id;

    private String name;

    private Long employeeCount;

}
