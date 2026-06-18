# Android Architecture Beyond MV* Patterns

:warning: The repository is only for technical assessment of the proposal. It
does not contain any technical patterns or solutions that can be followed or
reused. Work in progress.

## Features

* Just Clean Architecture approach without relying on MV* patterns
* Preserving the core benefits of Clean Architecture
* Reduced cognitive load when navigating large codebases
* Consistent unidirectional flow of control and data across the application
* Granular updates/rerenders of user interfaces
* Prevention of "prop drilling" / "prop walls" / logic scattered throughout
  layouts

**AI-related**

* Manageable AI-assisted development through a formalized architecture and
  development process
* Isolated contexts for both developers and AI agents
* Collective code ownership for developers and AI agents

## Proposal

The proposal is to use 
[Clean Reactive Architecture](https://github.com/clean-reactive) 
as the primary architectural approach for Android applications.

## TODO

- [ ] verify solution from technical perspective
- [ ] identify technical gap between existing framework and the architectural
      proposal (as a source for the framework improvement)
- [ ] split the code into separate files with idiomatic/consistent naming
