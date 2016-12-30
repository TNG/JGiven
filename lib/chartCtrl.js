import 'angular-chart.js'
import { defaults } from 'chart.js'

jgivenReportApp.controller('ChartCtrl', function ($scope, $location) {
  var red = 'rgba(255,0,0,1)';
  var gray = 'rgba(150,150,150,1)';

  var green = {
    fillColor: 'rgba(0,150,0,0.5)',
    strokeColor: 'rgba(0,150,0,0.7)',
    pointColor: "rgba(0,150,0,1)",
    pointStrokeColor: "#fff",
    pointHighlightFill: "#fff",
    pointHighlightStroke: "rgba(0,150,0,0.8)"
  };

  $scope.click = function (event) {
    if (event[0].label === 'Failed') {
      $scope.showFailed();
    } else if (event[0].label === 'Pending') {
      $scope.showPending();
    } else {
      $scope.showSuccessful();
    }
  };

  $scope.labels = ['Successful', 'Failed', 'Pending'];
  $scope.colors = ['#00aa00', '#cc0000', '#888888'];
  $scope.options = {
    percentageInnerCutout: 60,
    animationEasing: "easeInOutCubic",
    animationSteps: 50,
    segmentShowStroke: false
  };

})
;
