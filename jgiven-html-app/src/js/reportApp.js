import DataService from './service/dataService.js'
import TagService from './service/tagService.js'
import ClassService from './service/classService.js'
import OptionService from './service/optionService.js'
import SearchService from './service/searchService.js'

window.jgivenReportApp = angular.module('jgivenReportApp', ['ngSanitize', 'mm.foundation', 'mm.foundation.offcanvas',
        'chart.js', 'LocalStorageModule'])
    .config(['localStorageServiceProvider', function (localStorageServiceProvider) {
        localStorageServiceProvider.setPrefix('jgiven')
    }])
    .config([
        '$compileProvider', function ($compileProvider) {
            $compileProvider.aHrefSanitizationWhitelist(/.*/)
        }])

jgivenReportApp.filter('encodeUri', function ($window) {
    return $window.encodeURIComponent;
})


jgivenReportApp.factory('dataService', DataService)
jgivenReportApp.factory('tagService', ['dataService', TagService])
jgivenReportApp.factory('classService', ['dataService', ClassService])
jgivenReportApp.factory('optionService', ['dataService', OptionService])
jgivenReportApp.factory('searchService', ['dataService', SearchService])
