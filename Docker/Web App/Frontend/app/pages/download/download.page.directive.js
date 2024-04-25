(function () {
	angular.module("app.pages")
	.directive("downloadPage", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/pages/download/download.page.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {}
	    };
	}

	var architectures = {x64: 64, x32: 32};
	var systems = {Windows : "Windows", Linux: "Linux"};
	
	DirectiveController.$inject = ['config'];
	function DirectiveController(config){
		var ctrl = this;

		ctrl.architectures = architectures;
		ctrl.architecture = getSystemArchitecture();
        ctrl.systems = config.supportedSystems;
        ctrl.system = getSystemPlatform();
        ctrl.moreOptionsVisible = false;

        ctrl.toggleMoreOptions = toggleMoreOptions;

		////////////////////////

        function getSystemPlatform(){
            var platform = window.navigator ? window.navigator.oscpu : null;
            var result = config.supportedSystems[0];
            if(platform != null){
                result = platform.indexOf("Windows") >= 0 ? systems.Windows : systems.Linux;
            }
            return result;
        }

        function getSystemArchitecture() {
            var _to_check = [];
            if (window.navigator.cpuClass) _to_check.push((window.navigator.cpuClass + "").toLowerCase());
            if (window.navigator.platform) _to_check.push((window.navigator.platform + "").toLowerCase());
            if (navigator.userAgent) _to_check.push((navigator.userAgent + "").toLowerCase()) ;

            var _64bits_signatures = ["x86_64", "x86-64", "Win64", "x64;", "amd64", "AMD64", "WOW64", "x64_64", "ia64", "sparc64", "ppc64", "IRIX64"];
            var _bits = architectures.x32, _i, _c;
            outer_loop:
                for(_c = 0; _c < _to_check.length; _c++) {
                    for(_i = 0; _i < _64bits_signatures.length; _i++) {
                        if (_to_check[_c].indexOf(_64bits_signatures[_i].toLowerCase()) !== -1){
                            _bits = architectures.x64;
                            break outer_loop;
                        }
                    }
                }
            return _bits ;
        }

        function toggleMoreOptions(){
            ctrl.moreOptionsVisible = !ctrl.moreOptionsVisible;
        }
    }
}());