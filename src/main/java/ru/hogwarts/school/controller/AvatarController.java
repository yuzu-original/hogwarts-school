package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.dto.AvatarNotDetailDTO;
import ru.hogwarts.school.service.AvatarService;

import java.util.Collection;

@RestController
@RequestMapping("avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping
    public ResponseEntity<Collection<AvatarNotDetailDTO>> findAllByPage(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "page-size") Integer pageSize) {
        return ResponseEntity.ok(avatarService.findAllByPage(page - 1, pageSize));
    }
}
