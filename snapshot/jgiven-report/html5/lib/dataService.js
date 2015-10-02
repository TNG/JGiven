/**
 * Provides functions to access the generated scenario data
 */

jgivenReportApp.factory('dataService', [function () {
  'use strict';

  var tagFile = jgivenReport.tagFile;
  var testCases = jgivenReport.scenarios;

  function getAllScenarios () {
    return _.flatten(_.map(testCases, function (testClass) {
      return _.map(testClass.scenarios, function (scenario) {
        scenario.classTitle = testClass.name;
        return scenario;
      });
    }), true);
  }

  function getScenariosWhere (filter) {
    return sortByDescription(_.filter(getAllScenarios(), filter));
  }

  function getPendingScenarios () {
    return getScenariosWhere(function (x) {
      return x.executionStatus !== "FAILED" && x.executionStatus !== "SUCCESS";
    });
  }

  function getFailedScenarios () {
    return getScenariosWhere(function (x) {
      return x.executionStatus === "FAILED";
    });
  }

  return {

    getTagFile: function () {
      return tagFile;
    },

    getTestCases: function () {
      return testCases;
    },

    getMetaData: function () {
      return jgivenReport.metaData;
    },

    getCustomNavigationLinks: function () {
      return jgivenReport.customNavigationLinks;
    },

    getAllScenarios: getAllScenarios,
    getPendingScenarios: getPendingScenarios,
    getFailedScenarios: getFailedScenarios,

  };
}]);

