package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.CreateOrUpdateAdDTO;
import ru.skypro.homework.service.AdService;

import java.util.List;


@Service
public class AdServiceImpl implements AdService {

    @Override
    public List<AdDTO> getAllAds() {
        return null;
    }

    @Override
    public AdDTO createAd(CreateOrUpdateAdDTO createOrUpdateAdDTO) {
        return null;
    }

    @Override
    public AdDTO getAdById(int id) {
        return null;
    }

    @Override
    public boolean deleteAd(int id) {
    return false;
    }

    @Override
    public AdDTO updateAd(int id, CreateOrUpdateAdDTO createOrUpdateAdDTO) {
        return null;
    }

    @Override
    public List<AdDTO> getAdsByUser(int id) {
        return null;
    }

    @Override
    public boolean updateAdImage(int id, MultipartFile image) {
        return false;
    }
}



