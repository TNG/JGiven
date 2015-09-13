'use strict';

var jgivenReportApp = angular.module('jgivenReportApp', ['ngSanitize', 'mm.foundation', 'mm.foundation.offcanvas',
  'chart.js', 'LocalStorageModule'])
  .config(['localStorageServiceProvider', function (localStorageServiceProvider) {
    localStorageServiceProvider.setPrefix('jgiven');
  }]);

jgivenReportApp.filter('encodeUri', function ($window) {
  return $window.encodeURIComponent;
});


