(function () {
	var APP_CONFIG_URL = "config/application.config.json";
	var DEMO_DATA_URL = "demo/demo.data.json";
	var LANG_FILES_URL = "config/languages/locale-"
	
	// --------- PRE-ANGULAR ---------
	var externalConfig = {};
	var demoData = {};
	var globalInfo = {};
	
	loadExternalFile(APP_CONFIG_URL, function(data) {
        externalConfig = data;
        
        if(externalConfig.demoVersion){
        	loadExternalFile(DEMO_DATA_URL, function(demoJson){
            	demoData = demoJson;
            });
        }
    }).always(function() {
	    angular.element(document).ready(function() {
	        angular.bootstrap(document, ['app']);
	    });
	});
	
	// --------- DEPENDENCIES ---------
	var app = angular.module('app', [	
		'ngRoute',
		'ngCookies',
		'ui.router',
		'ngAnimate',
		'pascalprecht.translate',
		'perfect_scrollbar',
		'smoothScroll',
		'webcam',
        'chart.js',
        'app.services',
		'app.config',
		'app.components',
		'app.pages'
	]);
	
	// --------- CONFIGURATION ---------
	app.constant("config", externalConfig);
	app.constant("demoData", demoData);

	// Config routing
	app.config(['$locationProvider', 'config',
		function($locationProvider, config) {
			var useHtml5Mode = config.html5Mode || false;
			
			$locationProvider.html5Mode({
				  enabled: useHtml5Mode,
				  requireBase: true
			});
	}]);
	
	// Config translations
	app.config(['$translateProvider', 'config', 
		  function ($translateProvider, config) {
			var allowedLanguages = config.languages;
			var localeFiles = [];		
			
			if(allowedLanguages != null && allowedLanguages.length > 0){
				var defaultLang = config.languages[0];
				
				for(var i = 0; i < allowedLanguages.length; i++){
					var lang = allowedLanguages[i];
					localeFiles.push({url: LANG_FILES_URL + lang + '.json', lang: lang});
				}
				
				loadLocaleFiles(localeFiles, $translateProvider);
				$translateProvider
					.preferredLanguage(defaultLang)
					.useSanitizeValueStrategy('escapeParameters')
                    .useCookieStorage();
			}
		}]);
	
	// Config injectors
	app.config(['$httpProvider',
		function($httpProvider){	
			$httpProvider.interceptors.push('httpInterceptor');
	}]);
	
	
	// Handle configuring errors
	app.run(['pages', function(pages) {
		 if(globalInfo.error != null && globalInfo.error != {}){
			 pages.error(globalInfo.error);
		 }
	}]);

	// Config rest of pages
	app.run(['$animate', '$rootScope', function($animate, $rootScope){
        $animate.enabled(false, $rootScope)
	}]);
	
	// --------- DEFINE MODULES USED IN APPLICATION ---------
	angular.module('app.pages', []);
	angular.module('app.config', []);
	angular.module('app.services', []);
	angular.module('app.components', []);
	
	///////////////////////////////////////
	
	function loadExternalFile(url, successFunc){
		return $.ajax({
		    dataType: 'json',
		    url: url,
		    async: false,
		    success: successFunc,
		    error: function(error){
		    	var message;
		    	switch(error.status){
		    		case 404:
		    			message = "No important application file found at url: " + url;
		    		break;
		    		default:
		    			message = "Something is wrong with important application file at url: " + url;
		    	}
		    	
		    	globalInfo.error = {
		    		message: message,
		    		status: error.status
		    	};
		    }
		})
	}
	
	function loadLocaleFiles(filesArray, $translateProvider){
		for(var i = 0; i < filesArray.length; i++){
			var url = filesArray[i].url;
			var lang = filesArray[i].lang;
			
			loadExternalFile(url, function(data){
				$translateProvider.translations(lang, data);
			});
		}
	}
}());