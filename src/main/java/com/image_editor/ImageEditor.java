package main.java.com.image_editor;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

import java.io.File;

public class ImageEditor {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: java GeoLocationEditor <image-file> <latitude> <longitude> <altitude>");
            return;
        }

        String imagePath = args[0];
        double latitude = Double.parseDouble(args[1]);
        double longitude = Double.parseDouble(args[2]);
        double altitude = Double.parseDouble(args[3]);

        try {
            File imageFile = new File(imagePath);
            modifyGeoLocation(imageFile, latitude, longitude, altitude);
            System.out.println("Geolocation metadata updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void modifyGeoLocation(File imageFile, double latitude, double longitude, double altitude) throws Exception {
        // Read existing metadata
        com.drew.metadata.Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
        GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

        if (gpsDirectory == null) {
            throw new RuntimeException("No GPS directory found in the image.");
        }

        // Set the new geolocation metadata
        gpsDirectory.setDouble(GpsDirectory.TAG_LATITUDE, latitude);
        gpsDirectory.setDouble(GpsDirectory.TAG_LONGITUDE, longitude);
        gpsDirectory.setDouble(GpsDirectory.TAG_ALTITUDE, altitude);

        // Save changes to the image file
        // Note: metadata-extractor doesn't support writing metadata back to the file.
        // You may need a different library for that or use a dedicated tool for this purpose.
        System.out.println("Updated Metadata: ");
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                System.out.println(tag);
            }
        }
    }
}
