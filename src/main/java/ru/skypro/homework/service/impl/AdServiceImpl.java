package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
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
import ru.skypro.homework.mapper.Mapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Override
    public List<AdDTO> getAllAds() {
        return adRepository.findAll().stream()
                .map(mapper::toAdDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AdDTO createAd(CreateOrUpdateAdDTO createOrUpdateAdDTO) {
        Ad ad = mapper.toAd(createOrUpdateAdDTO);
        Ad savedAd = adRepository.save(ad);
        return mapper.toAdDTO(savedAd);
    }

    @Override
    public AdDTO getAdById(int id) {
        return adRepository.findById(id)
                .map(mapper::toAdDTO)
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
                    mapper.toAd(createOrUpdateAdDTO);
                    return adRepository.save(ad);
                })
                .map(mapper::toAdDTO)
                .orElse(null);
    }

    @Override
    public AdsDTO getAdsByUser() {
        User user = getCurrentUser();
        return mapper.toAdsDTO(adRepository.findAllByUserId(user.getId()));
    }

    @Override
    public boolean updateAdImage(int id, MultipartFile image) {
        return false;
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


