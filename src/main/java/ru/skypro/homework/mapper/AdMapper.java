package ru.skypro.homework.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.AdsDTO;
import ru.skypro.homework.dto.CreateOrUpdateAdDTO;
import ru.skypro.homework.dto.ExtendedAdDTO;
import ru.skypro.homework.entity.Ad;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AdMapper {
    private final ModelMapper mapper;

    public AdDTO toDto(Ad ad) {
        AdDTO adDTO = mapper.map(ad, AdDTO.class);
        adDTO.setPk(ad.getId());
        adDTO.setAuthor(ad.getUser() != null ? ad.getUser().getId() : null);
        return adDTO;
    }

    public Ad toEntity(CreateOrUpdateAdDTO createOrUpdateAdDTO) {
        return mapper.map(createOrUpdateAdDTO, Ad.class);
    }

    public ExtendedAdDTO toExtendedDto(Ad ad) {
        ExtendedAdDTO extendedAdDTO = mapper.map(ad, ExtendedAdDTO.class);
        extendedAdDTO.setPk(ad.getId());
        if (ad.getUser() != null) {
            extendedAdDTO.setAuthorFirstName(ad.getUser().getFirstName());
            extendedAdDTO.setAuthorLastName(ad.getUser().getLastName());
            extendedAdDTO.setEmail(ad.getUser().getEmail());
            extendedAdDTO.setPhone(ad.getUser().getPhone());
        }
        return extendedAdDTO;
    }

    public AdsDTO toAdsDto(List<Ad> ads) {
        AdsDTO adsDTO = new AdsDTO();
        adsDTO.setCount(ads.size());
        adsDTO.setResults(ads.stream().map(e -> toDto(e)).collect(Collectors.toList()));
        return adsDTO;
    }
}