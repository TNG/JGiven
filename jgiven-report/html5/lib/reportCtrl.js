/**
 * Main controller
 */



jgivenReportApp.controller('JGivenReportCtrl', function ($scope, $rootScope, $document, $timeout, $sanitize, $location, $window, localStorageService,
                                                         dataService, tagService, classService, searchService, optionService) {

  /**
   * The list of all scenarios theoretically visible
   */
  var scenarios = [];

  /**
   * Scenarios after an options filter has been applied
   */
  var filteredScenarios = [];

  $scope.currentPage = {};
  $scope.nav = {};
  $scope.bookmarks = [];

  $scope.totalStatistics = undefined;

  $scope.metaData = dataService.getMetaData();

  // Pager

  $scope.totalItems = 0;
  $scope.currentPageNr = 1;
  $scope.itemsPerPage = 0;

  $scope.showOptions = true;

  $scope.customNavigationLinks = dataService.getCustomNavigationLinks();

  $scope.init = function () {

    $scope.bookmarks = localStorageService.get('bookmarks') || [];
    $scope.$watch('bookmarks', function () {
      localStorageService.set('bookmarks', $scope.bookmarks);
    }, true);

    $scope.showSummaryPage();
  };

  var getAllScenarios = dataService.getAllScenarios;

  $scope.showSummaryPage = function () {

    $scope.currentPage = {
      title: "Welcome",
      breadcrumbs: [''],
      scenarios: [],
      groupedScenarios: [],
      statistics: $scope.getTotalStatistics(),
      summary: true
    };
  };

  $scope.$on('$locationChangeSuccess', function (event) {
    if ($scope.updatingLocation) {
      $scope.updatingLocation = false;
      return;
    }
    var search = $location.search();
    var selectedOptions = optionService.getOptionsFromSearch(search);
    var part = $location.path().split('/');
    console.log("Location change: " + part);
    $scope.currentPageNr = selectedOptions.page;
    $scope.itemsPerPage = selectedOptions.itemsPerPage;
    $scope.pagination = false;
    $scope.showOptions = false;
    if (part[1] === '') {
      $scope.showSummaryPage();
    } else if (part[1] === 'tag') {
      var tag = tagService.getTagByKey(getTagKey({
        name: part[2],
        value: part[3]
      }));

      if (tag) {
        $scope.updateCurrentPageToTag(tag, selectedOptions);
      } else {
        var tagNameNode = tagService.getTagNameNode(part[2]);
        $scope.updateCurrentPageToTagNameNode(tagNameNode, selectedOptions);
      }
    } else if (part[1] === 'tagid') {
      var tag = tagService.getTagByKey(getTagKey({
          fullType: part[2],
          value: part[3]
      }));
      $scope.updateCurrentPageToTag(tag, selectedOptions);
    } else if (part[1] === 'class') {
      $scope.updateCurrentPageToClassName(part[2], selectedOptions);
    } else if (part[1] === 'package') {
      $scope.updateCurrentPageToPackage(part[2], selectedOptions);
    } else if (part[1] === 'scenario') {
      $scope.showScenario(part[2], part[3], selectedOptions);
    } else if (part[1] === 'all') {
      $scope.showAllScenarios(selectedOptions);
    } else if (part[1] === 'failed') {
      $scope.showFailedScenarios(selectedOptions);
    } else if (part[1] === 'pending') {
      $scope.showPendingScenarios(selectedOptions);
    } else if (part[1] === 'search') {
      $scope.search(part[2], selectedOptions);
    }


    $scope.currentPage.embed = search.embed;
    $scope.currentPage.print = search.print;

  });

  $scope.setPage = function (pageNo) {
    $scope.currentPageNr = pageNo;
    $scope.applyOptionalPagination();
  };

  $scope.getTotalStatistics = function () {
    if (!$scope.totalStatistics) {
      $scope.totalStatistics = $scope.gatherStatistics(getAllScenarios());
    }
    return $scope.totalStatistics;
  };

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
        url: $window.location.hash,
        search: $window.location.search
      });
    }
  };

  $scope.removeCurrentBookmark = function () {
    $scope.removeBookmark($scope.findBookmarkIndex());
  };

  $scope.removeBookmark = function (index) {
    $scope.bookmarks.splice(index, 1);
  };

  $scope.isBookmarked = function () {
    return $scope.findBookmarkIndex() !== -1;
  };

  $scope.findBookmarkIndex = function () {
    for (var i = 0; i < $scope.bookmarks.length; i++) {
      if ($scope.bookmarks[i].url === $location.path()) {
        return i;
      }
    }
    return -1;
  };

  $scope.toggleTreeNode = function toggleTreeNode (node) {
    node.expanded = !node.expanded;

    // recursively open all packages that only have a single subpackage
    if (node.leafs().length === 0 && node.childNodes().length === 1) {
      toggleTreeNode(node.childNodes()[0]);
    }
  };

  $scope.currentPath = function () {
    return $location.path();
  };

  $scope.updateCurrentPageToClassName = function (className, options) {
    $scope.updateCurrentPageToTestCase(classService.getTestCaseByClassName(className), options);
  };

  $scope.updateCurrentPageToPackage = function (packageName, options) {
    $scope.currentPage = {
      scenarios: [],
      subtitle: "Package",
      title: packageName,
      breadcrumbs: packageName.split("."),
      loading: true
    };
    $timeout(function () {
      scenarios = classService.getScenariosOfPackage(packageName);
      $scope.currentPage.loading = false;
      $scope.currentPage.options = optionService.getOptions(scenarios, options);
      $scope.applyOptions();
    }, 0);
  };

  $scope.updateCurrentPageToTestCase = function (testCase, options) {
    var className = splitClassName(testCase.className);
    scenarios = sortByDescription(testCase.scenarios);

    $scope.currentPage = {
      subtitle: className.packageName + (testCase.name ? '.' + className.className : ''),
      title: testCase.name ? testCase.name : className.className,
      description: testCase.description,
      breadcrumbs: className.packageName.split("."),
      options: optionService.getOptions(scenarios, options)
    };
    $scope.applyOptions();
  };

  $scope.updateCurrentPageToTag = function (tag, options) {
    var key = getTagKey(tag);
    scenarios = sortByDescription(tagService.getScenariosByTag(tag));
    console.log("Update current page to tag " + key);
    $scope.currentPage = {
      title: tag.value ? (tag.prependType ? getTagName(tag) + '-' : '') + tag.value : getTagName(tag),
      subtitle: tag.value && !tag.prependType ? getTagName(tag) : undefined,
      description: tag.description,
      breadcrumbs: ['TAGS', getTagName(tag), tag.value],
      options: optionService.getOptions(scenarios, options)
    };
    $scope.applyOptions();
  };

  $scope.updateCurrentPageToTagNameNode = function (tagNameNode, options) {
    scenarios = sortByDescription(tagNameNode.scenarios());
    $scope.currentPage = {
      title: tagNameNode.nodeName(),
      description: "",
      breadcrumbs: ['TAGS', tagNameNode.nodeName()],
      options: optionService.getOptions(scenarios, options)
    };
    $scope.applyOptions();
  };

  $scope.showScenario = function (className, methodName, options) {
    scenarios = sortByDescription(_.filter(classService.getTestCaseByClassName(className).scenarios, function (x) {
      return x.testMethodName === methodName;
    }));
    $scope.currentPage = {
      title: scenarios[0].description.capitalize(),
      subtitle: className,
      description: scenarios[0].extendedDescription,
      breadcrumbs: ['SCENARIO'].concat(className.split('.')).concat([methodName]),
      options: optionService.getOptions(scenarios, options)
    };
    $scope.applyOptions();
  };

  $scope.showAllScenarios = function (options) {
    $scope.currentPage = {
      scenarios: [],
      title: 'All Scenarios',
      breadcrumbs: ['ALL SCENARIOS'],
      loading: true
    };

    $timeout(function () {
      scenarios = sortByDescription(getAllScenarios());
      $scope.currentPage.loading = false;
      $scope.currentPage.options = optionService.getOptions(scenarios, options);
      $scope.applyOptions();
    }, 0);
  };

  $scope.showPendingScenarios = function (options) {
    scenarios = dataService.getPendingScenarios();
    var description = getDescription(scenarios.length, "pending");
    $scope.currentPage = {
      title: "Pending Scenarios",
      description: description,
      breadcrumbs: ['PENDING SCENARIOS'],
      options: optionService.getOptions(scenarios, options)
    };
    $scope.applyOptions();
  };

  $scope.applyOptions = function applyOptions () {
    var page = $scope.currentPage;
    var selectedSortOption = getSelectedSortOption(page);

    filteredScenarios = selectedSortOption.apply(
      _.filter(scenarios, getFilterFunction(page)));

    $scope.showOptions = scenarios.length > 1;

    $scope.applyOptionalPagination();
  };

  $scope.applyOptionalPagination = function () {
    var page = $scope.currentPage;
    $scope.totalItems = filteredScenarios.length;

    var scenariosOfCurrentPage = filteredScenarios;
    var groupOption = getSelectedGroupOption(page);
    if (groupOption.name === 'None' && $scope.totalItems > $scope.itemsPerPage) {
      $scope.pagination = true;
      scenariosOfCurrentPage = $scope.applyPagination(filteredScenarios);
    } else {
      $scope.pagination = false;
    }

    console.time("Applying group option");
    page.groupedScenarios = groupOption.apply(scenariosOfCurrentPage);
    console.timeEnd("Applying group option");

    page.statistics = $scope.gatherStatistics(filteredScenarios);
    page.filtered = scenarios.length - filteredScenarios.length;
    $scope.updateLocationSearchOptions();
  };

  $scope.applyPagination = function applyPagination (filteredScenarios) {
    var start = ($scope.currentPageNr - 1) * $scope.itemsPerPage;
    return filteredScenarios.slice(start, start + $scope.itemsPerPage);
  };

  $scope.updateLocationSearchOptions = function updateLocationSearchOptions () {
    $scope.updatingLocation = true;
    var selectedSortOption = getSelectedSortOption($scope.currentPage);
    $location.search('sort', selectedSortOption.default ? null : selectedSortOption.id);

    var selectedGroupOption = getSelectedGroupOption($scope.currentPage);
    $location.search('group', selectedGroupOption.default ? null : selectedGroupOption.id);

    var selectedTags = getSelectedOptions($scope.currentPage.options.tagOptions);
    $location.search('tags', selectedTags.length > 0 ? _.map(selectedTags, 'name').join(";") : null);

    var selectedStatus = getSelectedOptions($scope.currentPage.options.statusOptions);
    $location.search('status', selectedStatus.length > 0 ? _.map(selectedStatus, 'id').join(";") : null);

    var selectedClasses = getSelectedOptions($scope.currentPage.options.classOptions);
    $location.search('classes', selectedClasses.length > 0 ? _.map(selectedClasses, 'name').join(";") : null);

    $location.search('page', $scope.pagination ? $scope.currentPageNr : null);

    $location.search('itemsPerPage', $scope.pagination ? $scope.itemsPerPage : null);

    $scope.updatingLocation = false;
  };

  $scope.showFailedScenarios = function (options) {
    scenarios = dataService.getFailedScenarios();
    var description = getDescription(scenarios.length, "failed");
    $scope.currentPage = {
      title: "Failed Scenarios",
      description: description,
      breadcrumbs: ['FAILED SCENARIOS'],
      options: optionService.getOptions(scenarios, options)
    };
    $scope.applyOptions();
  };

  function getDescription (count, status) {
    if (count === 0) {
      return "There are no " + status + " scenarios. Keep rocking!";
    } else if (count === 1) {
      return "There is only 1 " + status + " scenario. You nearly made it!";
    } else {
      return "There are " + count + " " + status + " scenarios";
    }
  }

  $scope.toggleTagType = function (tagType) {
    tagType.collapsed = !tagType.collapsed;
  };

  $scope.toggleScenario = function (scenario) {
    scenario.expanded = !scenario.expanded;
  };

  $scope.searchSubmit = function () {
    console.log("Searching for " + $scope.nav.search);

    $location.path("search/" + $scope.nav.search);
  };

  $scope.search = function search (searchString, options) {
    console.log("Searching for " + searchString);

    $scope.currentPage = {
      scenarios: [],
      title: "Search Results",
      description: "Searched for '" + searchString + "'",
      breadcrumbs: ['Search', searchString],
      loading: true
    };

    $timeout(function () {
      scenarios = searchService.findScenarios(searchString);
      $scope.currentPage.loading = false;
      $scope.currentPage.options = optionService.getOptions(scenarios, options);
      $scope.applyOptions();
    }, 1);
  };

  $scope.gatherStatistics = function gatherStatistics (scenarios) {
    var statistics = {
      count: scenarios.length,
      failed: 0,
      pending: 0,
      success: 0,
      totalNanos: 0
    };

    _.forEach(scenarios, function (x) {
      statistics.totalNanos += x.durationInNanos;
      if (x.executionStatus === 'SUCCESS') {
        statistics.success++;
      } else if (x.executionStatus === 'FAILED') {
        statistics.failed++;
      } else {
        statistics.pending++;
      }
    });

    $timeout(function () {
      statistics.chartData = [statistics.success, statistics.failed, statistics.pending];
    }, 0);

    return statistics;
  };

  $scope.printCurrentPage = function printCurrentPage () {
    $location.search("print", true);
    $timeout(printPage, 0);
  };

  function printPage () {
    if ($scope.currentPage.loading) {
      $timeout(printPage, 0);
    } else {
      window.print();
      $timeout(function () {
        $location.search("print", null);
      }, 0);
    }
  }

  $scope.expandAll = function expandAll () {
    _.forEach(scenarios, function (x) {
      x.expanded = true;
    });
  };

  $scope.collapseAll = function collapseAll () {
    _.forEach(scenarios, function (x) {
      x.expanded = false;
    });
  };

  $scope.sortOptionSelected = function sortOptionSelected (sortOption) {
    deselectAll($scope.currentPage.options.sortOptions);
    sortOption.selected = true;
    $scope.applyOptions();
  };

  $scope.groupOptionSelected = function groupOptionSelected (groupOption) {
    deselectAll($scope.currentPage.options.groupOptions);
    groupOption.selected = true;
    $scope.applyOptions();
  };

  $scope.filterOptionSelected = function filterOptionSelected (filterOption) {
    filterOption.selected = !filterOption.selected;
    $scope.applyOptions();
  };

  function getFilterFunction (page) {

    var anyStatusMatches = anyOptionMatches(getSelectedOptions(page.options.statusOptions));
    var anyTagMatches = allOptionMatches(getSelectedOptions(page.options.tagOptions));
    var anyClassMatches = anyOptionMatches(getSelectedOptions(page.options.classOptions));

    return function (scenario) {
      return anyStatusMatches(scenario) && anyTagMatches(scenario) && anyClassMatches(scenario);
    }
  }

  function anyOptionMatches (filterOptions) {
    // by default nothing is filtered
    if (filterOptions.length === 0) {
      return function () {
        return true;
      };
    }

    return function (scenario) {
      for (var i = 0; i < filterOptions.length; i++) {
        if (filterOptions[i].apply(scenario)) {
          return true;
        }
      }
      return false;
    }
  }

  function allOptionMatches (filterOptions) {
    // by default nothing is filtered
    if (filterOptions.length === 0) {
      return function () {
        return true;
      };
    }

    return function (scenario) {
      for (var i = 0; i < filterOptions.length; i++) {
        if (!filterOptions[i].apply(scenario)) {
          return false;
        }
      }
      return true;
    }
  }

  function getSelectedSortOption (page) {
    return getSelectedOptions(page.options.sortOptions)[0];
  }

  function getSelectedGroupOption (page) {
    return getSelectedOptions(page.options.groupOptions)[0];
  }

  function getSelectedOptions (options) {
    return _.filter(options, 'selected');
  }

  $scope.nanosToReadableUnit = nanosToReadableUnit;
  $scope.getWordValue = getWordValue;

  $scope.tagIdToString = function tagIdToString (tagId) {
    var tag = tagService.getTagByTagId(tagId);
    return tagToString(tag);
  };

  $scope.tagToString = tagToString;

  $scope.getUrlFromTagId = function getUrlFromTagId (tagId) {
    var tag = $scope.getTagByTagId(tagId);
    return $scope.getUrlFromTag(tag);
  };

  $scope.getUrlFromTag = function getUrlFromTag (tag) {
    if (tag.href) {
      return tag.href;
    }
    return '#tagid/' + getTagId(tag) +
      (tag.value ? '/' + $window.encodeURIComponent(tag.value) : '');
  };

  $scope.getTagByTagId = function (tagId) {
    return tagService.getTagByTagId(tagId);
  };

  $scope.getCssClassOfTag = function getCssClassOfTag (tagId) {
    var tag = $scope.getTagByTagId(tagId);
    if (tag.cssClass) {
      return tag.cssClass;
    }
    return 'tag-' + getTagName(tag);
  };

  /**
   * Returns the content of style attribute for the given tag
   */
  $scope.getStyleOfTag = function getStyleOfTag (tagId) {
    var tag = tagService.getTagByTagId(tagId);
    var style = "";
    if (tag.style) {
      style = tag.style;
    }
    if (tag.color) {
      style += ' background-color: ' + tag.color;
    }
    return style;
  };

  $scope.getScenarioTitleStatusClass = function (scenario) {
    switch (scenario.executionStatus) {
      case 'SUCCESS':
        return '';
      case 'FAILED':
        return 'failed';
      default:
        return 'pending';
    }
  };

  $scope.getNumberOfFailedCases = function (scenario) {
    var nCases = scenario.scenarioCases.length;
    if (nCases === 1) {
      return '';
    }

    var failedCases = 0;

    _.forEach(scenario.scenarioCases, function (aCase) {
      if (!aCase.success) {
        failedCases++;
      }
    });

    if (failedCases < nCases) {
      return " " + failedCases + " OF " + nCases + " CASES ";
    } else {
      return " ALL CASES";
    }
  };

  $scope.getScenarioCaseTitleStatusClass = function (scenarioCase) {
    return scenarioCase.success ? '' : 'failed';
  };

  $scope.isHeaderCell = function (rowIndex, columnIndex, headerType) {
    if (rowIndex === 0 && (headerType === 'HORIZONTAL' || headerType === 'BOTH')) {
      return true;
    }
    return columnIndex === 0 && (headerType === 'VERTICAL' || headerType === 'BOTH');

  };

  /**
   * Returns all but the intro words of the given array of words.
   * It is assumed that only the first word can be an intro word
   * @param words the array of all non-intro words of a step
   */
  $scope.getNonIntroWords = function getNonIntroWords (words) {
    if (words[0].isIntroWord) {
      return words.slice(1);
    }
    return words;
  };

  $scope.showFailed = function () {
    if ($location.path() === '/' || $location.path() === '') {
      $location.path('/failed');
    } else {
      $location.search('status', 'fail');
    }
  };

  $scope.showPending = function () {
    if ($location.path() === '/' || $location.path() === '') {
      $location.path('/pending');
    } else {
      $location.search('status', 'pending');
    }
  };

  $scope.showSuccessful = function () {
    if ($location.path() === '/' || $location.path() === '') {
      $location.path('/all');
    }
    $location.search('status', 'success');

  };

  $scope.clearFilter = function () {
    if ($location.path() === '/') {
      $location.path('/all');
    } else {
      $location.search('status', null);
      $location.search('tags', null);
      $location.search('classes', null);
    }

  };

  $scope.init();

});
