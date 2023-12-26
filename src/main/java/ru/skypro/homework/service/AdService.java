package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.AdsDTO;
import ru.skypro.homework.dto.CreateOrUpdateAdDTO;

import java.io.IOException;
import java.util.List;

public interface AdService {
    List<AdDTO> getAllAds();

    AdDTO createAd(CreateOrUpdateAdDTO createOrUpdateAdDTO, MultipartFile imageFile);

    AdDTO getAdById(int id);

    void deleteAd(int id);

    AdDTO updateAd(int id, CreateOrUpdateAdDTO createOrUpdateAdDTO);

    AdsDTO getAdsByUser();

    boolean updateAdImage(int id, MultipartFile image);

    byte[] getImage(int adId) throws IOException;
}
