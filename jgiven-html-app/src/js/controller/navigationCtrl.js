/**
 * AngularJS controller to handle the navigation tree on the left
 */


jgivenReportApp.controller('JGivenNavigationCtrl', function ($scope, $document, $window, classService, tagService) {
  'use strict';

  var scenarioContainer = $('#scenario-container');

  var navWidthBeforeResize;
  var pageXBeforeResize;
  /**
   * The root tag node of the hierarchical tag tree
   */
  $scope.rootTags = tagService.getRootTags();

  $scope.navHidden = false;

  $scope.summaryExpanded = true;
  $scope.tagsExpanded = true;
  $scope.classesExpanded = true;
  $scope.bookarksExpanded = true;

  /**
   * The root package node of the hierarchical package tree
   */
  $scope.rootPackage = classService.getRootPackage();

  adaptResizeHandle();
  $(window).resize(adaptResizeHandle);

  $scope.orderNodes = function (node) {
    return node.nodeName();
  };

  $scope.hideNav = function () {
    $scope.navHidden = true;
    scenarioContainer.previousLeftMargin = parseInt(scenarioContainer.css("margin-left"));
    scenarioContainer.css("margin-left", "0");
  };

  $scope.showNav = function () {
    $scope.navHidden = false;
    scenarioContainer.css("margin-left", scenarioContainer.previousLeftMargin);
    $scope.navStyle = scenarioContainer.previousLeftMargin;
    $('#sidebar').css("visible", "true");
    $('#sidebar').css("width", scenarioContainer.previousLeftMargin);
  };

  function adaptResizeHandle () {
    $('#nav-move-icon-container').css("top", ($(window).height() - parseInt($('#sidebar').css("top"))) / 2);
  }

  function resizeNav (event) {
    var widthDiff = pageXBeforeResize - event.pageX;
    var newWidth = navWidthBeforeResize - widthDiff;
    if (newWidth < 20) {
      newWidth = 20;
    }
    scenarioContainer.css("margin-left", newWidth);
    $('#sidebar').css("width", newWidth);
  };

  $scope.startResizeNav = function (event) {
    event.preventDefault();
    console.log("start resize " + event.pageX);
    navWidthBeforeResize = parseInt(scenarioContainer.css("margin-left"));
    pageXBeforeResize = event.pageX;
    $document.on("mousemove", resizeNav);
    $document.on("mouseup", finishResizeNav);
  };

  function finishResizeNav () {
    $document.unbind('mousemove', resizeNav);
    $document.unbind('mouseup', finishResizeNav);
  };

});
