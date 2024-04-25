(function () {
	angular.module("app.components")
	.directive("page", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/components/page/page.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {
	    		'pageClass' : '@',
	    		'scrollClass' : '@',
	    		'scrollableBody' : '=',
	    		'isLoading' : '=',
				'isProcessing' : '=',
	    		'scrollId' : '@',
				'backgroundImage' : '@'
	    	},
	    	transclude: {
	    		'title' : '?pageTitle',
	    		'top' : '?pageTop',
	    		'body' : 'pageBody',
	    		'footer' : '?pageFooter'
	    	}
	    };
	}
	
	DirectiveController.$inject = ['$transclude'];
	function DirectiveController($transclude){
		var ctrl = this;	
		
		ctrl.isTranscludeGiven = isTranscludeGiven;
		ctrl.getHomePageBackground = getHomePageBackground;

		///////////////////
		
		function isTranscludeGiven(slot){
			return $transclude.isSlotFilled(slot);
		}

		function getHomePageBackground(){
			var result = {};
			if(ctrl.backgroundImage){
				result['background-image'] = "url('" + ctrl.backgroundImage + "')"
			}

			return result;
		}
	}
}());