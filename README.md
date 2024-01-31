[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/trarcehubpm/tracehub)](http://www.rultor.com/p/tracehubpm/tracehub)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/tracehubpm/tracehub/actions/workflows/mvn.yml/badge.svg)](https://github.com/tracehubpm/tracehub/actions/workflows/mvn.yml)
[![codecov](https://codecov.io/gh/tracehubpm/tracehub/graph/badge.svg?token=hXMw1jvPJo)](https://codecov.io/gh/tracehubpm/tracehub)

[![Hits-of-Code](https://hitsofcode.com/github/tracehubpm/tracehub)](https://hitsofcode.com/view/github/tracehubpm/tracehub)
[![PDD status](http://www.0pdd.com/svg?name=tracehubpm/tracehub)](http://www.0pdd.com/p?name=tracehubpm/tracehub)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/tracehubpm/tracehub/blob/master/LICENSE.txt)

Tracehub and Project as a Code, a VCS-based collaboration tool, and its robot.

**Motivation**. We want to keep our code, tickets, and docs in-sync.
However, there is no tool to do it in flexible and configurable way.
We provide both-side integration through [YAML](https://yaml.org) notation.

**Principles**. These are the [design principles](https://www.elegantobjects.org/#principles) behind tracehub.

**How to use**. All you need is to register your project in [pmo](https://github.com/tracehubpm/pmo).

Then create project document (project.yml in the root of `.trace` directory, in `master` branch)
in the following notation:

```yaml
id: 5B0185CB-424B-42D4-9631-7B628B7BB78F
name: Tracehub
active: true
performers:
  - name: h1alexbel
    roles:
      - DEV
      - ARC
backlog:
  type: GitHub
  rules:
    min-words: 20
    min-estimate: 25m
    max-estimate: 90m
docs:
  type: Markdown
dependencies:
  - github.com/h1alexbel/cdit@master
  - github.com/tracehubpm/pmo@master
```

your file tree should look like this:

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

### project.yml notation

The element `id` is generated by [tracehubpm/pmo](https://github.com/tracehubpm/pmo)
and represents a unique [UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier)
assigned to the project.

The element `name` represents the name of the project.

The element `description` denotes project's description.

The element `active` represents that project active-in-development.

Section `performers` for specifying who is working on the projects.
Each one has the following schema:
* `name`: performer's name
* `roles`: an array of roles, that performer has

For now, we support these roles:
* `PO`: Product Owner
* `PM`: Project Manager
* `ARC`: Architect
* `DEV`: Developer, Programmer
* `TEST`: Tester


Section `backlog` represents the place where all issues from `/jobs` package
will be placed.
* `type`: issue tracker type, possible values are `GitHub`
(jobs will be registered as [issues](https://github.com/features/issues) in GitHub),
`JIRA` (jobs will be registered as tickets in [Atlassian JIRA](https://www.atlassian.com/software/jira)).
* `rules`: rules, related to the backlog.

Section `docs` represents the place where all documentation from `/docs` package
will be placed.
* `type`: document repository, possible values are `Markdown`
(all docs will be formatted in markdown and placed inside root `/docs` directory),
`GitHub Wikis`, `Confluence`.

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

Jobs can be created as "work activity" for performers.

Each job must be located inside `/jobs` package in the `.trace` directory.
Take a look at the example of Job document:

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

Once it committed inside `master` branch, @tracehubgit will create it in backlog that was
specified in `project.yml`.

It works both sides, when somebody will create/update/delete ticket in a `backlog`,
@tracehubgit will catch it up and create new Pull Request with your changes in your git repository.

### Docs

To document something, use Documents.

Each document must be located inside `/docs` package in the `.trace` directory.
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

Once it goes in `master` branch, @tracehubgit will create it in document repository that was
specified in `project.yml`.

It also works both sides, when somebody will create/update/delete document in a `docs`,
@tracehubgit will catch it up and create new Pull Request with changes in your git repository.

### Rules

TBD..

### Secret Variables

Avoid storing sensitive information in Git inside your YAML documents.
Use this notation instead:

```yaml
...
backlog:
 type: GitHub
 url: ${jira-url}
 token: ${jira-token}
 rules:
   ...
...
```

Both secrets `jira-url` and `jira-token` must be present in your project account
inside [pmo](https://github.com/tracehubpm/pmo).
When integration will happen,
values from these secret variables will be injected instead of placeholders.

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

Then run:

```shell
$ mvn clean install -Pjacoco
```

The build has to be clean. If it's not, [submit an issue](https://github.com/tracehubpm/tracehub/issues).

Then, make your changes, make sure the build is still clean, and [submit a pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).

If you want to run a real simulation run this:

```shell
$ mvn clean install -Psimulation -DTracehub-GitHubToken=...
```

Provide your GitHub [token](https://github.com/settings/tokens) with write permissions 
to the next repositories:

* [h1alexbel/test](https://github.com/h1alexbel/test)
* [h1alexbel/invalid-project](https://github.com/h1alexbel/invalid-project)
