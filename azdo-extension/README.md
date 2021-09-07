# Azure DevOps - Publish JGiven Reports
This extension enables an user to publish JGiven HTML reports to a newly created tab inside the Dev Ops console, near the `Summary` tab.

# Usage
## 1. Intall the extension from the market
[Insert link here]

## 2. Add JGiven step in your azure pipeline task

## 3. Include the extension in your pipeline config file as following:
```
- task: publishjgivenreport@1
    inputs:
    jgivenReportLocation: 'path/to/html/build/folder'
```

If the jgivenReportLocation is not set, its default value will be:
```
build/reports/jgiven/html/
```

# Screenshot
![JGiven Panel in Dev Ops](resources/screenshot.png "JGiven Dashboard")

# Contribute
TODO: Explain how other users and developers can contribute to make your code better. 

If you want to learn more about creating good readme files then refer the following [guidelines](https://docs.microsoft.com/en-us/azure/devops/repos/git/create-a-readme?view=azure-devops). You can also seek inspiration from the below readme files:
- [ASP.NET Core](https://github.com/aspnet/Home)
- [Visual Studio Code](https://github.com/Microsoft/vscode)
- [Chakra Core](https://github.com/Microsoft/ChakraCore)