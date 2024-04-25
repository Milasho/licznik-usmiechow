(function () {
	angular.module("app.config")
	.config(StateConfiguration);
	
	// Adding states here
	StateConfiguration.$inject = ['$stateProvider', '$urlRouterProvider'];
	function StateConfiguration($stateProvider, $urlRouterProvider){
	    $urlRouterProvider.otherwise('/home');
		
		addState("home", "/home", "home-page");
		addState("allSmiles", "/all-smiles", "all-detected-smiles-page");
		addState("webcam", "/test-services/{service}", "webcam-page", {service:null});
		addState("download", "/smilecounter-download", "download-page");
		addState("error", "/error", "error-page", {
			error: null
		});
		
		///////////////////////////
		
		function addState(stateName, url, template, params){
			$stateProvider.state(stateName, {
					url: url, 
					template: "<" + template + "></" + template + ">",
					params: params || {}
				}
			);
		}
	}
}());