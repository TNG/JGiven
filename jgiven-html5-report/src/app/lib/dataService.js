/**
 * Provides functions to access the generated scenario data
 */

jgivenReportApp.factory('dataService', [function () {
  'use strict';

  var tagFile = jgivenReport.tagFile;
  var scenarios = jgivenReport.scenarios;

  function getAllScenarios() {
    return _.flatten(_.map(scenarios, function (x) {
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

  function getTagByTagId(tagId) {
    var tagInstance = tagFile.tags[tagId];
    var tagType = tagFile.tagTypeMap[tagInstance.tagType];
    var tag = Object.create(tagType);
    tag.value = tagInstance.value;
    return tag;
  }

  return {

    getTagFile: function () {
      return tagFile;
    },

    getScenarios: function () {
      return scenarios;
    },

    getTestClasses: function () {
      return scenarios;
    },

    getAllScenarios: getAllScenarios,
    getPendingScenarios: getPendingScenarios,
    getFailedScenarios: getFailedScenarios,
    getTagByTagId: getTagByTagId

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
