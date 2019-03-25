# Spring Boot with Embeded OSGI
This is an example Spring Boot project which has embeded Felix OSGI framework. Other projects are API (interface and model classes) and its implementations. 

The Spring Boot application exposes these packages as the OSGI framework's extra packages (to be able to use the exposed services).

See the stackoverflow's post on [how to resolve the classloader issue for embeded OSGI framework](https://stackoverflow.com/questions/15270044/consuming-services-from-embedded-osgi-framework).

## Logging Solution in Embeded OSGI 
Added logging solution described [here](https://www.io7m.com/documents/brutal-felix-logging/).
