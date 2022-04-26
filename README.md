# Healthcheck

This is a sample implementation of various healthcheck modules.

At the beginning, most attention has been given to implement the backend, and there's a very simplistic frontend

This project currently consists of:

* Commerce Healthcheck adapters
    * This adapts commerce's `CommerceHealthHttpStatus` to the Healthcheck interface 
* Demo Checklist adapters
    * This adapts Olaf's demo-checklist to the Healthcheck interface 
* Breaking Changes implementations
    * with some copies from Liferay's portal-impl code (VerifyProperties, which currently runs only during upgrades)

## How to build

Clone this repository into a Liferay Workspace's `modules` directory.

Created with `liferay.workspace.product=dxp-7.4-u18`

## Reference

* [LPS-151937](https://issues.liferay.com/browse/LPS-151937)
