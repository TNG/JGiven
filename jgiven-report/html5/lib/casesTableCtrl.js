jgivenReportApp.controller('CasesTableCtrl', function ($scope) {
  var self = this;

  $scope.columns = initializeColumns($scope.scenario);
  $scope.groupColumns = getGroupColumns($scope.columns);
  $scope.cases = $scope.scenario.scenarioCases;
  $scope.groups = allGroup($scope.cases);
  $scope.sortColumn = $scope.columns[0];
  $scope.groupColumn = undefined;

  function allGroup (cases) {
    return [{
      hide: true,
      name: 'All',
      cases: cases,
      expanded: true
    }]
  }

  function getGroupColumns (columns) {
    return _.filter(columns, function (col) {
      return col.canGroup;
    });
  };

  function initializeColumns (scenario) {
    var columns = [];

    columns.push({
      name: '#',
      sorting: 'desc',
      canGroup: false,
      getValue: function (aCase) {
        return aCase.caseNr;
      }
    });

    if (scenario.scenarioCases[0].description) {
      columns.push({
        name: 'Description',
        canGroup: true,
        getValue: function (aCase) {
          return aCase.description
        }
      })
    }

    _.forEach(scenario.derivedParameters, function (param, index) {
      columns.push({
        name: param,
        canGroup: true,
        getValue: function (aCase) {
          return aCase.derivedArguments[index];
        }
      })
    });

    columns.push({
      name: 'Status',
      canGroup: true,
      isStatus: true,
      getValue: function (aCase) {
        return aCase.success;
      }
    });

    return columns;
  }

  $scope.changeGrouping = function (col) {
    var oldGrouping = col.grouping;
    _.forEach($scope.columns, function (c) {
      c.grouping = undefined;
    });
    col.grouping = !oldGrouping

    if (!col.grouping) {
      $scope.groupColumn = undefined;
      $scope.groups = allGroup($scope.cases);
    } else {
      $scope.groupColumn = col;
      $scope.groups = group(col, $scope.cases);
    }

    applySorting($scope.sortColumn, $scope.groups);
  };

  function group (col, cases) {
    var sortedByGroupValue = sort(col, cases);

    var groups = [];

    var group = {
      name: undefined
    };

    _.forEach(sortedByGroupValue, function (aCase) {
      var value = col.getValue(aCase);
      if (group.name !== value) {
        group = {
          name: value,
          cases: []
        };
        groups.push(group);
      }
      group.cases.push(aCase);
    });
    return groups;
  }

  $scope.changeSorting = function (col) {
    var oldSorting = col.sorting;

    _.forEach($scope.columns, function (c) {
      c.sorting = undefined;
    });

    col.sorting = oldSorting === 'desc' ? 'asc' : 'desc';
    $scope.sortColumn = col;
    applySorting(col, $scope.groups);
  };

  function applySorting (col, groups) {
    _.forEach(groups, function (group) {
      group.cases = sort(col, group.cases)
    })
  };

  function sort (col, cases) {
    var sorted = _.sortBy(cases, function (aCase) {
      return col.getValue(aCase);
    });

    if (col.sorting === 'asc') {
      return sorted.reverse();
    } else {
      return sorted;
    }
  }

  $scope.expandGroups = function () {
    setExpanded($scope.groups, true);
  };

  $scope.collapseGroups = function () {
    setExpanded($scope.groups, false);
  };

  function setExpanded (groups, value) {
    _.forEach(groups, function (group) {
      group.expanded = value;
    })
  }

})
;

