JSON in Java [package org.json]
===============================

[![Maven Central](https://img.shields.io/maven-central/v/org.json/json.svg)](https://mvnrepository.com/artifact/org.json/json)

JSON is a light-weight, language independent, data interchange format.
See http://www.JSON.org/

The files in this package implement JSON encoders/decoders in Java.
It also includes the capability to convert between JSON and XML, HTTP
headers, Cookies, and CDL.

This is a reference implementation. There is a large number of JSON packages
in Java. Perhaps someday the Java community will standardize on one. Until
then, choose carefully.

The license includes this restriction: "The software shall be used for good,
not evil." If your conscience cannot live with that, then choose a different
package.

The package compiles on Java 1.6-1.8.


**JSONObject.java**: The `JSONObject` can parse text from a `String` or a `JSONTokener`
to produce a map-like object. The object provides methods for manipulating its
contents, and for producing a JSON compliant object serialization.

**JSONArray.java**: The `JSONArray` can parse text from a String or a `JSONTokener`
to produce a vector-like object. The object provides methods for manipulating
its contents, and for producing a JSON compliant array serialization.

**JSONTokener.java**: The `JSONTokener` breaks a text into a sequence of individual
tokens. It can be constructed from a `String`, `Reader`, or `InputStream`.

**JSONException.java**: The `JSONException` is the standard exception type thrown
by this package.

**JSONPointer.java**: Implementation of
[JSON Pointer (RFC 6901)](https://tools.ietf.org/html/rfc6901). Supports
JSON Pointers both in the form of string representation and URI fragment
representation.

**JSONPropertyIgnore.java**: Annotation class that can be used on Java Bean getter methods.
When used on a bean method that would normally be serialized into a `JSONObject`, it
overrides the getter-to-key-name logic and forces the property to be excluded from the
resulting `JSONObject`.

**JSONPropertyName.java**: Annotation class that can be used on Java Bean getter methods.
When used on a bean method that would normally be serialized into a `JSONObject`, it
overrides the getter-to-key-name logic and uses the value of the annotation. The Bean
processor will look through the class hierarchy. This means you can use the annotation on
a base class or interface and the value of the annotation will be used even if the getter
is overridden in a child class.   

**JSONString.java**: The `JSONString` interface requires a `toJSONString` method,
allowing an object to provide its own serialization.

**JSONStringer.java**: The `JSONStringer` provides a convenient facility for
building JSON strings.

**JSONWriter.java**: The `JSONWriter` provides a convenient facility for building
JSON text through a writer.


**CDL.java**: `CDL` provides support for converting between JSON and comma
delimited lists.

**Cookie.java**: `Cookie` provides support for converting between JSON and cookies.

**CookieList.java**: `CookieList` provides support for converting between JSON and
cookie lists.

**HTTP.java**: `HTTP` provides support for converting between JSON and HTTP headers.

**HTTPTokener.java**: `HTTPTokener` extends `JSONTokener` for parsing HTTP headers.

**XML.java**: `XML` provides support for converting between JSON and XML.

**JSONML.java**: `JSONML` provides support for converting between JSONML and XML.

**XMLTokener.java**: `XMLTokener` extends `JSONTokener` for parsing XML text.

Unit tests are maintained in a separate project. Contributing developers can test
JSON-java pull requests with the code in this project:
https://github.com/stleary/JSON-Java-unit-test

Numeric types in this package comply with
[ECMA-404: The JSON Data Interchange Format](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf) and
[RFC 7159: The JavaScript Object Notation (JSON) Data Interchange Format](https://tools.ietf.org/html/rfc7159#section-6).
This package fully supports `Integer`, `Long`, and `Double` Java types. Partial support
for `BigInteger` and `BigDecimal` values in `JSONObject` and `JSONArray` objects is provided
in the form of `get()`, `opt()`, and `put()` API methods.

Although 1.6 compatibility is currently supported, it is not a project goal and may be
removed in some future release.

In compliance with RFC7159 page 10 section 9, the parser is more lax with what is valid
JSON than the Generator. For Example, the tab character (U+0009) is allowed when reading
JSON Text strings, but when output by the Generator, tab is properly converted to \t in
the string. Other instances may occur where reading invalid JSON text does not cause an
error to be generated. Malformed JSON Texts such as missing end " (quote) on strings or
invalid number formats (1.2e6.3) will cause errors as such documents can not be read
 reliably.

Release history:

~~~
20180813    POM change to include Automatic-Module-Name (#431)

20180130    Recent commits

20171018    Checkpoint for recent commits.

20170516    Roll up recent commits.

20160810    Revert code that was breaking opt*() methods.

20160807    This release contains a bug in the JSONObject.opt*() and JSONArray.opt*() methods,
it is not recommended for use.
Java 1.6 compatability fixed, JSONArray.toList() and JSONObject.toMap(),
RFC4180 compatibility, JSONPointer, some exception fixes, optional XML type conversion.
Contains the latest code as of 7 Aug, 2016

20160212    Java 1.6 compatibility, OSGi bundle. Contains the latest code as of 12 Feb, 2016.

20151123    JSONObject and JSONArray initialization with generics. Contains the
latest code as of 23 Nov, 2015.

20150729    Checkpoint for Maven central repository release. Contains the latest code
as of 29 July, 2015.
~~~


JSON-java releases can be found by searching the Maven repository for groupId "org.json"
and artifactId "json". For example:
https://search.maven.org/search?q=g:org.json%20AND%20a:json&core=gav
