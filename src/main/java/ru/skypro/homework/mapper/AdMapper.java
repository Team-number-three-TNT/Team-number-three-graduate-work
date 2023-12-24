package ru.skypro.homework.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.CreateOrUpdateAdDTO;
import ru.skypro.homework.dto.ExtendedAdDTO;
import ru.skypro.homework.entity.Ad;
@Component
public class AdMapper {
    private final ModelMapper modelMapper;

    public AdMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public AdDTO toDto(Ad ad) {
        AdDTO adDTO = modelMapper.map(ad, AdDTO.class);
        adDTO.setPk(ad.getId());
        adDTO.setAuthor(ad.getUser() != null ? ad.getUser().getId() : null);
        return adDTO;
    }

    public Ad toEntity(CreateOrUpdateAdDTO createOrUpdateAdDTO) {
        return modelMapper.map(createOrUpdateAdDTO, Ad.class);
    }

    public ExtendedAdDTO toExtendedDto(Ad ad) {
        ExtendedAdDTO extendedAdDTO = modelMapper.map(ad, ExtendedAdDTO.class);
        extendedAdDTO.setPk(ad.getId());
        if (ad.getUser() != null) {
            extendedAdDTO.setAuthorFirstName(ad.getUser().getFirstName());
            extendedAdDTO.setAuthorLastName(ad.getUser().getLastName());
            extendedAdDTO.setEmail(ad.getUser().getEmail());
            extendedAdDTO.setPhone(ad.getUser().getPhone());
        }
        return extendedAdDTO;
    }
}