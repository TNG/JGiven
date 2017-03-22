/**
 * Utility functions
 */

import { forEach, filter, indexOf, sortBy, takeWhile, drop, partial } from "lodash";

String.prototype.capitalize = function () {
  return this.charAt(0).toUpperCase() + this.slice(1);
};

Array.prototype.pushArray = function (arr) {
  this.push.apply(this, arr);
};

var ONE_HOUR_IN_MS = 3600000;
var ONE_MINUTE_IN_MS = 60000;
var ONE_SECOND_IN_MS = 1000;

export function nanosToReadableUnit (nanos) {
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


export function undefinedOrEmpty (array) {
  return !array || array.length === 0;
}

export function getWordValue(word) {
  return word.argumentInfo ? word.argumentInfo.formattedValue : word.value
}

export function getTagName (tag) {
  return tag.name ? tag.name : tag.type;
}

export function getTagId (tag) {
  return tag.fullType ? tag.fullType : tag.type;
}

export function getTagKey (tag) {
  return getTagId(tag) + (tag.value ? '-' + tag.value : '');
}

export function tagToString (tag) {
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

export function splitClassName (fullQualifiedClassName) {
  var index = fullQualifiedClassName.lastIndexOf('.');
  var className = fullQualifiedClassName.substr(index + 1);
  var packageName = fullQualifiedClassName.substr(0, index);
  return {
    className: className,
    packageName: packageName
  };
}

export function getScenarioId (scenario) {
  return scenario.className + "." + scenario.testMethodName;
}

export function sortByDescription (scenarios) {
  var sortedScenarios = forEach(sortBy(scenarios, function (x) {
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

export function getReadableExecutionStatus (status) {
  switch (status) {
    case 'SUCCESS':
      return 'Successful';
    case 'FAILED':
      return 'Failed';
    default:
      return 'Pending';
  }
}

export function ownProperties (obj) {
  var result = [];
  for (var p in obj) {
    if (obj.hasOwnProperty(p)) {
      result.push(p);
    }
  }
  return result;
}

export function deselectAll (options) {
  forEach(options, function (option) {
    option.selected = false;
  });
}

// returns a tuple of an Array where index         -> value
//                and an Array where argument name -> value
export function getArgumentInfos(wordArray) {

  var enumArray = [];
  var nameArray = [];

  function isArgument(word) { return !!word.argumentInfo; }
  function updateContainer(nameArr, enumArr, word) {
    nameArr[word.argumentInfo.argumentName] = word.value;
    enumArr.push(word.value);
  }

  forEach(wordArray, function (word) {
    if(isArgument(word)) updateContainer(nameArray, enumArray, word);
  });

  return [enumArray, nameArray];
}

export function parseNextInt(arr) {
    var numbers = takeWhile(arr, function (c) { return !isNaN(c) && c !== " "; }).join("");
    var parsedInt = parseInt(numbers);
    var result  = { integer : isNaN(parsedInt) ? undefined : parsedInt
                  , length  : numbers.length } ;
    return result;
}

export function parseNextArgumentName(string) {
    var stopChars = [" ", ",", ";", ":","\"","%","!","[","]","(",")","-", "$"]; // java-identifier or $
    var isNonStopChar = function (c) { return indexOf(stopChars, c) === -1; };
    return takeWhile(string, isNonStopChar).join("");
}

export function replaceArguments(string, enumArray, nameArray) {
    var separator = "$";
    var result = [];
    var singlePlaceHolderCount = 0;
    var dropNextWhitespace = false;
    var argument = undefined;
    var currentWord = "";

    for(var i = 0; i < string.length; ++i) {
        var c = string.charAt(i);

        var dollarMatch = c === separator;
        var nextCharExists = (i + 1) < string.length;
        var escapedDollarMatch = nextCharExists && (string.charAt(i + 1) === separator);
        var argumentName = nextCharExists ? parseNextArgumentName(drop(string, i+1)) : "";
        var namedArgumentExists = argumentName.length > 0;
        var nameArgumentMatch = namedArgumentExists && nameArray[argumentName] !== undefined;
        var enumArgumentMatch = nextCharExists && (enumArray.length > parseNextInt(drop(string, i+1)).integer - 1);
        var singleDollarCountIndexExists = singlePlaceHolderCount < enumArray.length;

        // $
        if (dollarMatch) {

            // $$
            if (escapedDollarMatch) {
                argument = separator;
                i += 1;

            // $argument
            } else if (nameArgumentMatch) {
                argumentName = parseNextArgumentName(drop(string, i+1));
                argument = nameArray[argumentName];
                i += argumentName.length;

            // $1
            } else if (enumArgumentMatch) {
                var index = parseNextInt(drop(string, i+1));
                argument = enumArray[index.integer-1];
                i += index.length;

            // $argumentNotKnown
            } else if (singleDollarCountIndexExists && namedArgumentExists) {
                argumentName = parseNextArgumentName(drop(string, i+1));
                argument = enumArray[singlePlaceHolderCount++];
                i += argumentName.length;

            // $
            } else if (singleDollarCountIndexExists) {
                argument = enumArray[singlePlaceHolderCount++];

            // ($notKnown || $) && counter > argument.size
            } else {
                argument = '$' + parseNextArgumentName(drop(string, i+1));
                i += argument.length;
            }

            // unfortunately we need this to be compatible to the @As description
            // is joins on space, and we will too, instead of result.join("")
            dropNextWhitespace = true;
            if (currentWord.length !== 0) {
                result.push(removeTrailingWhitespace(currentWord));
                currentWord = "";
            }
            result.push(argument);

        // otherwise
        } else {
            if (dropNextWhitespace && (c === ' ')) {
                dropNextWhitespace = false;
            } else {
                currentWord += c;
            }
        }
    }

    // flush last word
    if (currentWord.length !== 0) {
        result.push(currentWord);
    }

    return result.join(" ");
}

export function removeTrailingWhitespace(string) {
    if (string.charAt(string.length - 1) === " ") {
        return string.slice(0, string.length - 1);
    } else {
        return string;
    }
}

export function getThumbnailPath(path) {
    var splitted = path.split(".");
    var extension = splitted.pop();
    var subpath   = splitted.join(".");
    return subpath + "-thumb." + extension;
}

export function isImageType(mimetype) {
    var splitted = mimetype.split("/");
    return splitted !== null && splitted[0] === "image";
}