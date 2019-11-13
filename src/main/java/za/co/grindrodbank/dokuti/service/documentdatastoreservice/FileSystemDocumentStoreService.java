/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.service.documentdatastoreservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemDocumentStoreService implements DocumentDataStoreService {

	private final Path rootLocation;

	@Autowired
	public FileSystemDocumentStoreService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	public void store(MultipartFile file, UUID documentUUID, UUID versionUUID) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		Path pathToStoredFile = this.rootLocation.resolve(documentUUID.toString() + "/" + versionUUID.toString());
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				// This is a security check
				throw new StorageException(
						"Cannot store file with relative path outside current directory " + filename);
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.createDirectories(pathToStoredFile.getParent());
				Files.copy(inputStream, pathToStoredFile, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new StorageException("Failed to store file " + filename, e);
		}
	}

    @Override
    public void store(InputStream inputStream, UUID documentUUID, UUID versionUUID) {
        Path pathToStoredFile = this.rootLocation.resolve(documentUUID.toString() + "/" + versionUUID.toString());
        try {
            Files.createDirectories(pathToStoredFile.getParent());
            Files.copy(inputStream, pathToStoredFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store inputStream ");
        }
    }
	
	
	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
					.map(this.rootLocation::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(UUID documentUUID, UUID versionUUID) {
		try {
			
			Path pathToStoredFile = this.rootLocation.resolve(documentUUID.toString() + "/" + versionUUID.toString());
			Resource resource = new UrlResource(pathToStoredFile.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException("Could not read file: " + versionUUID.toString());

			}
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + versionUUID.toString(), e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}


}
