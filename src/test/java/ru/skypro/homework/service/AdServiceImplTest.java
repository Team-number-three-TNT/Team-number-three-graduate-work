package ru.skypro.homework.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.impl.AdServiceImpl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdServiceImplTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private AdMapper adMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdServiceImpl adService;

    // Предварительная настройка данных
    private Ad ad;
    private User user;
    private AdDTO adDTO;

    @BeforeEach
    void setUp() {
        // Инициализация данных
        user = new User();
        user.setId(1);
        user.setRole(Role.USER);

        ad = new Ad();
        ad.setId(1);
        ad.setUser(user);

        adDTO = new AdDTO();
        adDTO.setPk(1);

        // Настройка SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(new UserPrincipalDTO());
    }

    @Test
    @DisplayName("'getAllAds' should return all ads")
    void testGetAllAds() {
        List<Ad> ads = List.of(new Ad()); // Замените на реальные тестовые данные
        when(adRepository.findAll()).thenReturn(ads);
        when(adMapper.toDto(any(Ad.class))).thenReturn(new AdDTO());

        AdsDTO result = adService.getAllAds();

        assertThat(result).isNotNull();
        assertThat(result.getCount()).isEqualTo(ads.size());
        verify(adMapper, times(ads.size())).toDto(any(Ad.class));
    }

    @Test
    @DisplayName("createAd should create a new ad")
    void testCreateAd() throws IOException {
        CreateOrUpdateAdDTO dto = new CreateOrUpdateAdDTO();
        Ad ad = new Ad();
        when(adMapper.toEntity(dto)).thenReturn(ad);
        when(adRepository.save(ad)).thenReturn(ad);
        when(imageService.saveImageExceptHolderField(any())).thenReturn(new Image());

        AdDTO result = adService.createAd(dto, null);

        assertThat(result).isNotNull();
        verify(adRepository).save(any(Ad.class));
        verify(imageService).saveImageExceptHolderField(any());
    }

    @Test
    @DisplayName("getAdById should return ad when it exists")
    void testGetAdById() {
        when(adRepository.findById(1)).thenReturn(Optional.of(new Ad()));
        when(adMapper.toExtendedDto(any(Ad.class))).thenReturn(new ExtendedAdDTO());

        ExtendedAdDTO result = adService.getAdById(1);

        assertThat(result).isNotNull();
        verify(adMapper).toExtendedDto(any(Ad.class));
    }

    @Test
    @DisplayName("getAdById should throw AdNotFoundException if ad does not exist")
    void getAdById_ShouldThrowAdNotFoundException() {
        when(adRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adService.getAdById(1))
                .isInstanceOf(AdNotFoundException.class)
                .hasMessageContaining("Ad not found with ID: 1");
    }

    @Test
    @DisplayName("deleteAd should delete ad when user is authorized")
    void testDeleteAd() {
        Ad ad = new Ad();
        ad.setUser(new User());
        when(adRepository.findById(1)).thenReturn(Optional.of(ad));
        doNothing().when(adRepository).deleteById(1);

        adService.deleteAd(1);

        verify(adRepository).deleteById(1);
    }

    @Test
    @DisplayName("updateAd should update ad when it exists")
    void testUpdateAd() {
        CreateOrUpdateAdDTO dto = new CreateOrUpdateAdDTO();
        Ad ad = new Ad();
        when(adRepository.findById(1)).thenReturn(Optional.of(ad));
        when(adMapper.toDto(any(Ad.class))).thenReturn(new AdDTO());

        AdDTO result = adService.updateAd(1, dto);

        assertThat(result).isNotNull();
        verify(adRepository).save(any(Ad.class));
    }

    @Test
    @DisplayName("getAdsByUser should return ads for the current user")
    void testGetAdsByUser() {
        User user = new User();
        user.setId(1);
        List<Ad> ads = List.of(new Ad());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(adRepository.findAllByUserId(1)).thenReturn(ads);
        when(adMapper.toDto(any(Ad.class))).thenReturn(new AdDTO());

        AdsDTO result = adService.getAdsByUser();

        assertThat(result).isNotNull();
        assertThat(result.getCount()).isEqualTo(ads.size());
        verify(adRepository).findAllByUserId(1);
        verify(adMapper, times(ads.size())).toDto(any(Ad.class));
    }

    @Test
    @DisplayName("updateAdImage should update image for ad")
    void testUpdateAdImage() throws IOException {
        when(adRepository.findById(1)).thenReturn(Optional.of(new Ad()));
        doNothing().when(imageService).updateImage(any(), anyInt());

        adService.updateAdImage(1, null);

        verify(imageService).updateImage(any(), eq(1));
    }

    }
