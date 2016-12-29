/**
 * Utility functions
 */


String.prototype.capitalize = function () {
  return this.charAt(0).toUpperCase() + this.slice(1);
};

Array.prototype.pushArray = function (arr) {
  this.push.apply(this, arr);
};

var ONE_HOUR_IN_MS = 3600000;
var ONE_MINUTE_IN_MS = 60000;
var ONE_SECOND_IN_MS = 1000;

function nanosToReadableUnit (nanos) {
  var msNum = nanos / 1000000;

  if (msNum < ONE_SECOND_IN_MS) {
    return Math.floor(msNum) + "ms";
  }

  if (msNum < ONE_MINUTE_IN_MS) {
    return parseFloat(msNum / 1000).toFixed(3) + "s";
  }

  var hours = Math.floor(msNum / ONE_HOUR_IN_MS);
  var mins = Math.floor((msNum - (hours * ONE_HOUR_IN_MS)) / ONE_MINUTE_IN_MS);
  var secs = Math.floor((msNum - (hours * ONE_HOUR_IN_MS + mins * ONE_MINUTE_IN_MS)) / ONE_SECOND_IN_MS);
  var ms = Math.floor(ms);

  var time = secs;
  var unit = "min";

  if (mins > 0) {
    if (secs < 10) {
      secs = "0" + secs;
    }
    time = mins + ":" + secs;
  }

  if (hours > 0) {
    if (mins < 10) {
      mins = "0" + mins;
    }
    time = hours + ":" + mins;
    unit = "h";
  }
  return time + unit;
}


function undefinedOrEmpty (array) {
  return !array || array.length === 0;
}

function getWordValue(word) {
  return word.argumentInfo ? word.argumentInfo.formattedValue : word.value
}

function getTagName (tag) {
  return tag.name ? tag.name : tag.type;
}

function getTagId (tag) {
  return tag.fullType ? tag.fullType : tag.type;
}

function getTagKey (tag) {
  return getTagId(tag) + (tag.value ? '-' + tag.value : '');
}

function tagToString (tag) {
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

function splitClassName (fullQualifiedClassName) {
  var index = fullQualifiedClassName.lastIndexOf('.');
  var className = fullQualifiedClassName.substr(index + 1);
  var packageName = fullQualifiedClassName.substr(0, index);
  return {
    className: className,
    packageName: packageName
  };
}

function getScenarioId (scenario) {
  return scenario.className + "." + scenario.testMethodName;
}

function sortByDescription (scenarios) {
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

function getReadableExecutionStatus (status) {
  switch (status) {
    case 'SUCCESS':
      return 'Successful';
    case 'FAILED':
      return 'Failed';
    default:
      return 'Pending';
  }
}

function ownProperties (obj) {
  var result = [];
  for (var p in obj) {
    if (obj.hasOwnProperty(p)) {
      result.push(p);
    }
  }
  return result;
}

function deselectAll (options) {
  _.forEach(options, function (option) {
    option.selected = false;
  });
}

