package api.fitnessbuddyback.service;


import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    public String generatePresignedUrl(String objectName) throws Exception {
        String bucketName = "documents";
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .method(Method.PUT)
                        .build()
        );
    }
}