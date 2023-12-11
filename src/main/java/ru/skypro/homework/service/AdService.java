package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.CreateOrUpdateAdDTO;

import java.util.List;

public interface AdService {
    List<AdDTO> getAllAds();

    AdDTO createAd(CreateOrUpdateAdDTO createOrUpdateAdDTO);

    AdDTO getAdById(int id);

    boolean deleteAd(int id);

    AdDTO updateAd(int id, CreateOrUpdateAdDTO createOrUpdateAdDTO);
    List<AdDTO> getAdsByUser(int id);

    boolean updateAdImage(int id, MultipartFile image);
}
