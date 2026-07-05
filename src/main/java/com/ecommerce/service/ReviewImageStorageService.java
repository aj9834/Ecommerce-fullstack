package com.ecommerce.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ReviewImageStorageService {
	private static final Set<String> ALLOWED_EXTENSIONS =
			Set.of("jpg", "jpeg", "png", "webp", "gif");

	private final Path uploadDirectory;

	public ReviewImageStorageService(
			@Value("${app.review-upload-dir:uploads/reviews}") String uploadDirectory
	) {
		this.uploadDirectory = Paths.get(uploadDirectory).toAbsolutePath().normalize();
	}

	public String store(MultipartFile image) {
		if (image == null || image.isEmpty()) {
			return null;
		}
		if (image.getContentType() == null || !image.getContentType().startsWith("image/")) {
			throw new RuntimeException("Only image files can be uploaded");
		}

		String originalName = StringUtils.cleanPath(
				image.getOriginalFilename() == null ? "review-image" : image.getOriginalFilename()
		);
		String extension = getExtension(originalName);
		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new RuntimeException("Supported image types: JPG, PNG, WEBP, and GIF");
		}

		try {
			Files.createDirectories(uploadDirectory);
			String fileName = UUID.randomUUID() + "." + extension;
			Path destination = uploadDirectory.resolve(fileName).normalize();
			if (!destination.getParent().equals(uploadDirectory)) {
				throw new RuntimeException("Invalid image file name");
			}
			Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
			return "/uploads/reviews/" + fileName;
		} catch (IOException ex) {
			throw new RuntimeException("Could not store the review image");
		}
	}

	private String getExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
			throw new RuntimeException("The image file must have an extension");
		}
		return fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
	}
}
