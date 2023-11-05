package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.service.OtherService;

@RestController
@RequestMapping("other")
public class OtherController {
    private final OtherService otherService;

    public OtherController(OtherService otherService) {
        this.otherService = otherService;
    }

    @GetMapping("sum")
    public ResponseEntity<Long> getSum() {
        return ResponseEntity.ok(otherService.getSum());
    }
}
