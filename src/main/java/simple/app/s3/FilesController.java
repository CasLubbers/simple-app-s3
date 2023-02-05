package simple.app.s3;


import com.baeldung.openapi.api.FilesApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;

import java.util.List;
import java.util.stream.Collectors;

record File(String fileName){}
@RestController
public class FilesController  implements FilesApi {
    final private String bucketName;
    final private String accessKey;
    final private String secretKey;
    @Autowired
    public FilesController(@Value("${aws.secret.key}") String secretKey,
                           @Value("${aws.access.key}") String accessKey,
                           @Value("${s3.bucket.name}") String bucketName){
        this.bucketName = bucketName;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @Override
    public ResponseEntity<List<Object>> filesGet() {
        ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(bucketName)
                .build();
        S3Client s3 = buildS3Client(this.accessKey,this.secretKey, Region.EU_WEST_1);

        ListObjectsResponse res = s3.listObjects(listObjects);

        s3.close();
        return ResponseEntity.ok(res.contents().stream()
                .map(file -> new File(file.key()))
                .collect(Collectors.toList()));
    }

    private S3Client buildS3Client(String accessKey, String secretKey, Region region){
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                    accessKey,
                    secretKey);
            return S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
}
