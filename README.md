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
1. pastikan curl telah diset pada environment variable

## building
1. clone project
1. ant all
1. jar dapat didapatkan di folder dist
1. library yang dibutuhkan sudah ada di folder lib 

## testing 
1. masuk ke folder test 
1. jalankan apache couchdb 
1. buat database dengan nama shared
1. buat username / password dengan nilai admin / admin123
1. jalankan test.bat atau test.sh

## menggunakan library ini ke dalam project anda 
1. copy couchdb-java-client.jar dari folder dist 
1. copy semua .jar pada folder lib 
1. import library 
```java
import org.json.*;
import java.util.logging.*;
import radityopw.couchdbclient.*;
```
1. inisiasi config
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
1. membuat objek couchdbclient 
```java
CouchdbClient client = new CouchdbClient(config);
```
1. memanggil [method](https://radityopw.github.io/couchdb-java-client/radityopw/couchdbclient/CouchdbClient.html) yang tersedia