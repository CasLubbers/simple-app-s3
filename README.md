# Run simple app s3
This app runs a simple spring-boot which makes a /files endpoint that list files in a s3 bucket
## Run via maven
Set the values in application.properties for
* aws.access.key: access key from your AWS account
* aws.secret.key: secret key from your AWS account
* s3.bucket.name: bucket name to list the files from

`mvn spring-boot:run`

Access the application via localhost:8080/files
## Run via Docker
Build the docker image with:
`docker build . -t simple-app-s3:0.1.0`

After building the image, run it with:
`docker run -p 8080:8080 --env AWS_ACCESS_KEY="aws-access-key" --env AWS_SECRET_KEY="aws-secret-key" --env S3_BUCKET_NAME="s3-bucket-name" simple-app-s3:0.1.0`

Access the application via localhost:8080/files

## Run in kubernetes with Helm
Create aws-credentials secret with your access-key and secret-key
`kubectl create secret generic aws-credentials --from-literal=aws-access-key=supersecret --from-literal=aws-secret-key=topsecret`

Make a values.yml to edit the helm configuration. Don't forget to set the correct bucketname in the values.yml file
`helm install simple-app ./simple-app-s3 -f values.yml`

Port-forward the service created by the helm chart.
Now access the application via localhost:8080/files
## Error running native graalvm with springboot image
Tried building a native graalvm image with springboot. But when I accessed the /files endpoint I got the following error:
java.lang.ClassNotFoundException: software.amazon.awssdk.transfer.s3.internal.ApplyUserAgentInterceptor. I guess I missed some dependencies for AWS s3 api, but couldn't find it.

Native graalvm can be build following the guide on: https://www.baeldung.com/spring-native-intro


# Running Otomi on minikube and exposing the simple app application
Followed the guide on: https://github.com/redkubes/quickstart/tree/main/minikube
The metrics-server was stuck on a crashloopbackoff with a certificate error. So I allowed insecure connections with `--kubelet-insecure-tls`.
After that Otomi would continue to deploy.

I installed the simple-app-s3 with the helm command above. Then proceeded to create a service in otomi for the simple-app-s3

From the drone output I saw:
* Cert-manager created certificate resources for the host
* Ingress object with the Nginx class was edited to add the new host pointing towards a public Istio service
* Istio virtual service was created to route final destination, the service that was created from the helmchart

