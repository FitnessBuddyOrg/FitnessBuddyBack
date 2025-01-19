package api.fitnessbuddyback.service;


import api.fitnessbuddyback.entity.User;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    public void uploadProfilePicture(MultipartFile file, User user) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("documents")
                            .object("users/" + user.getId() + "/profile-picture.jpg")
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload profile picture");
        }
    }

    public String getProfilePictureUrl(User user) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket("documents")
                            .object("users/" + user.getId() + "/profile-picture.jpg")
                            .expiry(60 * 60 * 24 * 7)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to get profile picture URL");
        }
    }
}