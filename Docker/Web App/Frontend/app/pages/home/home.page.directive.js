(function () {
	angular.module("app.pages")
	.directive("homePage", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/pages/home/home.page.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {}
	    };
	}
	
	DirectiveController.$inject = ['config'];
	function DirectiveController(config){
		var ctrl = this;

		ctrl.background = config.homePageBackground;
	}
}());