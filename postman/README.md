# Running Postman tests

Dokuti can be spun up in several ways for testing purposes. The most common is using `docker-compose`. A `Dokuti.postman_collection.json` Postman collection has
been provided within the `postman` folder. The below all-caps variables can be set as Postman environment variables or in the Postman collection directly.

## Running with newman

Note: Dokuti.postman_collection.json require some test files with names: test-file.txt,test-file2.txt,test-file3.txt. 
All files MUST HAVE different content. It is important for testing checksum.
You have to create them manually or use scripts below:

```
echo "test-file.txt" > test-file.txt
echo "test-file2.txt" > test-file2.txt
echo "test-file3.txt" > test-file3.txt

newman run  Dokuti.postman_collection.json -e Dokuti-Quickstart.postman_environment.json
```

Note: The `Dokuti-Quickstart.postman.environment.json` file is a Postman environment intended for use with the `docker-compose` environment (It sets all the required environment variables when used).