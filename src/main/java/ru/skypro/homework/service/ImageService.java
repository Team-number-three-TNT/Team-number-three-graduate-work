package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Image;

import java.io.IOException;

public interface ImageService {
    Image saveImageExceptHolderField(MultipartFile imageFile) throws IOException;

    Image updateImage(MultipartFile imageFile, int holderId) throws IOException;

    Image saveImageToDb(Image image);

    byte[] getImage(int imageId) throws IOException;
}
