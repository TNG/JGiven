/**
 * Provides search functionality
 */

jgivenReportApp.factory('searchService', ['dataService', function (dataService) {
  'use strict';

  function findScenarios(searchString) {
    var searchStrings = searchString.split(" ");
    console.log("Searching for " + searchStrings);

    var regexps = _.map(searchStrings, function (x) {
      return new RegExp(x, "i");
    });

    return sortByDescription(_.filter(dataService.getAllScenarios(), function (x) {
      return scenarioMatchesAll(x, regexps);
    }));
  }

  function scenarioMatchesAll(scenario, regexpList) {
    for (var i = 0; i < regexpList.length; i++) {

      if (!scenarioMatches(scenario, regexpList[i])) {
        return false;
      }
    }
    return true;
  }

  function scenarioMatches(scenario, regexp) {
    if (scenario.className.match(regexp)) {
      return true;
    }

    if (scenario.description.match(regexp)) {
      return true;
    }

    var i;
    for (i = 0; i < scenario.tags.length; i++) {
      var tag = scenario.tags[i];
      if ((getTagName(tag) && getTagName(tag).match(regexp)) ||
        (tag.value && tag.value.match(regexp))) {
        return true;
      }
    }

    for (i = 0; i < scenario.scenarioCases.length; i++) {
      if (caseMatches(scenario.scenarioCases[i], regexp)) {
        return true;
      }
    }

  }

  function caseMatches(scenarioCase, regexp) {
    for (var i = 0; i < scenarioCase.steps.length; i++) {
      if (stepMatches(scenarioCase.steps[i], regexp)) {
        return true;
      }
    }

    return false;
  }

  function stepMatches(step, regexp) {
    for (var i = 0; i < step.words.length; i++) {
      if (step.words[i].value.match(regexp)) {
        return true;
      }
    }

    return false;
  }


  return {
    findScenarios: findScenarios
  };
}]);
