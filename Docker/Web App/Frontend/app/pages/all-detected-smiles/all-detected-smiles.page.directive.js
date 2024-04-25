(function () {
	angular.module("app.pages")
	.directive("allDetectedSmilesPage", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/pages/all-detected-smiles/all-detected-smiles.page.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {}
	    };
	}
	
	DirectiveController.$inject = [];
	function DirectiveController(){
		var ctrl = this;
	}
}());