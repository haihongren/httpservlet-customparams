# httpServlet-customParams
Custom Instrumentation for extracting HTTP request headers and parameters and inserting them as New Relic custom parameters. Uses javax.http.httpservlet as the base class to act upon.

### Installation / Usage
* Drop the extension jar in the newrelic agent's "extensions" folder.
* Edit the newrelic agent's configuration file (`newrelic.yml`) and add the following properties as applicable to the `common` stanza:

  * To collect custom requestheaders:
    `custom_request_header_names: myheader1, myheader2`
  * To collect custom request parameters:
    `custom_request_parameter_names: myparam1, myparam2, myparam3`

#### Notes
  * To collect the entire Request URL with query parameters, set `custom_request_header_names:` to `URL`.
  * Ensure the indentation is exactly 2 spaces, and within the `common` stanza. A safe bet is to find the `app_name` property and place the properties under that, matching its indentation.
  * If you don't need any of the three categories, leave out or comment out that config line.

### Results
The instrumentation will add the extracted headers and/or parameters as custom transaction parameters, which are found in 2 places:
1. APM Transaction Traces in the "Transaction Attributes" section.
2. Transaction events in Insights.
