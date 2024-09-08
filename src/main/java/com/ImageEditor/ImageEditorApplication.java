package com.ImageEditor;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
//import com.drew.imaging.ImageMetadataWriter;
import com.drew.metadata.exif.GpsDirectory;

import com.thebuzzmedia.exiftool.ExifTool;
import com.thebuzzmedia.exiftool.ExifToolBuilder;
import com.thebuzzmedia.exiftool.core.UnspecifiedTag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@SpringBootApplication
public class ImageEditorApplication {

	public static void main(String[] args) {

		SpringApplication.run(ImageEditorApplication.class, args);

//		if (args.length < 2) {
//			System.out.println("Usage: java GeoLocationEditor <image-file> <latitude> <longitude> <altitude>");
//			return;
//		}

//		String imagePath = "args[0]";
//		imagePath = "C:\\Users\\kushw\\Downloads\\IMG_20200210_223312 - Copy.jpg";
//		double latitude = Double.parseDouble("0");
//		double longitude = Double.parseDouble("0");
//		double altitude = Double.parseDouble("0");
//
//		try {
//			File imageFile = new File(imagePath);
//			modifyGeoLocation(imageFile, latitude, longitude, imagePath);
//			System.out.println("Geolocation metadata updated successfully.");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
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

		//ImageMetadataWriter.writeMetadata(imageFile, metadata);

	}


	public static void modifyGeoLocation(File imageFile, double latitude, double longitude, String imagePath) throws Exception {
		// Read existing metadata

		ExifTool tool = new ExifToolBuilder().build();

		Map<com.thebuzzmedia.exiftool.Tag, String> imageData = tool.getImageMeta(imageFile);


		com.drew.metadata.Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
		GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

		if (gpsDirectory == null) {
			System.out.println("No GPS directory found in the image.");
		}

		try {
			// Format GPS coordinates as required by ExifTool
			String latRef = latitude >= 0 ? "N" : "S";
			String lonRef = longitude >= 0 ? "E" : "W";
			String lat = convertToExifFormat(latitude);
			String lon = convertToExifFormat(longitude);

			lat = "12°58'35.8";
			lon = "77°42'53.7";

			imagePath = "C:\\Users\\kushw\\Downloads\\IMG_20200210_223312.jpg";

			// Construct the ExifTool command
			String command = String.format(
					"exiftool -P -GPSLatitude=%s -GPSLatitudeRef=%s -GPSLongitude=%s -GPSLongitudeRef=%s %s",
					lat, latRef, lon, lonRef, imagePath
			);

			System.out.println("cmd: " + command);

			// Execute the command
			Process process = Runtime.getRuntime().exec(command);

			// Capture output from ExifTool
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			process.waitFor();

			System.out.println("Geolocation data added successfully.");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
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
