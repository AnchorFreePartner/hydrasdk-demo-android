# Versions

## 0.21.3 - 9 Nov 2017

- Updated config to fix SA block

## 0.21.4 - 9 Nov 2017

- UCR fixes (for crashes reported by partners)

## 0.21.6 - 15 Nov 2017

- UCR fixes (for crashes reported by partners)
- Added API for reporting cause of start/stop connection into analytics

## 0.21.7 - 16 Nov 2017

- UMSA-156
- UMSA-152

## 0.21.8 - 17 Nov 2017

- Fixed restoring vpn connection after coming back from airplane mode (issue on some android versions)

## 0.21.9 - 20 Nov 2017

- Fixed SocketTimeout for HydraSDK api calls after establishing vpn connection

## 0.22.1 - 22 Nov 2017

- Fixes in internal sdk analytics

## 0.22.2 - 23 Nov 2017

- Fixed network loss and restore issues
- Fixed restore connection when task was removed and aiplane mode toggled

## 0.22.3 - 4 Dec 2017

- Removed adb logs

## 0.22.4 - 6 Dec 2017

- Social field in Subscriber

## 0.22.5 - 6 Dec 2017

- Revised error code in UCR analytics

## 0.22.7 - 12 Dec 2017

- Handle disabled user
- Handle hydra connection lost errors with its reconnection

## 0.22.8 - 18 Dec 2017

- UCR fixes
- Crash fixes
- Handle creds after country change

## 0.22.9 - 21 Dec 2017

- Fixed 2 rare crashes in UCR

## 0.23.0 - 23 Dec 2017

- Fixed crashes in UCR
- Removed x86 support with AbiNotSupportedException

## 0.23.2 - 9 Jan 2018

- Removed requirement to call HydraSDK.init on each process if sdk is in multiprocess mode

## 0.23.5 - 16 Jan 2018

- fixed restore after kill main process while vpn is connected

## 0.23.6 - 17 Jan 2018

- updated ucr lib
- ability to change global analytics prop

## 0.23.7 - 5 Feb 2018

- fix abi check
- updated vpn module version

## 0.23.8 - 8 Feb 2018

- updated vpn module to 0.7.6
- updated ucr module to 0.6.5-beta

## 0.23.9 - 8 Feb 2018

- send VpnException TrafficExceed on vpn Start

## 0.24.0 - 16 Feb 2018

- updated hydra with armv7 support
- method to get device id

## 0.24.1 - 21 Feb 2018

- added debug info method to sdk to collect information abot sdk issue

## 0.24.2 - 28 Feb 2018

- Changes in device id hashing
