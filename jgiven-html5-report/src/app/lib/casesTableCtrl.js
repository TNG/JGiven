jgivenReportApp.controller('CasesTableCtrl', function ($scope) {


  $scope.columns = initializeColumns($scope.scenario);
  $scope.cases = $scope.scenario.scenarioCases;

  function initializeColumns (scenario) {
    var columns = [];

    columns.push({
      name: '#',
      sorting: 'desc',
      getValue: function (aCase) {
        return aCase.caseNr;
      }
    });

    if (scenario.scenarioCases[0].description) {
      columns.push({
        name: 'Description',
        getValue: function (aCase) {
          return aCase.description
        }
      })
    }

    _.forEach(scenario.derivedParameters, function (param, index) {
      columns.push({
        name: param,
        getValue: function (aCase) {
          return aCase.derivedArguments[index];
        }
      })
    });

    columns.push({
      name: 'Status',
      getValue: function (aCase) {
        return aCase.success;
      }
    });

    return columns;
  }

  $scope.changeSorting = function (col) {
    var oldSorting = col.sorting;

    _.forEach($scope.columns, function (c) {
      c.sorting = undefined;
    });

    col.sorting = oldSorting === 'desc' ? 'asc' : 'desc';
    $scope.cases = applySorting(col, $scope.cases)
  }

  function applySorting (col, cases) {
    var sorted = _.sortBy(cases, function (aCase) {
      return col.getValue(aCase);
    });

    if (col.sorting === 'asc') {
      return sorted.reverse();
    } else {
      return sorted;
    }
  }


});

