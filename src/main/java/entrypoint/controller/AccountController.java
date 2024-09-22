package entrypoint.controller;

import entrypoint.dto.BalanceResponse;
import entrypoint.dto.EventRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class AccountController {

    @PostMapping
    public BalanceResponse handleEvent(@RequestBody EventRequest request) {

        return BalanceResponse.builder()
                .build();
    }
}
