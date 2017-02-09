# Custom Instrumentation for extracting HTTP request headers and parameters as New Relic custom parameters

Instrumention for capturing specific request or response HTTP headers and paramters (Servlet based). 


### Installation

Drop the extension jar in the newrelic agent's "extensions" folder.

Edit the newrelic agent's configuration file - newrelic.yml and add the following properties as applicable (these properties should be added at the same level as the "app_name" property).
So find the property
  &nbsp;&nbsp;app_name: New Relic Sample Application
and add these properties below it with the same level of indentation. Only add the properties that are needed.
  &nbsp;&nbsp;custom_request_header_names: myheader1, myheader2
  &nbsp;&nbsp;custom_request_parameter_names: myparam1, myparam2, myparam3

###  
The instrumentation will add the extracted headers and/or parameters as custom transaction parameters





