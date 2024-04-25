(function () {
	angular.module("app.components")
	.directive("scroll", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/components/scroll/scroll.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {},
	    	transclude: true
	    };
	}
	
	DirectiveController.$inject = [];
	function DirectiveController(){
		var ctrl = this;
	}
}());