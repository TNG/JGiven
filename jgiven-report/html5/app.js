'use strict';

var jgivenReportApp = angular.module('jgivenReportApp', ['ngSanitize','mm.foundation','mm.foundation.offcanvas',
  'chart.js','LocalStorageModule'])
  .config(['localStorageServiceProvider', function(localStorageServiceProvider){
    localStorageServiceProvider.setPrefix('jgiven');
  }])



jgivenReportApp.filter('encodeUri', function ($window) {
    return $window.encodeURIComponent;
});

jgivenReportApp.controller('JGivenReportCtrl', function ($scope, $rootScope, $timeout, $sanitize, $location, localStorageService) {
  $scope.scenarios = [];
  $scope.classNameScenarioMap = {};
  $scope.classNames;
  $scope.tagScenarioMap = {}; // lazy calculated by getTags()
  $scope.allTags;
  $scope.tags;
  $scope.currentPage;
  $scope.jgivenReport = jgivenReport;
  $scope.nav = {};
  $scope.bookmarks = [];

  $scope.init = function() {
      $scope.classNames = getClassNames();
      $scope.allTags = groupTagsByType(getTags());
      $scope.tags = $scope.allTags;

      $scope.bookmarks = localStorageService.get('bookmarks') || [];
      $scope.$watch('bookmarks', function () {
        localStorageService.set('bookmarks', $scope.bookmarks);
      }, true);

      $scope.showSummaryPage();
  };

  $scope.showSummaryPage = function() {
      var scenarios = getAllScenarios();

      $scope.currentPage = {
          title: "Welcome",
          breadcrumbs: [''],
          scenarios: [],
          statistics: $scope.gatherStatistics(scenarios),
          summary: true
      };

  }

  $scope.$on('$locationChangeSuccess', function(event) {
      var part = $location.path().split('/');
      console.log("Location change: " +part);
      if (part[1] === '') {
          $scope.showSummaryPage();
      } else if (part[1] === 'tag') {
         $scope.updateCurrentPageToTag( $scope.tagScenarioMap[ getTagKey({
             name: part[2],
             value: part[3]
         })].tag);
      } else if (part[1] === 'class') {
          $scope.updateCurrentPageToClassName(part[2]);
      } else if (part[1] === 'scenario') {
          $scope.showScenario(part[2],part[3]);
      } else if (part[1] === 'all') {
          $scope.showAllScenarios();
      } else if (part[1] === 'failed') {
          $scope.showFailedScenarios();
      } else if (part[1] === 'pending') {
          $scope.showPendingScenarios();
      } else if (part[1] === 'search') {
          $scope.search(part[2]);
      }

      var search = $location.search();

      $scope.currentPage.embed = search.embed;
      $scope.currentPage.print = search.print;

  });

  $scope.toggleBookmark = function () {
    if ($scope.isBookmarked()) {
       $scope.removeCurrentBookmark();
    } else {
      var name = $scope.currentPage.title;
      if (name === 'Search Results') {
        name = $scope.currentPage.description;
      }

      $scope.bookmarks.push({
        name: name,
        url: $location.path()
      });
    }
  };

  $scope.removeCurrentBookmark = function() {
    $scope.removeBookmark( $scope.findBookmarkIndex() );
  };

  $scope.removeBookmark = function (index) {
    $scope.bookmarks.splice(index, 1);
  };

  $scope.isBookmarked = function() {
    return $scope.findBookmarkIndex() !== -1;
  };

  $scope.findBookmarkIndex = function() {
    for (var i = 0; i < $scope.bookmarks.length; i++) {
      if ($scope.bookmarks[i].url === $location.path()) {
        return i;
      }
    }
    return -1;
  }

  $scope.currentPath = function() {
      return $location.path();
  }

  $scope.updateCurrentPageToClassName = function(className) {
      $scope.updateCurrentPageToTestCase( $scope.classNameScenarioMap[className] );
  }

  $scope.updateCurrentPageToTestCase = function (testCase) {
      var className = splitClassName(testCase.className);
      var scenarios = sortByDescription(testCase.scenarios);
      $scope.currentPage = {
          scenarios: scenarios,
          subtitle: className.packageName,
          title: className.className,
          breadcrumbs: className.packageName.split("."),
          statistics: $scope.gatherStatistics( scenarios )
      };
  };

  $scope.updateCurrentPageToTag = function(tag) {
      var key = getTagKey(tag);
      var scenarios = sortByDescription( $scope.tagScenarioMap[key].scenarios );
      console.log("Update current page to tag "+key);
      $scope.currentPage = {
          scenarios: scenarios,
          title: tag.value ? (tag.prependType ? tag.name + '-' : '') + tag.value : tag.name,
          subtitle: tag.value && !tag.prependType ? tag.name : undefined,
          description: tag.description,
          breadcrumbs: ['TAGS',tag.name,tag.value],
          statistics: $scope.gatherStatistics( scenarios )
      };
  };

  $scope.showScenario = function( className, methodName ) {
      var scenarios = sortByDescription(_.filter($scope.classNameScenarioMap[className].scenarios, function(x) {
          return x.testMethodName === methodName;
      }));
      $scope.currentPage = {
          scenarios: scenarios,
          title: methodName,
          subtitle: className,
          breadcrumbs: ['SCENARIO'].concat(className.split('.')).concat([methodName]),
          statistics: $scope.gatherStatistics( scenarios )
      }
  }

  $scope.showAllScenarios = function() {
      $scope.currentPage = {
          scenarios: [],
          title: 'All Scenarios',
          breadcrumbs: ['ALL SCENARIOS'],
          loading: true
      }

      $timeout(function() {
          $scope.currentPage.scenarios = sortByDescription(getAllScenarios());
          $scope.currentPage.loading = false;
          $scope.currentPage.statistics = $scope.gatherStatistics( $scope.currentPage.scenarios );
      }, 0);
  };

  $scope.showPendingScenarios = function() {
      var pendingScenarios = getPendingScenarios();
      var description = getDescription( pendingScenarios.length, "pending");
      $scope.currentPage = {
          scenarios: pendingScenarios,
          title: "Pending Scenarios",
          description: description,
          breadcrumbs: ['PENDING SCENARIOS'],
          statistics: $scope.gatherStatistics( pendingScenarios )
      };
  };

  $scope.showFailedScenarios = function() {
      var failedScenarios = getFailedScenarios();
      var description = getDescription( failedScenarios.length, "failed");
      $scope.currentPage = {
          scenarios: failedScenarios,
          title: "Failed Scenarios",
          description: description,
          breadcrumbs: ['FAILED SCENARIOS'],
          statistics: $scope.gatherStatistics( failedScenarios )
      };
  };

  function getDescription( count, status ) {
      if (count === 0) {
          return "There are no " + status +" scenarios. Keep rocking!";
      } else if (count === 1) {
          return "There is only 1 "+status+" scenario. You nearly made it!";
      } else {
          return "There are " + count + " " + status +" scenarios";
      }
  }

  $scope.toggleTagType = function(tagType) {
      tagType.collapsed = !tagType.collapsed;
  };

  $scope.toggleScenario = function(scenario) {
      scenario.expanded = !scenario.expanded;
  };

  $scope.searchSubmit = function() {
      console.log("Searching for " + $scope.nav.search);

      var x = $location.path("search/" + $scope.nav.search);
  }

  $scope.search = function search(searchString) {
      console.log("Searching for "+searchString);

      $scope.currentPage = {
          scenarios: [],
          title: "Search Results",
          description: "Searched for '" + searchString + "'",
          breadcrumbs: ['Search', searchString ],
          loading: true
      }

      $timeout( function() {
          $scope.currentPage.scenarios = $scope.findScenarios(searchString);
          $scope.currentPage.loading = false;
          $scope.currentPage.statistics = $scope.gatherStatistics($scope.currentPage.scenarios);
      },1);
  }

  $scope.gatherStatistics = function gatherStatistics( scenarios ) {
      var statistics = {
          count: scenarios.length,
          failed: 0,
          pending: 0,
          success: 0,
          totalNanos: 0
      };

      _.forEach( scenarios, function(x) {
          statistics.totalNanos += x.durationInNanos;
          if (x.executionStatus === 'SUCCESS') {
              statistics.success++;
          } else if (x.executionStatus === 'FAILED') {
              statistics.failed++;
          } else {
              statistics.pending++;
          }
      });

      $timeout( function() {
          statistics.chartData = [statistics.success, statistics.failed, statistics.pending];
      }, 0);

      return statistics;
  }

  $scope.findScenarios = function findScenarios( searchString ) {
      var searchStrings = searchString.split(" ");
      console.log("Searching for "+searchStrings);

      var regexps = _.map(searchStrings, function(x) {
          return new RegExp(x, "i");
      } );

      return sortByDescription(_.filter( getAllScenarios(), function(x) {
          return scenarioMatchesAll(x, regexps);
      } ));
  }

  $scope.updateNav = function() {
      $scope.classNames = _.filter(getClassNames(), function(x) {
          if ($scope.nav.search) {
              return x.className.match( new RegExp($scope.nav.search, "i"));
          }
          return true;
      });

      $scope.tags = _.filter($scope.allTags, function(x) {
          if ($scope.nav.search) {
              return (x.name + '-' + x.value).match( new RegExp($scope.nav.search, "i") );
          }
          return true;
      });
  };

  $scope.printCurrentPage = function printCurrentPage() {
      $location.search("print=true");
      $timeout(function() {
          window.print();
          $timeout(function() {
              $location.search("");
          }, 0)
      },0);
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

  function scenarioMatchesAll( scenario, regexpList ) {
      for (var i = 0; i < regexpList.length; i++ ) {

          if (!scenarioMatches(scenario, regexpList[i])) {
              return false;
          }
      }
      return true;
  }

  function scenarioMatches( scenario, regexp ) {
      if (scenario.className.match(regexp)) {
          return true;
      }

      if (scenario.description.match(regexp)) {
          return true;
      }

      for (var i = 0; i < scenario.tags.length; i++) {
          var tag = scenario.tags[i];
          if ( (tag.name && tag.name.match(regexp)) ||
               (tag.value && tag.value.match(regexp))) {
              return true;
          }
      }

      for (var i = 0; i < scenario.scenarioCases.length; i++) {
          if (caseMatches( scenario.scenarioCases[i], regexp )) {
              return true;
          }
      }

  }

  function caseMatches( scenarioCase, regexp) {
      for (var i = 0; i < scenarioCase.steps.length; i++) {
          if (stepMatches(scenarioCase.steps[i], regexp)) {
              return true;
          }
      }

      return false;
  }

  function stepMatches( step, regexp ) {
      for (var i = 0; i < step.words.length; i++) {
          if (step.words[i].value.match(regexp)) {
              return true;
          }
      }

      return false;
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
      return _.flatten( _.map( jgivenReport.scenarios, function(x) {
          return x.scenarios;
      }), true);
  }

  function getPendingScenarios() {
      return getScenariosWhere( function(x) {
          return x.executionStatus !== "FAILED" && x.executionStatus !== "SUCCESS";
      });
  }

  function getFailedScenarios() {
      return getScenariosWhere( function(x) {
          return x.executionStatus === "FAILED";
      });
  }

  function getScenariosWhere( filter ) {
      return sortByDescription(_.filter( getAllScenarios(), filter ));
  }

  function sortByDescription( scenarios ) {
      return _.forEach(_.sortBy(scenarios, function(x) {
          return x.description.toLowerCase();
      }), function(x) {
          x.expanded = false;
      });
  }

  function getClassNames() {
      var res = new Array();
      var allScenarios = jgivenReport.scenarios;
      for (var i = 0; i < allScenarios.length; i++ ) {
          var className = splitClassName( allScenarios[i].className );
          className.index = i;
          res.push(className);
          $scope.classNameScenarioMap[allScenarios[i].className] = allScenarios[i];
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
      var tagEntry;
      _.forEach(jgivenReport.scenarios, function(testCase) {
          _.forEach(testCase.scenarios, function(scenario) {
              _.forEach(scenario.tags, function(tag) {
                  key = getTagKey(tag);
                  res[ key ] = tag;
                  tagEntry = $scope.tagScenarioMap[ key ];
                  if (!tagEntry) {
                      tagEntry = {
                          tag: tag,
                          scenarios: new Array()
                      };
                      $scope.tagScenarioMap[ key ] = tagEntry
                  }
                  tagEntry.scenarios.push(scenario);
              });
          });
      });

      return _.sortBy(_.values(res), getTagKey);
  }

  $scope.nanosToSeconds = function( nanos ) {
      var secs = nanos / 1000000000;
      var res = parseFloat(secs).toFixed(3);
      return res;
  };

  $scope.tagToString = function tagToString(tag) {
      var res = '';

      if (!tag.value || tag.prependType) {
          res = tag.name;
      }

      if (tag.value) {
          if (res) {
              res += '-';
          }
          res += tag.value;
      }

      return res;
  };

  $scope.isHeaderCell = function( rowIndex, columnIndex, headerType ) {
    console.log(headerType);
    if (rowIndex === 0 && (headerType === 'HORIZONTAL' || headerType === 'BOTH')) {
       return true;
    }
    if (columnIndex === 0 && (headerType === 'VERTICAL' || headerType === 'BOTH')) {
       return true;
    }
    return false;
  };

  $scope.init();

});

jgivenReportApp.controller('SummaryCtrl', function ($scope) {
    var red = Chart.defaults.global.colours[2];
    var blue = Chart.defaults.global.colours[0];
    var green = {
        fillColor: 'rgba(0,150,0,0.5)',
        strokeColor: 'rgba(0,150,0,0.7)',
        pointColor: "rgba(0,150,0,1)",
        pointStrokeColor: "#fff",
        pointHighlightFill: "#fff",
        pointHighlightStroke: "rgba(0,150,0,0.8)"
    };
    var gray = Chart.defaults.global.colours[6];

    $scope.labels = ['Successful', 'Failed', 'Pending'];
    $scope.colours = [green, red, gray];
    $scope.options = {
        percentageInnerCutout : 60,
        animationEasing : "easeInOutCubic",
        animationSteps : 50,
        segmentShowStroke: false
    };

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

var jgivenReport = {
    scenarios: new Array(),

    setMetaData: function setMetaData(metaData) {
        this.metaData = metaData;
        _.forEach(metaData.data, function(x) {
            document.writeln("<script src='data/"+x+"'></script>");
        });
    },

    addScenarios: function addScenarios(scenarios) {
       this.scenarios = this.scenarios.concat(scenarios);
    },

    setAllScenarios: function setAllScenarios(allScenarios) {
        this.scenarios = allScenarios;
    }
};
