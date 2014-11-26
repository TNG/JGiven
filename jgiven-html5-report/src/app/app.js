var jgivenReportApp = angular.module('jgivenReportApp', ['ngSanitize','mm.foundation']);


jgivenReportApp.controller('JGivenReportCtrl', function ($scope, $rootScope, $timeout, $sanitize) {
  $scope.scenarios = [];
  $scope.classNames = getClassNames();
  $scope.tagScenarioMap = {}; // lazy calculated by getTags()
  $scope.allTags = groupTagsByType(getTags());
  $scope.tags = $scope.allTags;
  $scope.currentPage;

  $scope.updateCurrentPage = function (index) {
      console.log("updateCurrentPage "+ index);
      var className = splitClassName(allScenarios[index].className);
      $scope.currentPage = {
          scenarios: sortByDescription(allScenarios[index].scenarios),
          subtitle: className.packageName,
          title: className.className,
          breadcrumbs: className.packageName.split(".")
      };
  };

  $scope.updateCurrentPageToTag = function(tag) {
      var key = getTagKey(tag);
      $scope.currentPage = {
          scenarios: sortByDescription( $scope.tagScenarioMap[key] ),
          title: tag.value ? tag.value : tag.name,
          subtitle: tag.value ? tag.name : undefined,
          description: tag.description,
          breadcrumbs: ['TAGS',tag.name,tag.value]
      };
  };

  $scope.showFailedScenarios = function() {
      $scope.currentPage = {
          scenarios: getFailedScenarios(),
          title: "Failed Scenarios",
          description: "All failed scenarios",
          breadcrumbs: ['FAILED SCENARIOS']
      };
  };

  $scope.toggleScenario = function(nr, scenario) {
      console.log("toggle "+nr);

      scenario.expanded = !scenario.expanded;
  };

  $scope.updateNav = function() {
      $scope.classNames = _.filter(getClassNames(), function(x) {
          if ($scope.navsearch) {
              return x.className.match( new RegExp($scope.navsearch, "i"));
          }
          return true;
      });

      $scope.tags = _.filter($scope.allTags, function(x) {
          if ($scope.navsearch) {
              return (x.name + '-' + x.value).match( new RegExp($scope.navsearch, "i") );
          }
          return true;
      });
  };

  $scope.updateToolTips = function() {
      $rootScope.$on('$viewContentLoaded', function () {
          console.log('loaded!');
          console.log($('.has-tip').length, '  Num tips');// likely zero as ng-repeat hasn't completed

          $timeout(function () {
              $(document).foundation();
              console.log($('.has-tip').length, '  Num tips after timeout');  // 5
          }, 300)
      });
  };

  $scope.printCurrentPage = function printCurrentPage() {
      $scope.expandAll();
      $timeout(function() {
          window.print();
      },1000);
  }

  $scope.expandAll = function expandAll() {
      _.forEach($scope.currentPage.scenarios, function(x) {
          x.expanded = true;
      });
  }

  $scope.collapseAll = function collapseAll() {
      _.forEach($scope.currentPage.scenarios, function(x) {
          x.expanded = false;
      });
  }

  function groupTagsByType(tagList) {
      var types = {};
      _.forEach(tagList, function(x) {
          var list = types[x.name];
          if (!list) {
              list = new Array();
              types[x.name] = list;
          }
          list.push(x);
      })
      return _.map(_.sortBy(Object.keys(types), function(key) {
          return key;
      }), function(x) {
         return {
             type: x,
             tags: types[x]
         }
      });
  }

  function getAllScenarios() {
      return _.flatten( _.map( allScenarios, function(x) {
          return x.scenarios;
      }), true);
  }

  function getFailedScenarios() {
      return sortByDescription(_.filter( getAllScenarios(), function(x) {
          return x.executionStatus !== "SUCCESS";
      }));
  }

  function sortByDescription( scenarios ) {
      return _.sortBy(scenarios, function(x) {
          return x.description;
      });
  }


  function getClassNames() {
      var res = new Array();
      for (var i = 0; i < allScenarios.length; i++ ) {
          var className = splitClassName( allScenarios[i].className );
          className.index = i;
          res.push(className);
      }
      return _.sortBy( res, function(x) { return x.className; });
  }

  function getTagKey(tag) {
      return tag.name + '-' + tag.value;
  }

  function getTags() {
      var res = {};
      var key;
      var scenarioList;
      _.forEach(allScenarios, function(testCase) {
          _.forEach(testCase.scenarios, function(scenario) {
              _.forEach(scenario.tags, function(tag) {
                  key = getTagKey(tag);
                  res[ key ] = tag;
                  scenarioList = $scope.tagScenarioMap[ key ];
                  if (!scenarioList) {
                      scenarioList = new Array();
                      $scope.tagScenarioMap[ key ] = scenarioList;
                  }
                  scenarioList.push(scenario);
              });
          });
      });

      return _.sortBy(_.values(res), getTagKey);
  }

  $scope.nanosToSeconds = function( nanos ) {
      var secs = nanos / 1000000000;
      var res = parseFloat(secs).toFixed(2);
      console.log(res);
      return res;
  }
});

String.prototype.capitalize = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
}

function splitClassName( fullQualifiedClassName ) {
    var index = fullQualifiedClassName.lastIndexOf('.');
    var className = fullQualifiedClassName.substr(index+1);
    var packageName = fullQualifiedClassName.substr(0,index);
    return {
        className: className,
        packageName: packageName
    };
}
