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

### Changed
* [\#6](https://github.com/Finschia/finschia-kt/pull/6) Rename package 'ln.v2' -> 'sdk'

### Deprecated

### Removed

### Fixed

### Security

### Build, CI

* (build) [\#4](https://github.com/Finschia/finschia-kt/pull/4) Add gradle `updateSubmodule` task and `checkoutSubModule` task to run `git submodule update --init --remote` with specific version before `build task`
* (ci) [\#9](https://github.com/Finschia/finschia-kt/pull/9) Add ci/cd to publish to maven

### Document Updates

<!-- Release links -->
[Unreleased]: https://github.com/Finschia/finschia-kt/compare/8aa2005...HEAD
