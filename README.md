akka-quickser-serialization - quickser-based serializers for Akka
=====================================================================

This library provides a custom quickser-based serializer for Akka. It can be used for more efficient akka actor's remoting. 

Features
--------

*   It is more efficient than Java serialization - both in size and speed
*   It can be faster than Kryo and protostuff based serializers in many cases 
*   Does not require any additional build steps like compiling proto files, when using protobuf serialization
*   Almost any Scala and Java class can be serialized using it without any additional configuration or code changes
*   Apache 2.0 license

How to use this library in your project
----------------------------------------

To use this serializer, you need to do two things:
*   Include a dependency on this library into your project:

	`libraryDependencies += "com.romix.akka" % "akka-quickser-serialization" % "0.1-SNAPSHOT"`
    
*   Add some new elements to your Akka configuration file, e.g. `application.conf`


Configuration of akka-quickser-serialization in Akka configuration file
-----------------------------------------------------------------------

The following options are available for configuring this serializer:

*   You should declare in the Akka `serializers` section a new kind of serializer:  

		serializers {  
			java = "akka.serialization.JavaSerializer"  
			# Define protostuff serializer   
			quickser = "com.romix.akka.serialization.quickser.QuickserSerializer"  
		}    
     
*   As usual, you should declare in the Akka `serialization-bindings` section which classes should use quickser serialization

*   You need to add a following line to the list of your Akka extensions:
	`extensions = ["com.romix.akka.serialization.quickser.QuickserSerializationExtension$"]`
	
*   You may need to add a new `quickser` section to the akka.actor part of configuration  

		quickser  {  
					  
			# To guarantee that both sender and receiver use the same numeric ids for the same   
			# classes it is advised to pre-register them using the "classes" section
			#    
			# Define a set of fully qualified class names for   
			# classes to be used for serialization.  
			classes = [  
				"package1.name1.className1",  
				"package2.name2.className2"  
			]  
		}

