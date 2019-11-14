# SAP Hybris instrumentation

Custom Instrumentation for timing SAP Hybris tasks

## Installation / Usage

1. Drop the extension jar in the newrelic agent's "extensions" folder.

## Results

The instrumentation will add the extracted headers and/or parameters as custom transaction parameters, which are found in 2 places:

- APM Transaction Traces in the "Transaction Attributes" section
- Transaction events in Insights

## Troubleshooting

- Set log level to "FINER" in newrelic.yml to capture more detailed info about the extension's attempts. This can be done on-the-fly, and changed back to "INFO" once you have the log entries you need.
