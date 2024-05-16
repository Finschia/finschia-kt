<!--
Guiding Principles:

Changelogs are for humans, not machines.
There should be an entry for every single version.
The same types of changes should be grouped.
Versions and sections should be linkable.
The latest version comes first.
The release date of each version is displayed.
Mention whether you follow Semantic Versioning.

Usage:

Change log entries are to be added to the Unreleased section under the
appropriate stanza (see below). Each entry should ideally include a tag and
the Github issue reference in the following format:

* (<tag>) \#<issue-number> message

The issue numbers will later be link-ified during the release process so you do
not have to worry about including a link manually, but you can if you wish.

Types of changes (Stanzas):

"Added" for new features.
"Changed" for changes in existing functionality.
"Deprecated" for soon-to-be removed features.
"Removed" for now removed features.
"Fixed" for any bug fixes.
"Security" in case of vulnerabilities.
"Features" for new features.
"Build, CI" for CI/CD
"Document Updates" for document update
Ref: https://keepachangelog.com/en/1.0.0/
-->

# Changelog

## [Unreleased]

### Added
* [\#21](https://github.com/Finschia/finschia-kt/pull/21) add MsgCreateValidator examples for single and multi-sig
* [\#22](https://github.com/Finschia/finschia-kt/pull/22) bump up finschia-proto:2.0.0
* [\#23](https://github.com/Finschia/finschia-kt/pull/23) add new example for MsgSwap&MsgTransfer Tx

### Changed
* [\#19](https://github.com/Finschia/finschia-kt/pull/19) change the value of AminoMsg to the general type

### Deprecated

### Removed

### Fixed

### Security

### Build, CI

### Document Updates

## [v0.2.2]

### Fixed
* build: [#15](https://github.com/Finschia/finschia-kt/pull/15) fix for downloading maven dependency with 'descriptor.bin error'

### Build, CI
* [\#14](https://github.com/Finschia/finschia-kt/pull/14) change groupID for maven publish. remove tx module from maven publish.
* build: [\#16](https://github.com/Finschia/finschia-kt/pull/16) change build script to use maven publications(crypto, proto) and update README


## [v0.2.1]

### Changed
* [\#6](https://github.com/Finschia/finschia-kt/pull/6) Rename package 'ln.v2' -> 'sdk'
* (ci) [\#11](https://github.com/Finschia/finschia-kt/pull/11) Add ci/cd to publish to maven

### Build, CI

* (build) [\#4](https://github.com/Finschia/finschia-kt/pull/4) Add gradle `updateSubmodule` task and `checkoutSubModule` task to run `git submodule update --init --remote` with specific version before `build task`


<!-- Release links -->
[Unreleased]: https://github.com/Finschia/finschia-kt/compare/v0.2.2...HEAD
[v0.2.2]: https://github.com/Finschia/finschia-kt/compare/v0.2.1...v0.2.2
[v0.2.1]: https://github.com/Finschia/finschia-kt/compare/8aa2005...v0.2.1
