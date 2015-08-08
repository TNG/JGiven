# Contributing

Contributions are very welcome. There are different ways you can contribute:

1. Improve the documentation
    * The JGiven documentation lives in the [gh-pages](https://github.com/TNG/JGiven/tree/gh-pages) branch.
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
4. Create a pull request

## System Requirements

* JDK 1.7 (`JAVA_HOME` and `PATH` should be set accordingly)
    - Note that JGiven is built and compatible with Java 1.6, however, because of some test libraries, Java 1.7 is required for executing the tests in the `jgiven-tests` project
* Gradle (is automatically downloaded by the `gradlew` script)
* Bower (required for the HTML5 report generator, can be installed with `sudo npm install -g bower`)
* PhantomJS (required for the Selenium tests of the HTML5 report, can be installed under Ubuntu with `sudo apt-get install phantomjs`)

## Building and Testing JGiven

After you have cloned the Git repository execute the following command to build and test JGiven:

```
./gradlew test
```

## Code Formatting

Please follow the code format of existing code. Do never reformat a file!
You can ensure the correct code formatting by using the provided Eclipse formatter `develop/eclipse-formatter.xml`.
