'use strict';

require("lodash/core");
window.$ = window.jQuery = require("jquery");
require("angular");
require("angular-sanitize");
require("angular-foundation");
require("angular-local-storage");
require('./util.js');
require("./reportApp.js")
require('./api.js');
require('./controller/casesTableCtrl.js');
require('./controller/navigationCtrl.js');
require('./controller/chartCtrl.js');
require('./controller/reportCtrl.js');
require('./offCanvasDirective.js');

