# Contributing

Contributions are very welcome. There are different ways you can contribute:

1. Improve the documentation
    * The JGiven documentation is written in AsciiDoc and is located in the `docs` folder.
1. Fix/implement known issues
    * Issues with the label [help wanted](https://github.com/TNG/JGiven/labels/help%20wanted) would be the ideal candidate for that.
1. Come up with new ideas for improving JGiven
    * If you have an idea how JGiven can be improved, just open a new issue and describe your idea.
1. Report bugs
    * If you find a bug in JGiven, please open a new issue.

If you want to contribute code or documentation please follow the following workflow:

1. Fork the project
2. Create a new feature branch with your contribution (not needed for documentation improvements)
3. Implement your great new feature or bug fix
4. Provide a DCO for your contribution
5. Create a pull request

## System Requirements

* JDK 21 (`JAVA_HOME` and `PATH` should be set accordingly)
* Gradle (is automatically downloaded by the `gradlew` script)
* Android SDK if you want to build the `jgiven-android module`
    - Setting `ANDROID_SDK_ROOT` and `ANDROID=true` env variables is required

## Building and Testing JGiven

After you have cloned the Git repository execute the following command to build and test JGiven:

```
./gradlew test
```

## Code Formatting

Please follow the code format of existing code. Do never reformat a file!
You can ensure the correct code formatting by using the provided checkstyle formatting file ./checkstyle.xml

## Publishing a GitHub Release

With Bash, `jq`, and curl 7.76.0 or newer installed, provide `RELEASE_TOKEN`
(or `GITHUB_TOKEN` as a fallback) through your environment or CI secret store.
The token needs permission to write repository contents so it can create releases
and discover drafts. Do not enable shell tracing when supplying secrets.

```bash
RELEASE=true bash scripts/publishReleaseOnGithub.sh --version=1.2.3
```

The script adds the `v` tag prefix. As before, omitting `RELEASE=true` creates a
draft instead of a published release. Rerunning skips an already published release
successfully; an existing draft fails and must be published or deleted manually.
API failures stop the script without proceeding to publication.

The legacy `--credentials=username:token` option remains supported with a
deprecation warning, but exposes credentials in process arguments. Environment
tokens take precedence over this option.
