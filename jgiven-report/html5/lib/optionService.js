/**
 * Provides functions for sorting, grouping and filtering
 */

jgivenReportApp.factory('optionService', ['dataService', function (dataService) {
  'use strict';

  function groupTagsByType (tagList) {
    var types = {};
    _.forEach(tagList, function (x) {
      var list = types[getTagName(x)];
      if (!list) {
        list = [];
        types[getTagName(x)] = list;
      }
      list.push(x);
    });
    return _.map(_.sortBy(Object.keys(types), function (key) {
      return key;
    }), function (x) {
      return {
        type: x,
        tags: types[x]
      }
    });
  }


  function getOptions (scenarios, optionSelection) {
    var result = getDefaultOptions(scenarios);

    if (optionSelection.sort) {
      deselectAll(result.sortOptions);
      selectOption('id', optionSelection.sort, result.sortOptions);
    }

    if (optionSelection.group) {
      deselectAll(result.groupOptions);
      selectOption('id', optionSelection.group, result.groupOptions);
    }

    if (optionSelection.tags) {
      deselectAll(result.tagOptions);
      _.forEach(optionSelection.tags, function (tagName) {
        selectOption('name', tagName, result.tagOptions);
      });
    }

    if (optionSelection.status) {
      deselectAll(result.statusOptions);
      _.forEach(optionSelection.status, function (status) {
        selectOption('id', status, result.statusOptions);
      });
    }

    if (optionSelection.classes) {
      deselectAll(result.classOptions);
      _.forEach(optionSelection.classes, function (className) {
        selectOption('name', className, result.classOptions);
      });
    }
    return result;
  }

  function selectOption (property, value, options) {
    _.filter(options, function (option) {
      return option[property] === value;
    })[0].selected = true;
  }

  function getDefaultOptions (scenarios) {
    var uniqueSortedTags = getUniqueSortedTags(scenarios);

    return {
      sortOptions: getDefaultSortOptions(uniqueSortedTags),
      groupOptions: getDefaultGroupOptions(),
      statusOptions: getDefaultStatusOptions(),
      tagOptions: getDefaultTagOptions(uniqueSortedTags),
      classOptions: getDefaultClassOptions(scenarios)
    }
  }

  function getDefaultStatusOptions () {
    return [
      {
        selected: false,
        name: 'Successful',
        id: 'success',
        apply: function (scenario) {
          return scenario.executionStatus === 'SUCCESS';
        }
      },
      {
        selected: false,
        name: 'Failed',
        id: 'fail',
        apply: function (scenario) {
          return scenario.executionStatus === 'FAILED';
        }
      },
      {
        selected: false,
        name: 'Pending',
        id: 'pending',
        apply: function (scenario) {
          return scenario.executionStatus !== 'SUCCESS' &&
            scenario.executionStatus !== 'FAILED';
        }
      }
    ];
  }

  function getDefaultTagOptions (uniqueSortedTags) {
    var result = [];
    _.forEach(uniqueSortedTags, function (tag) {
      var tagName = tagToString(tag);
      result.push({
        selected: false,
        name: tagName,
        apply: function (scenario) {
          for (var i = 0; i < scenario.tags.length; i++) {
            if (tagToString(scenario.tags[i]) === tagName) {
              return true;
            }
          }
          return false;
        }
      })
    });
    return result;
  }

  function getDefaultClassOptions (scenarios) {
    var uniqueSortedClassNames = getUniqueSortedClassNames(scenarios)
      , result = [];
    _.forEach(uniqueSortedClassNames, function (className) {
      result.push({
        selected: false,
        name: className,
        apply: function (scenario) {
          return scenario.className === className;
        }
      })
    });
    return result;
  }

  function getUniqueSortedClassNames (scenarios) {
    var allClasses = {};
    _.forEach(scenarios, function (scenario) {
      allClasses[scenario.className] = true;
    });
    return ownProperties(allClasses).sort();
  }

  function getUniqueSortedTags (scenarios) {
    var allTags = {};
    _.forEach(scenarios, function (scenario) {
      _.forEach(scenario.tags, function (tag) {
        allTags[tagToString(tag)] = tag;
      });
    });
    return _.map(ownProperties(allTags).sort(), function (tagName) {
      return allTags[tagName];
    });
  }

  function getDefaultGroupOptions () {
    return [
      {
        selected: true,
        default: true,
        id: 'none',
        name: 'None',
        apply: function (scenarios) {
          var result = toArrayOfGroups({
            'all': scenarios
          });
          result[0].expanded = true;
          return result;
        }
      },
      {
        selected: false,
        id: 'class',
        name: 'Class',
        apply: function (scenarios) {
          return toArrayOfGroups(_.groupBy(scenarios, 'className'));
        }
      },
      {
        selected: false,
        id: 'classtitle',
        name: 'Class Title',
        apply: function (scenarios) {
          return toArrayOfGroups(_.groupBy(scenarios, function (scenario) {
            if (scenario.classTitle) {
              return scenario.classTitle;
            }
            return '<No Title>';
          }));
        }
      },
      {
        selected: false,
        id: 'status',
        name: 'Status',
        apply: function (scenarios) {
          return toArrayOfGroups(_.groupBy(scenarios, function (scenario) {
            return getReadableExecutionStatus(scenario.executionStatus);
          }));
        }
      },
      {
        selected: false,
        id: 'tag',
        name: 'Tag',
        apply: function (scenarios) {
          return toArrayOfGroups(groupByTag(scenarios));
        }
      }
    ];
  }

  function groupByTag (scenarios) {
    var result = {}, i, j, tagName;
    _.forEach(scenarios, function (scenario) {
      _.forEach(scenario.tags, function (tag) {
        tagName = tagToString(tag);
        addToArrayProperty(result, tagName, scenario);
      });

      if (scenario.tags.length === 0) {
        // extra space to ensure that it is first in the list
        addToArrayProperty(result, ' No Tag', scenario);
      }
    });
    return result;
  }

  function addToArrayProperty (obj, p, value) {
    if (!obj.hasOwnProperty(p)) {
      obj[p] = [];
    }
    obj[p].push(value);
  }

  function getDefaultSortOptions (uniqueSortedTags) {
    var result = [
      {
        selected: true,
        default: true,
        id: 'name-asc',
        name: 'A-Z',
        apply: function (scenarios) {
          return _.sortBy(scenarios, function (x) {
            return (x.classTitle ? x.classTitle + ' ' : 'Z') + x.description.toLowerCase();
          });
        }
      },
      {
        selected: false,
        id: 'name-desc',
        name: 'Z-A',
        apply: function (scenarios) {
          return _.chain(scenarios).sortBy(function (x) {
            return (x.classTitle ? x.classTitle + ' ' : 'Z') + x.description.toLowerCase();
          }).reverse().value();
        }
      },
      {
        selected: false,
        id: 'status-asc',
        name: 'Failed',
        apply: function (scenarios) {
          return _.chain(scenarios).sortBy('executionStatus')
            .value();
        }
      },
      {
        selected: false,
        id: 'status-desc',
        name: 'Successful',
        apply: function (scenarios) {
          return _.chain(scenarios).sortBy('executionStatus')
            .reverse().value();
        }
      },
      {
        selected: false,
        id: 'duration-asc',
        name: 'Fastest',
        apply: function (scenarios) {
          return _.sortBy(scenarios, 'durationInNanos');
        }
      },
      {
        selected: false,
        id: 'duration-desc',
        name: 'Slowest',
        apply: function (scenarios) {
          return _.chain(scenarios).sortBy('durationInNanos')
            .reverse().value();
        }
      }

    ];

    return result.concat(getTagSortOptions(uniqueSortedTags))
  }

  function getTagSortOptions (uniqueSortedTags) {
    var result = [];

    var tagTypes = groupTagsByType(uniqueSortedTags);

    _.forEach(tagTypes, function (tagType) {
      if (tagType.tags.length > 1) {
        result.push({
          selected: false,
          name: tagType.type,
          apply: function (scenarios) {
            return _.sortBy(scenarios, function (scenario) {
              var x = getTagOfType(scenario.tags, tagType.type)[0];
              return x ? x.value : undefined;
            });
          }
        });
      }
    });

    return result;
  }

  function getTagOfType (tags, type) {
    return _.filter(tags, function (tag) {
      return getTagName(tag) === type;
    });
  }

  function toArrayOfGroups (obj) {
    var result = [];
    _.forEach(ownProperties(obj), function (p) {
      result.push({
        name: p,
        values: obj[p],
        counts: countFailedAndPending(obj[p])
      });
    });
    return _.sortBy(result, 'name');
  }

  function countFailedAndPending (scenarios) {
    var counts = {
      failed: 0,
      pending: 0,
      durationInNanos: 0
    };
    _.forEach(scenarios, function (scenario) {
      if (scenario.executionStatus === 'FAILED') {
        counts.failed++;
      } else if (scenario.executionStatus !== 'SUCCESS') {
        counts.pending++;
      }
      counts.durationInNanos += scenario.durationInNanos;
    });
    return counts;
  }

  function getOptionsFromSearch (search) {
    var result = {};
    result.page = parseInt(search.page) || 1;
    result.itemsPerPage = parseInt(search.itemsPerPage) || 40;
    result.sort = search.sort;
    result.group = search.group;
    result.tags = search.tags ? search.tags.split(";") : [];
    result.classes = search.classes ? search.classes.split(";") : [];
    result.status = search.status ? search.status.split(";") : [];
    return result;
  }


  return {
    getOptions: getOptions,
    getOptionsFromSearch: getOptionsFromSearch,
    getDefaultOptions: getDefaultOptions

  };

}])
;