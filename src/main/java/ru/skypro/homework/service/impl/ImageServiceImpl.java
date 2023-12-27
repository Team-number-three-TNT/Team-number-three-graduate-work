package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.ImageIsTooBigException;
import ru.skypro.homework.exception.ImageNotFoundException;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.service.ImageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Value("${path.to.images.folder}")
    private String imagesDir;

    /**
     * Сохранение изображения в БД без заполнения поля владельца
     *
     * @param imageFile переданный файл
     * @return сохраненную в БД Image, но с null в полях user_id и ad_id
     * @throws IOException при возникновении проблем с директорией
     */
    @Override
    public Image saveImageExceptHolderField(MultipartFile imageFile) throws IOException {
        long imageSize = imageFile.getSize();
        checkImageSize(imageSize);

        Image image = new Image();
        image.setFileSize(imageFile.getSize());
        image.setMediaType(imageFile.getContentType());
        Image savedImage = saveImageToDb(image);

        log.debug("Создание директории, удаление изображения, если необходимо");
        Path filePath = Path.of(imagesDir, image.getId() + "." +
                getExtension(imageFile.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        savedImage.setFilePath(filePath.toString());

        try {
            saveImageFileToProject(imageFile, savedImage);
        } catch (IOException e) {
            imageRepository.delete(savedImage);
        }
        return saveImageToDb(savedImage);
    }

    /**
     * Обновление фотографии объявления или аватарки пользователя.
     * Сначала удаляет старую фотографию в памяти, затем изменяет поля сущности
     * и сохраняет эту обновленную сущность в Бд и возвращает ее из метода.
     *
     * @param imageFile новое изображение
     * @param holderId  Id сущности, чье изображение необходимо обновить
     * @return обновленную сущность Image
     * @throws IOException при возникновении проблем с директорией
     */
    @Override
    public Image updateImage(MultipartFile imageFile, int holderId) throws IOException {
        long imageSize = imageFile.getSize();
        checkImageSize(imageSize);
        Image image;

        image = imageRepository.findByUserId(holderId).orElse(null);

        if (image == null) {
            image = imageRepository.findByAdId(holderId)
                    .orElseThrow(() -> new ImageNotFoundException("Предыдущее изображение, которое необходимо было обновить, не было найдено"));
        }

        log.debug("Создание директории, удаление старого изображения");
        Path filePath = Path.of(imagesDir, image.getId() + "." +
                getExtension(image.getFilePath()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        image.setFileSize(imageFile.getSize());
        image.setMediaType(imageFile.getContentType());
        image.setFilePath(Path.of(imagesDir, image.getId() + "." +
                getExtension(imageFile.getOriginalFilename())).toString());
        saveImageFileToProject(imageFile, image);
        return saveImageToDb(image);
    }

    /**
     * Сохранение сущности в Бд без дополнительных операций
     *
     * @param image сохраняемая сущность
     * @return сохранненая сущность {@link Image}
     */
    @Override
    public Image saveImageToDb(Image image) {
        return imageRepository.save(image);
    }

    /**
     * Получение изображения в виде массива байтов из памяти
     *
     * @param imageId Id изображение, которое необходимо получить
     * @return byte[]
     * @throws IOException если возникнут проблемы при чтении файла
     */
    @Override
    public byte[] getImage(int imageId) throws IOException {
        Path path = Path.of(imagesDir, imageId + "." + getExtension(imageRepository.findFilePathById(imageId)));
        return new ByteArrayResource(Files
                .readAllBytes(path)
        ).getByteArray();
    }

    /**
     * Проверка размера передаваемой фотографии
     *
     * @param imageSize размер в long
     * @throws ImageIsTooBigException если размер превышает 5MB
     */
    private void checkImageSize(long imageSize) {
        if (imageSize > (1024 * 5000)) {
            log.error("Размер переданного изображения слишком велик. Размер = {} MB", imageSize / 1024 / (double) 1000);
            throw new ImageIsTooBigException("Размер изображения превышает максимально допустимое значение, равное 5 MB");
        }
    }

    private void saveImageFileToProject(MultipartFile imageFile, Image image) throws IOException {
        try (
                InputStream is = imageFile.getInputStream();
                OutputStream os = Files.newOutputStream(Path.of(image.getFilePath()), CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }
    }

    /**
     * Вспомогательный метод, который отвечает за извлечение расширение файла из его имени
     *
     * @param fileName имя файла, расширение которого необходимо получить
     * @return Строку, которая представляет извлеченное расширение файла
     */
    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
