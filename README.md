# couchdb-java-client
couchdb java client menggunakan java [wrapper](https://en.wikipedia.org/wiki/Wrapper_function) terhadap [curl](https://curl.se/) 

## requirement 
1. Java SDK 8+
1. [Apache Ant](https://ant.apache.org/) 
1. [curl](https://curl.se/download.html) 
1. [JSON-Java](https://github.com/stleary/JSON-java) diletakkan didalam folder lib (sudah include)
1. [Apache Couchdb](https://couchdb.apache.org/)
1. [Project Lombok](https://projectlombok.org/) diletakkan didalam folder lib (sudah include)

## konfigurasi 
* pastikan curl telah diset pada environment variable

## building
* clone project
* ant all
* jar dapat didapatkan di folder dist
* library yang dibutuhkan sudah ada di folder lib 

## testing 
* masuk ke folder test 
* jalankan apache couchdb 
* buat database dengan nama shared
* buat username / password dengan nilai admin / admin123
* jalankan test.bat atau test.sh

## menggunakan library ini ke dalam project anda 
* copy couchdb-java-client.jar dari folder dist 
* copy semua .jar pada folder lib 
* import library 
```java
import org.json.*;
import java.util.logging.*;
import radityopw.couchdbclient.*;
```
* inisiasi config
```java
CouchdbClientConfig config = new CouchdbClientConfig();
config.setDatabase("nama database");
```
atau
```java
CouchdbClientConfig config = new CouchdbClientConfig();
config.setDatabase("nama database");
config.setCurl("c:\\folder_lokasi_curl\\curl.exe");
config.setProtocol("http");
config.setHost("127.0.0.1");
config.setPort("5984");
config.setUsername("admin");
config.setPassword("admin123");
```
* membuat objek couchdbclient 
```java
CouchdbClient client = new CouchdbClient(config);
```
* memanggil [method](https://radityopw.github.io/couchdb-java-client/radityopw/couchdbclient/CouchdbClient.html) yang tersedia