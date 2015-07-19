/**
 * Responsible for handling class and package-related operations, e.g. finding scenarios for a certain class or package
 */

jgivenReportApp.factory('classService', ['dataService', function (dataService) {
  'use strict';

  var classNameScenarioMap = getClassNameScenarioMap();
  var packageNodeMap = {};
  var rootPackage = getRootPackage();


  function getClassNameScenarioMap() {
    var classNameScenarioMap = {};
    var testClasses = dataService.getTestClasses();

    _.forEach(testClasses, function (testClass) {
      classNameScenarioMap[testClass.className] = testClass;
    });
    return classNameScenarioMap;
  }

  function getTestClasses() {
    return dataService.getTestClasses();
  }

  /**
   * Builds up the navigation tree for classes
   */
  function getRootPackage() {
    var allClasses = getTestClasses();
    var rootPackage = getPackageNode("");

    _.forEach(allClasses, function (testClass) {
      var classObj = splitClassName(testClass.className);
      getPackageNode(classObj.packageName).addClassNode(createClassNode(classObj));
    });

    return rootPackage;

    function createPackageNode(packageObj) {
      return {
        packageObj: packageObj,

        nodeName: function () {
          return packageObj.name;
        },

        url: function () {
          return '#package/' + packageObj.qualifiedName;
        },

        leafs: function () {
          return packageObj.classes;
        },

        childNodes: function () {
          return packageObj.packages;
        },

        hasChildren: function () {
          return packageObj.packages.length + packageObj.classes.length > 0;
        },

        addClassNode: function (classNode) {
          packageObj.classes.push(classNode);
        },

        addPackageNode: function (packageNode) {
          packageObj.packages.push(packageNode);
        }
      }
    }

    function createClassNode(classObj) {
      return {
        fullQualifiedName: function () {
          return classObj.packageName + "." + classObj.className;
        },

        nodeName: function () {
          return classObj.className;
        },

        url: function () {
          return '#class/' + this.fullQualifiedName();
        }
      };
    }

    function getPackageNode(packageName) {
      var parentPackage, index, simpleName;
      var packageNode = packageNodeMap[packageName];
      if (packageNode === undefined) {
        index = packageName.lastIndexOf('.');
        simpleName = packageName.substr(index + 1);

        packageNode = createPackageNode({
          qualifiedName: packageName,
          name: simpleName,
          classes: [],
          packages: []
        });

        packageNodeMap[packageName] = packageNode;

        if (simpleName !== "") {
          parentPackage = getPackageNode(packageName.substring(0, index));
          parentPackage.addPackageNode(packageNode);
        }
      }
      return packageNode;
    }
  }

  function getScenariosOfPackage(packageName) {
    var scenarios = [];
    var packageNode = packageNodeMap[packageName];
    collectScenariosFromPackage(packageNode, scenarios);
    return scenarios;
  }

  function collectScenariosFromPackage(packageNode, scenarios) {
    _.forEach(packageNode.leafs(), function (clazzNode) {
      scenarios.pushArray(classNameScenarioMap[clazzNode.fullQualifiedName()].scenarios);
    });

    _.forEach(packageNode.childNodes(), function (subpackageNode) {
      collectScenariosFromPackage(subpackageNode, scenarios);
    });
  }


  return {
    getTestClasses: getTestClasses,
    getScenariosOfPackage: getScenariosOfPackage,
    getRootPackage: function () {
      return rootPackage;
    }
  };

}]);