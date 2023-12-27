package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.AdsDTO;
import ru.skypro.homework.dto.CreateOrUpdateAdDTO;
import ru.skypro.homework.dto.ExtendedAdDTO;

import java.io.IOException;

public interface AdService {
    AdsDTO getAllAds();

    AdDTO createAd(CreateOrUpdateAdDTO createOrUpdateAdDTO, MultipartFile imageFile) throws IOException;

    ExtendedAdDTO getAdById(int id);

    void deleteAd(int id);

    AdDTO updateAd(int id, CreateOrUpdateAdDTO createOrUpdateAdDTO);

    AdsDTO getAdsByUser();

    void updateAdImage(int id, MultipartFile image) throws IOException;
}
