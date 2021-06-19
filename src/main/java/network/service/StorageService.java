package network.service;

import network.api.response.CommonResponseData;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    CommonResponseData uploadImage(MultipartFile file);
}
