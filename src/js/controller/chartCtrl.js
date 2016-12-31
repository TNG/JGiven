import 'angular-chart.js'

jgivenReportApp.controller('ChartCtrl', function ($scope, $timeout) {

  var red = {
    backgroundColor: "rgba(247,70,74,0.5)",
    pointBackgroundColor: "rgba(247,70,74,1)"
  }

  var gray = {
    backgroundColor: "rgba(77,83,96,0.5)",
    pointBackgroundColor: "rgba(77,83,96,1)"
  }

  var green = {
    backgroundColor: 'rgba(0,150,0,0.5)',
    pointBackgroundColor: "rgba(0,150,0,0.8)"
  }

  $scope.click = function (event) {
    $timeout(function () {
      var label = event[0]._model.label
      if (label === 'Failed') {
        $scope.showFailed();
      } else if (label === 'Pending') {
        $scope.showPending();
      } else {
        $scope.showSuccessful();
      }
    }, 0)
  }

  $scope.labels = ['Successful', 'Failed', 'Pending'];
  $scope.colors = [green, red, gray];
  $scope.options = {
    cutoutPercentage: 60,
    animationEasing: "easeInOutCubic",
    animationSteps: 50,
    elements: { arc: { borderWidth: 0 } }
  }

})

