# Healthcheck

This is a sample implementation of various healthcheck modules.

At the beginning, most attention has been given to implement the backend, and there's a very simplistic frontend

This project currently consists of:

* Commerce Healthcheck adapters
    * This adapts commerce's `CommerceHealthHttpStatus` to the Healthcheck interface 
* Demo Checklist adapters
    * This adapts [Olaf's demo-checklist](https://github.com/olafk/demo-checklist-web) to the Healthcheck interface 
* Breaking Changes implementations
    * with some copies from Liferay's portal-impl code (VerifyProperties, which currently runs only during upgrades)
* Best Practice implementations
    * e.g. "Do not use default user accounts"
* Operational implementations
    * e.g. checks for available memory and redirection configuration
	
## How to build

Clone this repository into a Liferay Workspace's `modules` directory.

Created with `liferay.workspace.product=dxp-7.4-u18`

## Reference

* [LPS-151937](https://issues.liferay.com/browse/LPS-151937)

## Limitations

* Very basic permission checking (Commerce Health Checks implement this in a better way, for example in CommerceHealthStatusDisplayContext). Health Check UI is only available for Company Administrators.
* UI is veeeeery barebones right now 
* Only few native health checks implemented: Contribute more, in code or just ideas
* Assumes it only runs on System level (e.g. first virtual instance) and UI is not available on secondary instances. Even though implemented checks might be instance specific...