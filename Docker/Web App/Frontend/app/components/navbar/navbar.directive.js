(function () {
	angular.module("app.components")
	.directive("navbar", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/components/navbar/navbar.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {
	    		'spinnerText' : '@'
	    	}
	    };
	}
	
	DirectiveController.$inject = [];
	function DirectiveController(){
		var ctrl = this;
	}
}());