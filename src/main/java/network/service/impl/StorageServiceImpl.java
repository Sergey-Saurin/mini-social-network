package network.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import network.api.response.UploadImageResponse;
import network.model.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import network.api.response.CommonResponseData;
import network.repository.PersonRepository;
import network.service.StorageService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Created by timur_guliev on 03.04.2021.
 */
@Service
public class StorageServiceImpl implements StorageService {

    @Value("${cloudinary.url}")
    String cloudinaryUrl;

    private final AccountServiceImpl accountServiceImpl;
    private final PersonRepository personRepository;

    @Autowired
    public StorageServiceImpl(AccountServiceImpl accountServiceImpl, PersonRepository personRepository) {
        this.accountServiceImpl = accountServiceImpl;
        this.personRepository = personRepository;
    }

    @Override
    public CommonResponseData uploadImage(MultipartFile file) {
        String error = "";
        Cloudinary cloudinary = new Cloudinary(cloudinaryUrl);
        Map uploadResult;
        UploadImageResponse uploadImageResponse = new UploadImageResponse();

        try {
            Person currentPerson = accountServiceImpl.getCurrentPerson();
            File uploadedFile = convertMultiPartToFile(file);
            uploadResult = cloudinary.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
            uploadImageResponse = UploadImageResponse.fromUploadImage(uploadResult, currentPerson.getId());
            currentPerson.setPhoto(uploadImageResponse.getRelativeFilePath());
            personRepository.save(currentPerson);
        } catch (IOException e) {
            error = e.getMessage();
        }

        CommonResponseData commonResponseData = new CommonResponseData();
        commonResponseData.setError(error);
        commonResponseData.setTimestamp(LocalDateTime.now());
        commonResponseData.setData(uploadImageResponse);

        return commonResponseData;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
