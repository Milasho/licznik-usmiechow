(function () {
	angular.module("app.components")
	.directive("spinner", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/components/spinner/spinner.directive.html',
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
		ctrl.text = ctrl.spinnerText || 'spinner.loading';
	}
}());