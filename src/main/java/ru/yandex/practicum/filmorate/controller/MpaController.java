package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<MpaRating> getMpaRatings() {
        log.info("Получен запрос на получение списка рейтингов");
        return mpaService.getAll();
    }

    @GetMapping("/{id}")
    public MpaRating getMpaById(@PathVariable Long id) {
        log.info("Получен запрос на получение рейтинга фильма по id = " + id);
        return mpaService.findById(id);
    }
}
