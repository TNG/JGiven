/**
 * AngularJS controller to handle the navigation tree on the left
 */


jgivenReportApp.controller('JGivenNavigationCtrl', function ($scope, classService, tagService) {
  'use strict';

  /**
   * The root tag node of the hierarchical tag tree
   */
  $scope.rootTags = tagService.getRootTags();

  /**
   * The root package node of the hierarchical package tree
   */
  $scope.rootPackage = classService.getRootPackage();

  $scope.orderNodes = function (node) {
    return node.nodeName();
  };

});
