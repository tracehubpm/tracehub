### How to configure?

The Project itself is defined as a set of [YAML](https://en.wikipedia.org/wiki/YAML) documents.

Documents must be located inside `.trace` directory,
placed in the root of your `master` branch.

```text
.tracehub
  /project.yml
  /docs
    /req.yml
    /uc1.yml
    /uc2.yml
  /jobs
    /update-year.yml
    /fix-me.yml
  ...
...(other files like package.json, Dockerfile and so on)
```

Project document (project.yml) is the special document and has the following notation:

```yaml
id: 5B0185CB-424B-42D4-9631-7B628B7BB78F
name: Tracehub
description: Tracehub and Project as a Code, a VCS-based collaboration tool
active: true
performers:
  individuals:
    - email: aliaksei.bialiauski@hey.com
      roles: [PO, ARC]
issues:
docs:
dependencies:
  - github.com/h1alexbel/cdit
  - github.com/tracehubpm/pmo
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
* `email`: performer email
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

Section `docs` represents the place where all documentation from `/docs` package
will be placed.

Section `dependencies` denotes the repository dependencies
This is can be helpful in case of [multi-repository](https://www.gitkraken.com/blog/git-multi-repo-vs-git-mono-repo#git-multi-repo-pros-cons) codebase management.

Inside those repositories, outlined as a dependencies, you should create
`.trace` directory inside root `master` as well with this configuration:

```yaml
central: github.com/tracehubpm/tracebot@master
```
Now, you inherit all the configuration from central repository (`@` specifies which branch to check).

### Jobs

Job (Activity analogue in [PMI](https://www.pmi.org/pmbok-guide-standards/lexicon)) is a distinct,
scheduled portion of work performed during the course of a project.

Each job must be located inside `/jobs` package inside the `.trace` directory.
Take a look at the example:

```yaml
label: Update License year to 2024
description: |-
  Lets update a copyright year in our License to 2024
cost: 20 minutes
```

The element `label` represents the job name.
The element `description` represents description of the job.
The element `cost` represents an estimation of this job, positive integer value.

Once it committed inside `master`, @tracehubgit will create it in issue-tracker that was
specified in `.project.yml`.

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
specified in `.project.yml`.

### Rules

Rules is a declarative way to observe and control
what's happened in the project.
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

### Supported platforms

We support the following platforms:

Git-hosted repositories in:
* [GitHub](https://github.com)
* [GitLab](https://gitlab.com)
* [Bitbucket](https://bitbucket.org)

Issue trackers:
* [GitHub Issues](https://github.com/features/issues)
* [JIRA](https://www.atlassian.com/software/jira)

Document storages:
* [GitHub](https://docs.github.com/en/communities/documenting-your-project-with-wikis/about-wikis)
* [Confluence](https://www.atlassian.com/software/confluence)

### How to contribute?

It is a Java project. First install [Java SDK 17+](https://www.oracle.com/java/technologies/downloads), [Maven 3.8+](https://maven.apache.org).
Then:

```shell
$ mvn clean install -Pqulice
```

The build has to be clean. If it's not, [submit an issue](https://github.com/tracehubpm/tracebot/issues).
Then, make your changes, make sure the build is still clean, and [submit a pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
