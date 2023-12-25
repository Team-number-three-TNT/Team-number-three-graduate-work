package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.AdsDTO;
import ru.skypro.homework.dto.CreateOrUpdateAdDTO;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper mapper;

    @Value("{path.to.ad-images.folder}")
    private String imagesDir;

    @Override
    public List<AdDTO> getAllAds() {
        return adRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AdDTO createAd(CreateOrUpdateAdDTO createOrUpdateAdDTO) {
        Ad ad = mapper.toEntity(createOrUpdateAdDTO);
        Ad savedAd = adRepository.save(ad);
        return mapper.toDto(savedAd);
    }

    @Override
    public AdDTO getAdById(int id) {
        return adRepository.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public boolean deleteAd(int id) {
        if (adRepository.existsById(id)) {
            adRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public AdDTO updateAd(int id, CreateOrUpdateAdDTO createOrUpdateAdDTO) {
        return adRepository.findById(id)
                .map(ad -> {
                    mapper.toEntity(createOrUpdateAdDTO);
                    return adRepository.save(ad);
                })
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public AdsDTO getAdsByUser() {
        User user = getCurrentUser();
        return mapper.toAdsDto(adRepository.findAllByUserId(user.getId()));
    }

    @Override
    public boolean updateAdImage(int id, MultipartFile image) {
        return false;
    }

    @Override
    public byte[] getImage(int adId) throws IOException {
        Path path = Path.of(imagesDir + ":" + adId);
        return new ByteArrayResource(Files
                .readAllBytes(path)
        ).getByteArray();
    }

    /**
     * Метод, который возвращает текущего пользователя
     *
     * @return User
     * @see User
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email: " + email + " is not found"));
    }
}


