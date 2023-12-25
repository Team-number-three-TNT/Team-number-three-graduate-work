package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.AdsDTO;
import ru.skypro.homework.dto.CreateOrUpdateAdDTO;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.Ad;
import org.springframework.security.core.Authentication;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private static final Logger logger = LoggerFactory.getLogger(AdServiceImpl.class);
    private final UserRepository userRepository;
    private final AdMapper mapper;

    @Value("{path.to.ad-images.folder}")
    private String imagesDir;

    /**
     * Получает все объявления из репозитория.
     *
     * @return список всех объявлений.
     */
    @Override
    public List<AdDTO> getAllAds() {
        List<AdDTO> ads = adRepository.findAll().stream()
                .map(adMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Fetched all ads, total count: {}", ads.size());
        return ads;
    }
    /**
     * Создает новое объявление.
     *
     * @param createOrUpdateAdDTO DTO для создания объявления.
     * @return созданное объявление.
     */
    @Override
    public AdDTO createAd(CreateOrUpdateAdDTO createOrUpdateAdDTO) {
        Ad ad = adMapper.toEntity(createOrUpdateAdDTO);
        Ad savedAd = adRepository.save(ad);
        logger.info("Created a new ad with ID: {}", savedAd.getId());
        return adMapper.toDto(savedAd);
    }
    /**
     * Получает объявление по его идентификатору.
     *
     * @param id идентификатор объявления.
     * @return объявление.
     * @throws AdNotFoundException если объявление не найдено.
     */
    @Override
    public AdDTO getAdById(int id) {
        return adRepository.findById(id)
                .map(adMapper::toDto)
                .orElseThrow(() -> {
                    logger.error("Ad not found with ID: {}", id);
                    return new AdNotFoundException("Ad not found with ID: " + id);
                 });
    }
    /**
     * Удаляет объявление по идентификатору.
     *
     * @param id идентификатор объявления.
     * @throws AdNotFoundException если объявление не найдено.
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
     * @param id идентификатор объявления.
     * @param createOrUpdateAdDTO DTO для обновления объявления.
     * @return обновленное объявление.
     * @throws AdNotFoundException если объявление не найдено.
     * @throws AccessDeniedException если пользователь не имеет права обновить объявление.
     */
    @Override
    public AdDTO updateAd(int id, CreateOrUpdateAdDTO createOrUpdateAdDTO) {
        return adRepository.findById(id)
                .map(ad -> {
                    checkPermission(ad.getUser().getId());
                    Ad updatedAd = adMapper.toEntity(createOrUpdateAdDTO);
                    updatedAd.setId(ad.getId());
                    updatedAd.setUser(ad.getUser());
                    return adRepository.save(updatedAd);
                })
                .map(adMapper::toDto)
                .orElseThrow(() -> new AdNotFoundException("Ad not found with ID: " + id));
    }

    @Override
    public AdsDTO getAdsByUser() {
        User user = getCurrentUser();
        return mapper.toAdsDto(adRepository.findAllByUserId(user.getId()));
    }

    @Override
    public boolean updateAdImage(int adId, MultipartFile image) {

            return false;
        }
    /**
     * Проверяет, имеет ли текущий пользователь права для выполнения операции.
     *
     * @param ownerId идентификатор владельца объявления.
     * @throws @AccessDeniedException если текущий пользователь не имеет прав.
     */
    private void checkPermission(Integer ownerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        if (!currentUser.getId().equals(ownerId) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Access denied");
        }
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
