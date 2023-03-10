package uz.nt.uzumproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.nt.uzumproject.dto.ImageDto;
import uz.nt.uzumproject.dto.ResponseDto;
import uz.nt.uzumproject.model.Image;
import uz.nt.uzumproject.repository.ImageRepository;
import uz.nt.uzumproject.service.mapper.ImageMapper;
import uz.nt.uzumproject.service.mapper.UsersMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final ImageRepository imageRepository;
    private final UsersMapper usersMapper;
    private final ImageMapper imageMapper;
    public ResponseDto<ImageDto> saveImage(MultipartFile file) {

        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setExtension(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
        image.setCreatedAt(LocalDateTime.now());

        try {
            String filePath;
            Files.copy(file.getInputStream(), Path.of(filePath = filePath(image.getExtension())));
            image.setUrl(filePath);
            imageRepository.save(image);

            return ResponseDto.<ImageDto>builder()
                    .data(imageMapper.toDto(image))
                    .message("OK")
                    .success(true)
                    .build();
        } catch (IOException e) {
            log.error("Error while saving file: {}", e.getMessage());
            return ResponseDto.<ImageDto>builder()
                    .code(2)
                    .data(imageMapper.toDto(image))
                    .message("Error while saving file: " + e.getMessage())
                    .build();
        }
    }

    private synchronized String filePath(String ext){
        LocalDate localDate = LocalDate.now();
        String path = localDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        File file = new File("upload/" + path);
        if (!file.exists()){
            file.mkdirs();
        }
        return file.getPath() + "\\"+ System.currentTimeMillis() + ext;
    }
}
