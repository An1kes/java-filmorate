package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpaRating.MpaRatingStorage;

import java.util.Collection;

@Slf4j
@Service
public class MpaService {

    private final MpaRatingStorage mpaRatingStorage;

    private MpaService(@Qualifier("MpaRatingStorage") MpaRatingStorage mpaRatingStorage) {
        this.mpaRatingStorage = mpaRatingStorage;
    }

    public Collection<MpaRating> getAll() {
        return mpaRatingStorage.getAll();
    }

    public MpaRating getMpaRatingOrThrow(Long mpaRatingId) {
        return mpaRatingStorage.findById(mpaRatingId)
                .orElseThrow(() -> new NotFoundException("Рейтинг фильма с айди id= " + mpaRatingId + " не найден."));
    }

    public MpaRating findById(Long mpaRatingId) {
        return getMpaRatingOrThrow(mpaRatingId);
    }
}
