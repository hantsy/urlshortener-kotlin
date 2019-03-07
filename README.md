# UrlShortener service built with Spring and Kotlin


## Prerequisites

1. Java 8
2. Apache Maven


## Get the source codes

Clone the source codes from Github.

```bash
git clone https://github.com/hantsy/urlshortener-kotlins
```

## Build 

Enter the project folder, build the project by Maven command.

```bash
mvn clean package
```

When it is done, there is a jar file in the **target** folder.


## Run

Run the application.

```bash
java -jar target/app-0.0.1-SNAPSHOT.jar 
```

## Test

It is easy to test the application by `curl`.

Generate a shorten url by sending request the root endpoint with POST method.

```bash
curl -v -X POST http://localhost:8080/ -d "{\"url\":\"test.com\"}" -H "Content-Type:application/json" -H "Accept:application/json"
Note: Unnecessary use of -X or --request, POST is already inferred.
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> POST / HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.55.1
> Content-Type:application/json
> Accept:application/json
> Content-Length: 18
>
* upload completely sent off: 18 out of 18 bytes
< HTTP/1.1 201
< Location: http://localhost:8080/t8ldiTo6nA
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Thu, 07 Mar 2019 07:23:40 GMT
<
{"id":"t8ldiTo6nA","originUrl":"test.com"}* Connection #0 to host localhost left intact

```
>NOTE: There is a **Location** header has been added in the response headers, it is easy to visit the new created resource by this url provided.

Access the single resource by generated id.

```bash
curl -v http://localhost:8080/t8ldiTo6nA
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /t8ldiTo6nA HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.55.1
> Accept: */*
>
< HTTP/1.1 308
< Location: test.com
< Content-Length: 0
< Date: Thu, 07 Mar 2019 07:23:50 GMT
<
* Connection #0 to host localhost left intact
```

When a generated id is found, then return a 308 status code. If you request this url in browser, it should redirect the location set in the header.

## How it works

1. When you send a shorten url generation request to http://localhost:8080/.
2. Internally, the `save` method of `UrlShortenerController` will handle this request, including:
	* Generate a ramdon string as id of the short url
	* Save a mapping between the short url and long url
	* Set the **Location** header(the shorten url link) to the new created resource(`RegisteredUrl`)
    * Return the saved resource into the response body.
3. Access the short url that extracted from the **Location** from response headers, it will perform the following steps:
    * Extract the id from url, and find the RegisteredUrl by id
    * If it is existed, return 308 status and set **Location** header to the origin url.
    * If it is not existed, return 404 	
