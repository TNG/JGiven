'use strict';

var jgivenReportApp = angular.module('jgivenReportApp', ['ngSanitize', 'mm.foundation', 'mm.foundation.offcanvas',
  'chart.js', 'LocalStorageModule'])
  .config(['localStorageServiceProvider', function (localStorageServiceProvider) {
    localStorageServiceProvider.setPrefix('jgiven');
  }]);


jgivenReportApp.filter('encodeUri', function ($window) {
  return $window.encodeURIComponent;
});


String.prototype.capitalize = function () {
  return this.charAt(0).toUpperCase() + this.slice(1);
};

Array.prototype.pushArray = function (arr) {
  this.push.apply(this, arr);
};

