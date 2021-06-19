package network.api.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import network.repository.util.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

/**
 * Created by timur_guliev on 03.04.2021.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UploadImageResponse extends ResponseData {
    private String id;
    private long ownerId;
    private String fileName;
    private String relativeFilePath;
    private String rawFileURL;
    private String fileFormat;
    private long bytes;
    private String fileType;
    private long createdAt;

    public static UploadImageResponse fromUploadImage(Map uploadResult, long personId) {
        UploadImageResponse upIm = new UploadImageResponse();
        upIm.setId((String) uploadResult.get("asset_id"));
        upIm.setOwnerId(personId);
        upIm.setFileName((String) uploadResult.get("original_filename"));
        upIm.setRelativeFilePath((String) uploadResult.get("url"));
        upIm.setFileFormat((String) uploadResult.get("format"));
        upIm.setBytes((Integer) uploadResult.get("bytes"));
        upIm.setFileType((String) uploadResult.get("resource_type"));
        upIm.setCreatedAt(Utils.getLongFromLocalDateTime(LocalDateTime.parse(
            (String) uploadResult.get("created_at"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH))
        ));
        return upIm;
    }
}
