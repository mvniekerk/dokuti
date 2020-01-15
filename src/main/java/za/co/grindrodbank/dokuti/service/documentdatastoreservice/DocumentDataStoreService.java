/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.service.documentdatastoreservice;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface DocumentDataStoreService {
	
	void init();

	void store(MultipartFile file, String id);
	void store(InputStream inputStream, String id);
	
    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String id);
    
    void deleteAll();

}
