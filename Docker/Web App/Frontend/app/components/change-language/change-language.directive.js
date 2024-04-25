(function () {
	angular.module("app.components")
	.directive("changeLanguage", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/components/change-language/change-language.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {}
	    };
	}
	
	DirectiveController.$inject = ['config', '$translate'];
	function DirectiveController(config, $translate){
		var ctrl = this;	
		ctrl.languages = config.languages;
		
		ctrl.changeLanguage = changeLanguage;
		ctrl.isSelected = isSelected;
		
		///////////////////////
		
		function changeLanguage(lang){
			if(!isSelected(lang)){
				$translate.use(lang);
			}
		}
		
		function isSelected(lang){
			return $translate.use() == lang;
		}
	}
}());