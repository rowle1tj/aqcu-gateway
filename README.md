# aqcu-gateway
Aquarius Customization - Gateway

This service sits between the outside world and the back end services of AQCU. It is build upon the Spring Cloud/Netflix platform.

It is built as a Docker Container

Configured functionality includes:

- **Swagger Api Documentation** Located at https://localhost:8443/aqcu-gateway/swagger-iu.html
- **Hystrix Dashboard** Located at https://localhost:8443/aqcu-gateway/hystrix/monitor?stream=https%3A%2F%2Flocalhost%3A8443%2Faqcu-gateway%2Fhystrix.stream%20
- **CIDA Auth** token forwarding
- **Leagcy Endpoint Mapping** Each major endpoint has it's own mapping and timeout configuration

## Migrating to new Services
The filter CustomZuulFilter is designed so that the application can transition from delivering data via aqcu-webservice to using the service per report model. It will intercept proxies to serviceId's configured with the new pattern and route them correctly. The built in Zuul behavior will not change the URI of a request (beyond stripping the prefix). This filter will change the URI to be the same as the serviceId. Only serviceIds mathing the regex "^aqcu-.*/.*$" will be intercepted. For example, serviceId "timeseriessummaryReport" will route via default Zuul logic, while serviceId "aqcu-tss-report/timeseriessummary" will route via this custom logic.

## Running the Application

This application can be run locally using the docker container built during the build process or by directly building and running the application JAR. The included `docker-compose` file has 3 profiles to choose from when running the application locally:

1. aqcu-gateway: This is the default profile which runs the application as it would be in our cloud environment. This is not recommended for local development as it makes configuring connections to other services running locally on your machine more difficult.
2. aqcu-gateway-local-dev: This is the profile which runs the application as it would be in the aqcu-local-dev project, and is configured to make it easy to replace the aqcu-gateway instance in the local-dev project with this instance. It is run the same as the `aqcu-gateway` profile, except it uses the docker host network driver.
3. aqcu-gateway-debug: This is the profile whichi runs the application exactly the same as `aqcu-gateway-local-dev` but also enables remote debugging for the application and opens up port 8000 into the container for that purpose.

Before any of these options are able to be run you must also generate certificates for this application to serve using the `create_certificates` script in the `docker/certificates` directory. Additionally, this service must be able to connect to a running instance of Water Auth when starting, and it is recommended that you use the Water Auth instance from the `aqcu-local-dev` project to accomplish this. In order for this application to communicate with any downstream services that it must call, including Water Auth, you must also place the certificates that are being served by those services into the `docker/certificates/import_certs` directory to be imported into the Java TrustStore of the running container.

To build and run the application after completing the above steps you can run: `docker-compose up --build {profile}`, replacing `{profile}` with one of the options listed above.