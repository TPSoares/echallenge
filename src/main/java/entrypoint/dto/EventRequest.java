package entrypoint.dto;

import lombok.Data;

@Data
public class EventRequest {
    private String type;
    private String origin;
    private String destination;
    private Integer amount;
}

