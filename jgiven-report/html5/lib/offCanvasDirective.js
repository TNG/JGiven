/**
 * Implements multi-level menu which is not implemented yet in angular-foundation
 */


/**
 * Workaround for a problem with angular-foundation that always hides
 * the off-canvas menu when the window resizes. This leads to problems
 * on mobile devices with virtual keyboards, which, when they appear,
 * trigger a resize event that in turn hides the menu. Making the
 * search input field quite useless on mobile phones.
 *
 * The workaround captures all resize events and stops their
 * propagation if the off-canvas menu is currently open.
 *
 * Also see JGiven issue http://github.com/TNG/JGiven/issues/182 and
 * AngularJS Foundation issue http://github.com/pineconellc/angular-foundation/issues/161
 *
 */
jgivenReportApp.directive('offCanvasWrapFixResize', ['$window', function ($window) {
  return {
    require: 'offCanvasWrap',
    restrict: 'C',
    link: function ($scope, element, attrs, offCanvasWrap) {
      var win = angular.element($window);
      win.bind("resize.body", function (e) {
        var menuOpen = angular.element("div.move-right");
        if (menuOpen.length > 0) {
          e.stopImmediatePropagation();
        }
      });
    }
  };
}]);


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
