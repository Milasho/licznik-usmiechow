(function () {
	angular.module("app.pages")
	.directive("errorPage", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/pages/error/error.page.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {}
	    };
	}
	
	DirectiveController.$inject = ['$stateParams', 'pages', 'config'];
	function DirectiveController($stateParams, pages, config){
		var ctrl = this;	
		ctrl.error = parseError();
		
		if(ctrl.error == null){
			pages.home();
		}
		
		ctrl.showDetails = showDetails;
			
		/////////////////
		
		function showDetails(){
			return config.debugVersion == true;
		}
		
		function parseError(){
			var error = angular.copy($stateParams.error);
			if(error){
				error.stackTrace = error.stackTrace != null ? error.stackTrace.split("\n") : undefined;
				return JSON.stringify(error, undefined, 5);
			}
			return null;
		}
	}
}());