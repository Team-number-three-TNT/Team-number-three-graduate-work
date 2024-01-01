package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;


@Service
@RequiredArgsConstructor
@Transactional
public class AdServiceImpl implements AdService {

    private static final Logger logger = LoggerFactory.getLogger(AdServiceImpl.class);
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final AdMapper mapper;

    /**
     * Получает все объявления из репозитория.
     *
     * @return список всех объявлений.
     */
    @Override
    public AdsDTO getAllAds() {
        AdsDTO adsDto = mapper.toAdsDto(adRepository.findAll());
        logger.info("Fetched all ads, total count: {}", adsDto.getCount());
        return adsDto;
    }

    /**
     * Создает новое объявление.
     *
     * @param createOrUpdateAdDTO DTO для создания объявления.
     * @param imageFile           фотография предмета
     * @return созданное объявление.
     */
    @Override
    public AdDTO createAd(CreateOrUpdateAdDTO createOrUpdateAdDTO, MultipartFile imageFile) throws IOException {
        Ad ad = mapper.toEntity(createOrUpdateAdDTO);
        ad.setUser(getCurrentUser());
        Ad savedAd = adRepository.save(ad);
        Image image = imageService.saveImageExceptHolderField(imageFile);
        image.setAd(savedAd);
        imageService.saveImageToDb(image);
        savedAd.setImage(image);
        adRepository.save(savedAd);
        logger.info("Created a new ad: {}, with corresponding image: {}", savedAd.getId(), image.getId());
        return mapper.toDto(savedAd);
    }

    /**
     * Получает объявление по его идентификатору.
     *
     * @param id идентификатор объявления.
     * @return объявление.
     * @throws AdNotFoundException если объявление не найдено.
     */
    @Override
    public ExtendedAdDTO getAdById(int id) {
        return adRepository.findById(id)
                .map(mapper::toExtendedDto)
                .orElseThrow(() -> {
                    logger.error("Ad not found with ID: {}", id);
                    return new AdNotFoundException("Ad not found with ID: " + id);
                });
    }

    /**
     * Удаляет объявление по идентификатору.
     *
     * @param id идентификатор объявления.
     * @throws AdNotFoundException   если объявление не найдено.
     * @throws AccessDeniedException если пользователь не имеет права удалить объявление.
     */
    @Override
    public void deleteAd(int id) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Ad not found with ID: " + id));
        checkPermission(ad.getUser().getId());
        adRepository.deleteById(id);
    }

    /**
     * Обновляет существующее объявление.
     *
     * @param id                  идентификатор объявления.
     * @param createOrUpdateAdDTO DTO для обновления объявления.
     * @return обновленное объявление.
     * @throws AdNotFoundException   если объявление не найдено.
     * @throws AccessDeniedException если пользователь не имеет права обновить объявление.
     */
    @Override
    public AdDTO updateAd(int id, CreateOrUpdateAdDTO createOrUpdateAdDTO) {
        Ad ad = adRepository.findById(id).orElseThrow(() -> new AdNotFoundException("Ad not found with ID: " + id));
        ad.setPrice(createOrUpdateAdDTO.getPrice());
        ad.setDescription(createOrUpdateAdDTO.getDescription());
        ad.setTitle(createOrUpdateAdDTO.getTitle());
        return mapper.toDto(ad);
    }

    @Override
    public AdsDTO getAdsByUser() {
        User user = getCurrentUser();
        return mapper.toAdsDto(adRepository.findAllByUserId(user.getId()));
    }

    @Override
    public void updateAdImage(int adId, MultipartFile imageFile) throws IOException {
        imageService.updateImage(imageFile,
                adRepository.findById(adId).orElseThrow(() -> new AdNotFoundException("Объявление: " + adId + " не найдено")).getId());
    }

    /**
     * Проверяет, имеет ли текущий пользователь права для выполнения операции.
     *
     * @param ownerId идентификатор владельца объявления.
     * @throws AccessDeniedException если текущий пользователь не имеет прав.
     */
    private void checkPermission(Integer ownerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        if (!currentUser.getId().equals(ownerId) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Access denied");
        }
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
