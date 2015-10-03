jgivenReportApp.controller('ChartCtrl', function ($scope, $location) {
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
  $scope.colours = [green, red, gray];
  $scope.options = {
    percentageInnerCutout: 60,
    animationEasing: "easeInOutCubic",
    animationSteps: 50,
    segmentShowStroke: false
  };

})
;

