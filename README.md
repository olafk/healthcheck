# Healthcheck

This is a sample implementation of various healthcheck modules.

At the beginning, most attention has been given to implement the backend, and there's a very simplistic frontend

This project currently consists of:

* Commerce Healthcheck adapters
    * An adapter from commerce's `CommerceHealthHttpStatus` to the Healthcheck interface 
* Demo System relaxed security healthchecks
    * This refactors [Olaf's demo-checklist](https://github.com/olafk/demo-checklist-web) to the Healthcheck interface, implementing relaxed security checks that are suitable for demo systems. 
* Breaking Changes implementations
    * e.g. with some copies from Liferay's portal-impl code (e.g. VerifyProperties, which currently runs only during upgrades)
    * e.g. with a simulated "Have you implemented a deprecated service that's no longer supported?" alert
* Best Practice implementations
    * e.g. "Do not use default user accounts & passwords"
* Operational implementations    
    * e.g. checks for available memory and redirection configuration

## How to build

Clone this repository into a Liferay Workspace's `modules/healthcheck` directory.

Created with `liferay.workspace.product=dxp-7.4-u18`, last run with U65.

## Reference

* [LPS-151937](https://issues.liferay.com/browse/LPS-151937)

## Limitations

* Very basic permission checking (Commerce Health Checks implement this in a better way, for example in CommerceHealthStatusDisplayContext). Health Check UI is only available for Company Administrators.
* UI is veeeeery barebones right now 
* Only few native health checks implemented: Contribute more, in code or just ideas
* Assumes it only runs on System level (e.g. first virtual instance) and UI is not available on secondary instances. Even though implemented checks might be instance specific...
* Speaking of instances: Not much thought has been given to scenarios with multiple instances that might have different - instance specific - configuration.

## Ideas for the UI

The UI is - explicitly - _very_ ugly. To make its ugliness even more explicit, it's minimally interactive and 100% built with `<table>`.

You can ignore some healthchecks (successful or unsuccessful). They'll still be executed, but the result will not be shown.
 
Collecting a few ideas that could go into UI features:

* Run healthchecks in background (scheduled) - this enables long running processes to run as well
* Filter/Sort results by category/result
* Selectively run individual filters or categories (especially when long running checks are implemented, that are meant to run in the background)
