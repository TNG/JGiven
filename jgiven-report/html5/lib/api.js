'use strict';

/**
 * Global variable that is used by the generated JSONP files.
 * This variable can also be used by the custom Javascript file.
 */
var jgivenReport = {
  scenarios: [],
  customNavigationLinks: [],

  setTags: function setTags (tagFile) {
    this.tagFile = tagFile;
  },

  setMetaData: function setMetaData (metaData) {
    this.metaData = metaData;
    _.forEach(metaData.data, function (x) {
      document.writeln("<script src='data/" + x + "'></script>");
    });
  },

  addZippedScenarios: function addZippedScenarios (zip) {
    var string = pako.ungzip(atob(zip), {to: 'string'})
    var unzipped = JSON.parse(string);
    this.addScenarios(unzipped.scenarios);
  },

  addScenarios: function addScenarios (scenarios) {
    this.scenarios = this.scenarios.concat(scenarios);
  },

  setAllScenarios: function setAllScenarios (allScenarios) {
    this.scenarios = allScenarios;
  },

  addNavigationLink: function addNavigationLink (link) {
    this.customNavigationLinks.push(link);
  },

  setTitle: function setTitle (title) {
    this.metaData.title = title;
  }
};

