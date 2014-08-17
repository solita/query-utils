# A layer on top of JPA2 to make querying a database even more complex ;)

## Motivation

Pure SQL is not enough. Some layer on top of it is needed.  
Pick your favorite library to see how often simple, common needs are awkward or even difficult to express.  
JPA2 (Java Persistence API) standard with its Criteria API is probably the most horrifying of them all.

This library is a thin wrapper on top of JPA2 to make some things easier to express. It doesn't strive to answer 100% of user needs, please fall back to regular JPA2 or the API of your persistence provider when needed.


## The holy trinity

Separation of concerns is often a good thing. This library separated query processing to three distinct parts:

### Query generation

Query generation should be a separate step before execution. This way one can execute the same query "different ways", for example with different orderings, different pagination, or different projections.

### Query execution

Query execution should not modify the query-object so that it can be reused later. Execution should be strongly typed, so that the result type(s) can be seen in the type of the query. This applies also to native queries. Tuples can be used to return multiple values.

### Query result projection

Many times a query is constructed so that it targets whole rows in a table, but often only a single column (possibly the ID) is actually needed. Loading the whole rows would be silly (due to missing all indexes), but fixing the projection into the query severely limits the reuseability of the query. Simple projections (ID, a single column, multiple columns...) should be easy to express while executing a query.

Often the need is to get a "tree of data" with a single rowset (located through a possibly complex query) as the root of the tree. The operation can perform multiple queries under the covers, but the amount must not depend on the amount of data. It should be possible to somehow declaratively express what kind of tree is needed from the database. This library tries to answer this need by generating certain kind of meta-constructors for chosen classes, which accept JPA2 meta-attributes as arguments.


## Installing

Put the following file (or the latest version) to classpath and enable annotation processing in your build tool:

https://github.com/solita/query-utils/releases/download/0.8/query-utils-0.8.jar


### Eclipse (tested in Juno)

Add the jars to project dependencies.

Project properties -> Java Compiler -> Annotation Processing:
<ul>
  <li>Enable project specific settings</li>
	<li>Enable annotation processing</li>
</ul>

Project properties -> Java Compiler -> Annotation Processing -> Factory Path:
<ul>
	<li>Enable project specific settings</li>
	<li>Add JARs... and select hibernate-jpa-2.0-api jar</li>
	<li>Add JARs... and select functional-utils jar</li>
	<li>Add JARs... and select meta-utils jar</li>
	<li>Add JARs... and select query-utils jar</li>
</ul>

Project properties -> Java Compiler -> Annotation Processing -> Processor options:
<ul>
	<li>Indicate somehow (e.g. JpaConstructorProcessor.includesAnnotation or JpaMetamodelProcessor.includesRegex) which classes to include for meta-constructor generation.</li>
</ul>

Now whenever you save a file the metaclasses are automatically generated and immediately ready for use.

### IntelliJ Idea

TODO: Anyone know how Idea supports Annotation Processors?

### Maven/Gradle/...

Please consult the documentation of your build tool on how to enable and control annotation processing.

## Word of warning

This package comes with no warranty what-so-ever. It's higly experimental, might contain loads of bugs and needs more testing.
Packages and classes may get renamed or moved, and things may suddenly break.
Use at your own risk!

Bug reports, feature requests and opinionated recommendations are highly welcome ;)


## License

Copyright © Solita Oy

Distributed under the MIT License.
