 import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(

            @NotNull MultipartFile file,
            @NotNull Integer book,
            @NotNull Integer userId) {
         final String fileUploadSubPath = "users" + separator + userId;
         return uploadFile(sourceFile, fileUploadPath);
    }
    private String uploadFile(
        @Nonnull MultipartFile sourceFile,
        @Nonnull String fileUploadSubPath) {
      final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
      File targetFolder = new File(finalUploadPath);
      if (!targetFolder.exists()) {
           boolean folderCreated = targetFolder.mkdirs();
           if (!folderCreated) {
               log.warn("Failed to create the target folder");
               return null;
            }
       }
       final string fileExtension = getFileExtension(sourceFile.getOriginalFilename());
       String targetFilePath = finalUploadPath + separator + system.currentTimeMillis() + "." + fileExtension 
       Path tagetaPth = Paths.get(targetFilePath); 
       try {
        File.write(tagetaPth, sourceFile.getBytes());
        log.info("file saved to" + targetFilePath);
        return targetFilePath;
       } catch (I0Exception e) {
        log.error("File was not saved", e) ;
       }
       return null;
    }

    private string getFileExtension(String fileName){
        if (fileName == null || fileName.isEmpty() ){
            return "";  
        }
        int lasrDotIndex = fileName.lasr Index0f(str:".");
        if (lasrDotIndex == -1) {
            return "";
        }
    }
    return fileName.substring(beginIndex + 1).toLowerCase(); 
    )
}  
