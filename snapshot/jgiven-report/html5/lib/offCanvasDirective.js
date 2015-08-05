/**
 * Implements multi-level menu which is not implemented yet in angular-foundation
 */

jgivenReportApp.directive('hasSubmenu', [function () {
  return {
    require: '^offCanvasWrap',
    restrict: 'C',
    link: function ($scope, element, attrs) {
      element.on('click', function (e) {
        e.stopPropagation();
        angular.element(this.getElementsByClassName('left-submenu')[0]).addClass('move-right');
        angular.element(this.getElementsByClassName('right-submenu')[0]).addClass('move-left');
      });
    }
  };
}]);


jgivenReportApp.directive('offCanvasBack', [function () {
  return {
    restrict: 'C',
    link: function ($scope, element) {
      element.on('click', function (e) {
        e.stopPropagation();
        angular.element(this.parentElement).removeClass('move-right');
        angular.element(this.parentElement).removeClass('move-left');
      });
    }
  };
}]);

jgivenReportApp.directive('offCanvasClose', [function () {
  return {
    require: '^offCanvasWrap',
    restrict: 'C',
    link: function ($scope, element, attrs, offCanvasWrap) {
      element.on('click', function (e) {
        e.stopPropagation();
        offCanvasWrap.hide();
      });
    }
  };
}]);

jgivenReportApp.directive('offCanvasCloseOnSubmit', [function () {
  return {
    require: '^offCanvasWrap',
    restrict: 'C',
    link: function ($scope, element, attrs, offCanvasWrap) {
      element.on('click', function (e) {
        e.stopPropagation();
      });
      element.on('submit', function (e) {
        e.stopPropagation();
        offCanvasWrap.hide();
      });
    }
  };
}]);
