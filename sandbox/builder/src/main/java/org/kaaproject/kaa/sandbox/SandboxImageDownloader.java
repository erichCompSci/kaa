package org.kaaproject.kaa.sandbox;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SandboxImageDownloader {


    private static final Logger LOG = LoggerFactory.getLogger(AbstractSandboxBuilder.class);
    private static final String MD5 = "MD5";
    private static final String MD5_FILE_EXT = ".md5";
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 64;

    public static void downloadFile(URL sourceUrl, File targetFile) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext context = new BasicHttpContext();
        if (targetFile.exists()) {
            String checksumFileUrl = sourceUrl.toString() + MD5_FILE_EXT;
            String md5sum = downloadCheckSumFile(httpClient, context, checksumFileUrl);
            boolean result = compareChecksum(targetFile, md5sum);
            LOG.debug("Compare checksum result is {}", result);
            if (!result) {
                targetFile.delete();
                downloadFile(httpClient, context, sourceUrl, targetFile);
            }
        } else {
            downloadFile(httpClient, context, sourceUrl, targetFile);
        }
    }

    public static void downloadFile(HttpClient httpClient, HttpContext context, URL sourceUrl, File targetFile) throws Exception {
        LOG.debug("Download {} to file {}", sourceUrl.toString(), targetFile.getAbsolutePath());
        HttpEntity entity = null;
        try {
            HttpGet httpGet = new HttpGet(sourceUrl.toURI());
            HttpResponse response = httpClient.execute(httpGet, context);
            entity = response.getEntity();
            long length = entity.getContentLength();
            InputStream in = new BufferedInputStream(entity.getContent());
            OutputStream out = new BufferedOutputStream(new FileOutputStream(targetFile));
            copyLarge(in, out, new byte[DEFAULT_BUFFER_SIZE], length);
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        } finally {
            EntityUtils.consumeQuietly(entity);
        }
    }

    private static long copyLarge(InputStream input, OutputStream output, byte[] buffer, long length)
            throws IOException {
        long count = 0;
        int n = 0;
        LOG.info("0%");
        int loggedPercents = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
            int percentsComplete = (int)((double)count/(double)length*100f);
            if (percentsComplete-loggedPercents>=10) {
                LOG.info(percentsComplete+"%");
                loggedPercents = percentsComplete;
            }
        }
        return count;
    }


    private static boolean compareChecksum(File targetFile, String downloadedCheckSum) {
        String checkSum = "";
        try (FileInputStream fileInput = new FileInputStream(targetFile)) {
            MessageDigest messageDigest = MessageDigest.getInstance(MD5);
            byte[] dataBytes = new byte[1024 * 100];
            int bytesRead = 0;
            while ((bytesRead = fileInput.read(dataBytes)) != -1) {
                messageDigest.update(dataBytes, 0, bytesRead);
            }
            byte[] digestBytes = messageDigest.digest();
            checkSum = DatatypeConverter.printHexBinary(digestBytes);
            LOG.debug("Calculated checksum for filer[{}]: [{}], downloaded is [{}]", targetFile.getAbsolutePath(), checkSum, downloadedCheckSum);
        } catch (IOException | NoSuchAlgorithmException e) {
            LOG.debug("Can't calculate checksum for file [{}]", targetFile.getAbsolutePath());
        }
        return checkSum.equalsIgnoreCase(downloadedCheckSum);
    }

    public static String downloadCheckSumFile(HttpClient httpClient, HttpContext context, String url) throws Exception {
        LOG.debug("Starting download [{}] ...", url);
        HttpGet httpGet = new HttpGet(URI.create(url));
        HttpResponse response = httpClient.execute(httpGet, context);
        HttpEntity entity = response.getEntity();
        InputStream in = entity.getContent();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(in, out);
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
        String checkSum = new String(out.toByteArray(), StandardCharsets.UTF_8);
        return checkSum != null ? checkSum.trim() : "";
    }

}
