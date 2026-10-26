# ADR 0001: Initial JobHunter Architecture

- Status: Accepted
- Date: 2026-10-26

## Context

JobHunter needs to discover jobs, match them to a candidate's
skills and resume, assist with application autofill, and track
application progress.

The project should remain understandable for a small team while
allowing new job sources and matching capabilities to be added.

## Decisions

- Java 17 and Spring Boot for the main REST API.
- Python and FastAPI for resume parsing and matching.
- React for the web frontend.
- A browser extension for user-reviewed application autofill.
- MySQL for persistent application data.
- Git feature branches and pull requests for changes.
- Automated tests before merging features into main.
- Authorized job sources, respecting their terms and access rules.

## Consequences

Each component has a clear responsibility. Communication between
services must use explicit contracts.

We will begin with a small working application and introduce
additional services and infrastructure only when needed.

Database schema changes, credentials, service ports, and local
container resources must be managed without interfering with
other projects on the development machine.