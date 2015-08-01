/**
 * Utility functions
 */

function undefinedOrEmpty(array) {
  return !array || array.length === 0;
}

function getTagName(tag) {
  return tag.name ? tag.name : tag.type;
}

function getTagKey(tag) {
  return getTagName(tag) + (tag.value ? '-' + tag.value : '');
}

function tagToString(tag) {
  var res = '';

  if (!tag.value || tag.prependType) {
    res = getTagName(tag);
  }

  if (tag.value) {
    if (res) {
      res += '-';
    }
    res += tag.value;
  }

  return res;
}

function splitClassName(fullQualifiedClassName) {
  var index = fullQualifiedClassName.lastIndexOf('.');
  var className = fullQualifiedClassName.substr(index + 1);
  var packageName = fullQualifiedClassName.substr(0, index);
  return {
    className: className,
    packageName: packageName
  };
}

function getScenarioId(scenario) {
  return scenario.className + "." + scenario.testMethodName;
}

function sortByDescription(scenarios) {
  var sortedScenarios = _.forEach(_.sortBy(scenarios, function (x) {
    return x.description.toLowerCase();
  }), function (x) {
    x.expanded = false;
  });

  // directly expand a scenario if it is the only one
  if (sortedScenarios.length === 1) {
    sortedScenarios[0].expanded = true;
  }

  return sortedScenarios;
}

function getReadableExecutionStatus(status) {
  switch (status) {
    case 'SUCCESS':
      return 'Successful';
    case 'FAILED':
      return 'Failed';
    default:
      return 'Pending';
  }
}

function ownProperties(obj) {
  var result = [];
  for (var p in obj) {
    if (obj.hasOwnProperty(p)) {
      result.push(p);
    }
  }
  return result;
}

function deselectAll(options) {
  _.forEach(options, function (option) {
    option.selected = false;
  });
}

