[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/trarcehubpm/tracehub)](http://www.rultor.com/p/tracehubpm/tracehub)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/tracehubpm/tracehub/actions/workflows/mvn.yml/badge.svg)](https://github.com/tracehubpm/tracehub/actions/workflows/mvn.yml)
[![codecov](https://codecov.io/gh/tracehubpm/tracehub/graph/badge.svg?token=hXMw1jvPJo)](https://codecov.io/gh/tracehubpm/tracehub)

[![Hits-of-Code](https://hitsofcode.com/github/tracehubpm/tracehub)](https://hitsofcode.com/view/github/tracehubpm/tracehub)
[![PDD status](http://www.0pdd.com/svg?name=tracehubpm/tracehub)](http://www.0pdd.com/p?name=tracehubpm/tracehub)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/tracehubpm/tracehub/blob/master/LICENSE.txt)

Tracehub and Project as a Code, a VCS-based collaboration tool, and its robots.

### How to configure?

The Project itself is defined as a set of [YAML](https://en.wikipedia.org/wiki/YAML) documents.

All files must be located inside `.trace` directory,
placed in the root of your `master` branch.

```text
.trace
  project.yml
  /docs
    req.yml
    uc1.yml
    uc2.yml
  /jobs
    update-year.yml
    fix-me.yml
  /rules
    contribution.yml
  ...
...
```

Project document (project.yml in the root of `.trace` directory) is the special document
and has the following notation:

```yaml
id: 5B0185CB-424B-42D4-9631-7B628B7BB78F
name: Tracehub
active: true
performers:
  - name: h1alexbel
    roles:
      - DEV
      - ARC
issues:
  type: JIRA
  url: ...
  token: ...
docs:
  type: Confluence
  url: ...
  token: ...
dependencies:
  - github.com/h1alexbel/cdit@master
  - github.com/tracehubpm/pmo@master
```

The element `id` is generated by [tracehubpm/pmo](https://github.com/tracehubpm/pmo)
and represents a unique [UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier)
assigned to the project.

The element `name` represents the name of the project.

The element `description` denotes project's description.

The element `active` represents that project active-in-development.

Section `performers` for specifying who is working on the projects.
Subsection `individuals` allows you to specify independent project people, each one has
the following schema:
* `name`: performer's name
* `roles`: an array of roles, performer has

For now, we support these roles:
* `PO`: Product Owner
* `PM`: Project Manager
* `ARC`: Architect
* `DEV`: Developer, Programmer
* `TEST`: Tester
* `SA`: System Analyst


Section `issues` represents the place where all issues from `/jobs` package
will be placed.
* `type`: issue tracker type, take a look at [supported](#supported-platforms) ones.

Section `docs` represents the place where all documentation from `/docs` package
will be placed.
* `type`: document repository, take a look at [supported](#supported-platforms) ones.

Section `dependencies` denotes the repository dependencies
This is can be helpful in case of [multi-repository](https://www.gitkraken.com/blog/git-multi-repo-vs-git-mono-repo#git-multi-repo-pros-cons) codebase management.

Inside those repositories, outlined as a dependencies (`@` specifies which branch to check),
you should create `.trace` directory inside root, in `master` branch as well
with this configuration (project.yml):

```yaml
central: github.com/tracehubpm/tracebot
```
Now, you inherit all the configuration from central repository.

### Jobs

Job (Activity analogue in [PMI](https://www.pmi.org/pmbok-guide-standards/lexicon)) is a distinct,
scheduled portion of work performed during the course of a project.

Each job must be located inside `/jobs` package inside the `.trace` directory.
Take a look at the example:

```yaml
label: Update License year to 2024
description: |
  Lets update a copyright year in our License to 2024
cost: 20 minutes
role: ARC
```

The element `label` represents the job name.
The element `description` represents description of the job.
The element `cost` represents an estimation of this job, positive integer value.
The element `role` represents a required role for assigning on the job.
If not specified, then it will be DEV.

Once it committed inside `master`, @tracehubgit will create it in issue-tracker that was
specified in `project.yml`.

### Docs

Project Documents.

Each so-called doc must be located inside `/docs` package inside the `.trace` directory.
Take a look at the example of it:

```yaml
type: Specs
spec: |-
  ...
```

The element `type` defines the type of the document.
We support these values:
* Specs, analogue of [SRS](https://ieeexplore.ieee.org/document/278253)
* UC, which stands for [Use case](https://en.wikipedia.org/wiki/Use_case)
* Other, any other document you wish to have

Once it committed inside `master`, @tracehubgit will create it in document repository that was
specified in `.trace/project.yml`.

### Rules

Rules is a declarative way to observe and control
the situation in the project.
Can be helpful feature for PM, ARC, or PO, and
motivation source for others project-performers.

In this example project declares a rule of contribution:
if project is active-in-development, then
each DEV must deliver us at least 5 Pull Requests per week.

```yaml
each:
  role:
    - DEV
  if: project.active
  contribution: 5 PR per week
```

### Secret Variables

Avoid storing sensitive information in your YAML documents.
Use this notation instead:

```yaml
...
issues:
 type: JIRA
 url: ${pmo.jira-url}
 token: ${pmo.jira-token}
...
```

Both secrets `jira-url` and `jira-token` must be present in your project account
inside [pmo](https://github.com/tracehubpm/pmo).
Now, values from these secret variables will be injected when integration happens.

### Supported platforms

We aim to support the following platforms:

Git-hosted repositories in:
* [GitHub](https://github.com)
* [GitLab](https://gitlab.com)
* [Bitbucket](https://bitbucket.org)

Issue trackers:
* [GitHub Issues](https://github.com/features/issues)
* [JIRA](https://www.atlassian.com/software/jira)

Document storages:
* [GitHub Wikis](https://docs.github.com/en/communities/documenting-your-project-with-wikis/about-wikis)
* [Confluence](https://www.atlassian.com/software/confluence)

### How to contribute?

It is a Java project. First install [Java SDK 17+](https://www.oracle.com/java/technologies/downloads), [Maven 3.8+](https://maven.apache.org).

Then:

```shell
$ mvn clean install -Pjacoco
```

The build has to be clean. If it's not, [submit an issue](https://github.com/tracehubpm/tracehub/issues).

Then, make your changes, make sure the build is still clean, and [submit a pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).

### Maven profiles

* `release`: for packaging and preparing artifact to become a Docker image.
* `simulation`: for running integration tests on GitHub, GitLab, JIRA and others.
* `jacoco`: for running coverage control.
