// Karma configuration
// Generated on Sat Aug 01 2015 13:13:27 GMT+0200 (CEST)

module.exports = function (config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
      'build/bower_components/**/dist/jquery.js',
      'build/bower_components/angular/angular.js',
      'build/bower_components/angular-sanitize/angular-sanitize.js',
      'build/bower_components/angular-mocks/angular-mocks.js',
      'build/bower_components/**/mm-foundation-tpls.js',
      'build/bower_components/**/foundation.js',
      'build/bower_components/**/Chart.js',
      'build/bower_components/**/angular-chart.js',
      'build/bower_components/**/angular-local-storage.js',
      'build/bower_components/**/lodash.js',
      'build/bower_components/angular-bindonce/bindonce.min.js',
      'src/app/**/*.js',
      'src/test/app/**/*.js'
    ],


    // list of files to exclude
    exclude: [
      'build/bower_components/ngInfiniteScroll/test/**'
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {},


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['Chrome'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false
  })
}
