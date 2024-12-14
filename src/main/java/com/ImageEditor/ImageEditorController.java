package com.ImageEditor;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;


import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;


@Slf4j
@RestController
public class ImageEditorController {


    private String failedImages= "failedImages: ";

    private static HashSet<String> noGeoLocationSetImages = new HashSet<>();


    @PostMapping("/updateImageLocation")
    public String home(@RequestBody RequestObject requestObject) {

        log.info("Request Object received: {}", requestObject);
        failedImages= "failedImages: ";
        noGeoLocationSetImages = new HashSet<>();

        try {
            AtomicInteger failed = new AtomicInteger(0);
            AtomicInteger success = new AtomicInteger(0);
            requestObject.getFileImageName().forEach(fileName -> {
                int count = success.get() + failed.get() + 1;
                log.info("Processing for file no: {} {}", count, fileName);
                MDC.put("filename", fileName);
//                String imagePath = "G:\\Realme\\DCIM\\Camera\\" + fileName;
                String imagePath = "C:\\Users\\kushw\\Downloads\\Photos\\" + fileName;
                log.info("Image path: {}", imagePath);
                File imageFile = new File(imagePath);
                try {
                    modifyGeoLocation(imageFile, requestObject, imagePath, fileName);
                    success.getAndIncrement();
                } catch (Exception e) {
                    log.error("Exception occurred while modify data ", e);
                    failedImages = failedImages + fileName;
                    failed.incrementAndGet();
                }
                MDC.remove("filename");
            });
            log.info("Total successful: {}  failed: {}", success.get(), failed.get());
        } catch (Exception e) {
            log.error("Exception occurred while modify data", e);
        }
        log.error("Not Setting Images: {}", noGeoLocationSetImages);

        return failedImages;
    }


    public static void modifyGeoLocation(File imageFile, RequestObject requestObject, String imagePath, String fileName) throws Exception {
        // Read existing metadata

        Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
        GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

        if (gpsDirectory == null) {
            log.error("No GPS directory found in the image.");
            changeExifMetadata(imageFile, fileName, requestObject, imagePath);
        } else {
            log.info("GPS Directory found: {}", gpsDirectory);
            noGeoLocationSetImages.add(fileName);
            changeExifMetadata(imageFile, fileName, requestObject, imagePath);
        }

        //exiftool(imageFile, requestObject, imagePath);


    }

    public static void changeExifMetadata( File jpegImageFile, String fileName , RequestObject requestObject, String imagePath) throws IOException {
            TiffOutputSet outputSet = null;
             String destinationImagePath = "G:\\Realme\\DCIMNew\\Camera\\" + fileName;

             destinationImagePath = "C:\\Users\\kushw\\Downloads\\Photos\\final\\" + fileName;

            // note that metadata might be null if no metadata is found.
            final ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            if (null != jpegMetadata) {
                // note that exif might be null if no Exif metadata is found.
                final TiffImageMetadata exif = jpegMetadata.getExif();

                if (null != exif) {
                    outputSet = exif.getOutputSet();
                }
            }

            if (null == outputSet) {
                outputSet = new TiffOutputSet();
            }

            {
                // Example of how to add/update GPS info to output set.
                final double longitude = Double.parseDouble(requestObject.getLongitude()); // 74 degrees W (in Degrees East)
                final double latitude = Double.parseDouble(requestObject.getLatitude()); // 40 degrees N (in Degrees
                outputSet.setGpsInDegrees(longitude, latitude);
            }

        try (FileOutputStream fos = new FileOutputStream(destinationImagePath);
             OutputStream os = new BufferedOutputStream(fos)) {

            new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os, outputSet);
            log.info("Geolocation data added successfully {}. Thread sleeping for 50ms", imagePath);
        }
    }


    private static void exiftool(File imageFile, RequestObject requestObject, String imagePath) {
        try {
            // Format GPS coordinates as required by ExifTool
            String latRef = Double.parseDouble(requestObject.getLatitude()) >= 0 ? "N" : "S";
            String lonRef = Double.parseDouble(requestObject.getLongitude()) >= 0 ? "E" : "W";
            String lat = requestObject.getLatitude();
            String lon = requestObject.getLongitude();


            // Construct the ExifTool command
            String command = String.format(
                    "exiftool -P -GPSLatitude=%s -GPSLatitudeRef=%s -GPSLongitude=%s -GPSLongitudeRef=%s %s",
                    lat, latRef, lon, lonRef, imagePath
            );
            log.info("Executing command: {}", command);
            // Execute the command
            Process process = Runtime.getRuntime().exec(command);

            // Capture output from ExifTool
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();
            log.info("Geolocation data added successfully {}. Thread sleeping for 50ms", imageFile);
            Thread.sleep(50);
        } catch (IOException | InterruptedException e) {
            log.error("Exception occurred while modify data", e);
        }
    }


    private static String convertToExifFormat(double coordinate) {
        int degrees = (int) coordinate;
        double fractional = Math.abs(coordinate - degrees) * 60;
        int minutes = (int) fractional;
        double seconds = (fractional - minutes) * 60;
        return degrees + "/1," + minutes + "/1," + (int) seconds + "/1";
    }
}
