package org.kaaproject.kaa.sandbox.demo.projects;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

public class DockerfileRestGenerator extends RestTemplate {


    private static final String REST_DOCKERFILE_PART = "rest_docker_part";

    @Override
    public <T> T postForObject(URI url, Object request, Class<T> responseType) throws RestClientException {
        final String dockerRestUrl = "RUN curl -v -S -u kaa:kaa123 -X POST "+url + "| python -mjson.tool";
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(REST_DOCKERFILE_PART, true)))) {
            out.println(dockerRestUrl);
        }catch (IOException e) {
            logger.debug("failed to write to file");
        }

        return null;
    }


    @Override
    public <T> T getForObject(URI url, Class<T> responseType) throws RestClientException {

        return null;
    }

}
