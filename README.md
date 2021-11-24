# qsc-spring-feeder

## Getting started

- Download icecat data

```
mkdir -p src/main/resources/jsons/ignore-by-git
curl https://querqy.org/datasets/icecat/icecat-products-150k-20200809.tar.gz > src/main/resources/jsons/ignore-by-git/icecat-products-150k-20200809.tar.gz

```

- Example configuration

1) To read icecat data (icecat data is not qsc type, so we have to parse
   it before pushing to Quasiris Search Cloud)

```
export URL=http://localhost:8083/api/v1/data/bulk/qsc/tenant-code/feeding-code
export X_QSC_TOKEN=sometoken1234;
BATCH_SIZE=500;
export FILE_PATH=jsons/ignore-by-git/path-to-icecat.json
```

2) To read already parsed data (uncomment appropriate line of code)

```
// docs = QscFeedingUtils.readDocumentsFromFile(new ClassPathResource(filePath).getFile());
```

```
export URL=http://localhost:8083/api/v1/data/bulk/qsc/tenant-code/feeding-code
export X_QSC_TOKEN=sometoken1234;
BATCH_SIZE=500;
export FILE_PATH=jsons/example.json
```

3) To read all files from directory (uncomment appropriate line in `QscSpringFeederApplication`)

```
// docs = QscFeedingUtils.readDocumentsFromDirectory(Path.of(directory));
```

```
export URL=http://localhost:8083/api/v1/data/bulk/qsc/tenant-code/feeding-code
export X_QSC_TOKEN=sometoken1234;
BATCH_SIZE=500;
export DIRECTORY=src/main/resources/jsons/ignore-by-git/custom
```

4) If error occurs during pushing data you will have ability to retry configurable amount of times. And after that
application will write remain items to a report file. You can continue after that using `CONTINUE=true` flag. There is
example of continue configuration

```
export URL=http://localhost:8083/api/v1/data/bulk/qsc/tenant-code/feeding-code
export X_QSC_TOKEN=sometoken1234;
export BATCH_SIZE=500;
export CONTINUE=true;
export DIRECTORY=src/main/resources/jsons/ignore-by-git/custom;
```

Feel free to customize all logic for your needs.