package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.AdsDTO;
import ru.skypro.homework.dto.CreateOrUpdateAdDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.service.AdService;

import java.util.Collections;
import java.util.List;


@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/ads")
public class AdController {
    private final AdService adService;

    @GetMapping("/ads")
    public ResponseEntity<List<AdDTO>> getAllAds() {
        //получить все объявления
        List<AdDTO> ads = adService.getAllAds();
        if (ads.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(ads);
    }

    @PostMapping
    public ResponseEntity<AdDTO> createAd(@RequestBody CreateOrUpdateAdDTO createOrUpdateAdDTO) {
        // добавление нового объявления
        AdDTO ad = adService.createAd(createOrUpdateAdDTO);
        if (ad == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ad, HttpStatus.CREATED);
    }

    @GetMapping("/ads/{id}")
    public ResponseEntity<AdDTO> getAdById(@PathVariable int id) {
        AdDTO ad = adService.getAdById(id);
        if (ad == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ad);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable int id) {
        boolean isDeleted = adService.deleteAd(id);
        if (!isDeleted) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdDTO> updateAd(@PathVariable int id, @RequestBody CreateOrUpdateAdDTO createOrUpdateAdDTO) {
        AdDTO updatedAd = adService.updateAd(id, createOrUpdateAdDTO);
        if (updatedAd == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(updatedAd);
    }
    @GetMapping("/me")
    public ResponseEntity<AdsDTO> getMyAds(Authentication authentication) {
        if (authentication == null) {
            // если пользователь не авторизован, возвращаем статус 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDTO user = (UserDTO) authentication.getPrincipal();
        Integer userId = user.getId(); // Получаем ID пользователя из DTO

        // сервис возвращает список объявлений для данного пользователя
        List<AdDTO> userAds = adService.getAdsByUser(userId);

        if (userAds.isEmpty()) {
            // если объявлений нет, возвращаем пустой список с 200 OK
            return ResponseEntity.ok(new AdsDTO(0, Collections.emptyList()));
        }

        // если объявления есть, возвращаем их вместе с их количеством
        return ResponseEntity.ok(new AdsDTO(userAds.size(), userAds));
    }

    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateAdImage(@PathVariable int id, @RequestParam("image") MultipartFile image) {
        boolean isUpdated = adService.updateAdImage(id, image);
        if (!isUpdated) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().build();
    }
}
