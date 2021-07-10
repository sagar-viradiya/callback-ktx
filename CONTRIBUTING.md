# Contributing to callback-ktx

First thing first, Thank you for taking time and deciding to contribute :tada:

## Before you start
Callback extensions are divided across different modules based on the category they fall under. For example, all framework APIs would fall under the core module. Anything not related to the framework is in its separate module. 
If you are contributing new extensions please see if they could be part of the existing module or they belong to a new module.

## How Can I Contribute?

### Reporting Bugs and suggesting enhancements
Bugs and enhancements requests are tracked as [GitHub issues](https://guides.github.com/features/issues/). Before opening an issue for reporting bug or enhancement please check the existing list of issues as chances are it's already open by someone.
Explain and include additional details:

* **Use a clear and descriptive title** for the issue to identify the problem or enhancement.
* For a bug, **describe the exact steps which reproduce the problem** in as many details as possible.
* For an enhancement, **provide a step-by-step description of the suggested enhancement** in as many details as possible.
* **Provide specific examples to demonstrate the steps**. Include links to files or GitHub projects, or copy/pasteable snippets, which you use in those examples. If you're providing snippets in the issue, use [Markdown code blocks](https://help.github.com/articles/markdown-basics/#multiple-lines).

### Proposing new extensions
New extension request is also tracked through [GitHub issues](https://guides.github.com/features/issues/). Please check existing issues before you open a new one as chances are someone might have a similar request.
Please include the following while opening an issue.

* **Use a clear and descriptive title** for the new extension request.
* **Explain why this enhancement would be useful**.

### Pull Requests

Below are a few points that you should consider before opening a pull request.

1. Please follow the official kotlin code [styleguides](https://kotlinlang.org/docs/coding-conventions.html)
2. Commit message should have proper title and description of all changes.
3. Please modify kdoc (Kotlin documentation) if required while fixing a bug or doing an enhancement. Similarly please add kdoc for new extensions.
4. Please make sure to change instrumentation tests if required in case you are fixing a bug or doing an enhancement.
5. Please make sure to include instrumentation tests if you are contributing new extensions.
6. Please run test.sh script which will verify your code style, binary compatibility and instrumentation tests.

Below are a few points that you should consider while opening a pull request.

1. Please make sure to open PR against the default branch which is `master`.
2. After you submit your pull request, verify that all [status checks](https://help.github.com/articles/about-status-checks/) are passing. If a status check is failing, and you believe that the failure is unrelated to your change, please leave a comment on the pull request explaining why you believe the failure is unrelated. A maintainer will re-run the status check for you.
