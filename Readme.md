# httpServlet-customParams
Custom Instrumentation for extracting HTTP request headers and parameters and inserting them as New Relic custom parameters. Uses javax.http.httpservlet as the base class to act upon.

### Installation / Usage
  1. Drop the extension jar in the newrelic agent's "extensions" folder.
  2. Edit the newrelic agent's configuration file (`newrelic.yml`) and add the following properties as applicable to the `common` stanza:

    * To collect custom requestheaders:
    `custom_request_header_names: myheader1, myheader2`
    * To collect custom request parameters:
    `custom_request_parameter_names: myparam1, myparam2, myparam3`

  3. Java extensions are typically picked up on-the-fly. If wishing to use that ('hot deploy'), wait a minute or so and then check the logs to see that the extension loaded. 
  4. If you prefer a cold deploy or it doesn't work right with a hot deploy, restart your JVM after adding the JAR and configurations.
  5. Check your [results](#results)!

#### `newrelic.yml` Config Notes
  * To collect the entire Request URL with query parameters, set `custom_request_header_names:` to `URL`.
  * Ensure the indentation is exactly 2 spaces, and within the `common` stanza. A safe bet is to find the `app_name` property and place the properties under that, matching its indentation.
  * If you don't need any of the three categories, leave out or comment out that config line.

### Results
The instrumentation will add the extracted headers and/or parameters as custom transaction parameters, which are found in 2 places:
  * APM Transaction Traces in the "Transaction Attributes" section
  * Transaction events in Insights

### Troubleshooting
  * Set log level to "FINER" in newrelic.yml to capture more detailed info about the extension's attempts. This can be done on-the-fly, and changed back to "INFO" once you have the log entries you need.
