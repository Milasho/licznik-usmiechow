(function () {
	angular.module("app.pages")
	.directive("mainPage", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/pages/main/main.page.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {}
	    };
	}
	
	DirectiveController.$inject = ['config', 'rest'];
	function DirectiveController(config, rest){
		var ctrl = this;
		
		ctrl.showDevPanel = showDevPanel;

		init();

		//////////////////////

		function init(){
			rest.config.init();
		}

		function showDevPanel(){
			return config.debugVersion == true;
		}
	}
}());