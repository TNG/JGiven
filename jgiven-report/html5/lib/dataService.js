/**
 * Provides functions to access the generated scenario data
 */

jgivenReportApp.factory('dataService', [function () {
  'use strict';

  var tagFile = jgivenReport.tagFile;
  var testCases = jgivenReport.scenarios;
    
  function getAllScenarios() {
    return _.flatten(_.map(testCases, function (x) {
      return x.scenarios;
    }), true);
  }

  function getScenariosWhere(filter) {
    return sortByDescription(_.filter(getAllScenarios(), filter));
  }

  function getPendingScenarios() {
    return getScenariosWhere(function (x) {
      return x.executionStatus !== "FAILED" && x.executionStatus !== "SUCCESS";
    });
  }

  function getFailedScenarios() {
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

    getMetaData: function() {
      return jgivenReport.metaData;
    },

    getAllScenarios: getAllScenarios,
    getPendingScenarios: getPendingScenarios,
    getFailedScenarios: getFailedScenarios,

  };
}]);

/**
 * Global variable that is used by the generated JSONP files
 */
var jgivenReport = {
  scenarios: [],

  setTags: function setTags(tagFile) {
    this.tagFile = tagFile;
  },

  setMetaData: function setMetaData(metaData) {
    this.metaData = metaData;
    _.forEach(metaData.data, function (x) {
      document.writeln("<script src='data/" + x + "'></script>");
    });
  },

  addScenarios: function addScenarios(scenarios) {
    this.scenarios = this.scenarios.concat(scenarios);
  },

  setAllScenarios: function setAllScenarios(allScenarios) {
    this.scenarios = allScenarios;
  }
};
