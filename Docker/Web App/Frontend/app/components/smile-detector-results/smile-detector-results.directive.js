(function () {
	angular.module("app.components")
	.directive("smileDetectorResults", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/components/smile-detector-results/smile-detector-results.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {
	    		handlers: '=',
	    		onSnapshotSelected: '='
	    	}
	    };
	}
	
	DirectiveController.$inject = ['$interval', '$scope', '$timeout'];
	function DirectiveController($interval, $scope, $timeout){
		var ctrl = this;
        var detectedSmilesCount = 0;
        var smilingSnapshots = [];
        var notSmilingSnapshots = [];
        var startedDisplayingResults = new Date();
        ctrl.detectedSmiles = [];
        var responseTimesSum = 0;
        var responseTimesCount = 0;

		ctrl.getAllDetectedSmilesCount = getAllDetectedSmilesCount;
		ctrl.isSmileSmiling = isSmileSmiling;
		$timeout(init, 0);

		///////////////////
		
		function init(){
            clear();
            initAnimation(300);
			if(ctrl.handlers){
				ctrl.handlers.onNewSnapshot = newSnapshotDetected;
				ctrl.handlers.clear = clear;
				ctrl.handlers.getStats = getStats;
				ctrl.handlers.confirmedSmiles = [];
			}
		}

		function clear(){
			detectedSmilesCount = 0;
			smilingSnapshots = [];
			notSmilingSnapshots = [];
            ctrl.detectedSmiles = [];
            startedDisplayingResults = new Date();
			responseTimesSum = 0;
			responseTimesCount = 0;
		}

		function getStats(){
			return {
				detectedSmiles: detectedSmilesCount,
				smiling: smilingSnapshots.length,
				notSmiling: notSmilingSnapshots.length,
				timeSpent: new Date() - startedDisplayingResults,
				averageResponseTime: responseTimesCount ? Math.round(responseTimesSum / responseTimesCount) : 0
			};
		}

		function isSmileSmiling(smile, smiling){
			var id = smile.id;
			var smilingPos = smilingSnapshots.indexOf(id);
			var notSmilingPos = notSmilingSnapshots.indexOf(id);
			if(smiling){
				if(smilingPos < 0){
                    smilingSnapshots.push(id);
				}
				if(notSmilingPos >= 0){
                    notSmilingSnapshots.splice(notSmilingPos, 1);
				}

				if(ctrl.handlers){
                    ctrl.handlers.confirmedSmiles.push(smile.src[0]);
				}
			}
			else{
                if(notSmilingPos < 0){
                    notSmilingSnapshots.push(id);
                }
                if(smilingPos >= 0){
                    smilingSnapshots.splice(smilingPos, 1);
                }
			}
			smile.smiling = smiling;
		}

		function initAnimation(animationTime){
            var animateSmileInterval = $interval(function(){
                updateAllSmiles();
            }, animationTime);

            $scope.$on("$destroy", function(){
                if (angular.isDefined(animateSmileInterval)) {
                    $interval.cancel(animateSmileInterval);
                    animateSmileInterval = undefined;
                }
            })
		}
		
		function getAllDetectedSmilesCount(){
			return detectedSmilesCount;
		}
		
		function newSnapshotDetected(snapshot){
            responseTimesCount++;
            responseTimesSum+= snapshot.requestTime;

			if(!snapshot.atLeastOneLasting){
				detectedSmilesCount++;
                insertFirst(ctrl.detectedSmiles, createImage(snapshot), 5);
            }
            else if(ctrl.detectedSmiles.length > 0 && ctrl.detectedSmiles[0].src){
				var smile = ctrl.detectedSmiles[0];
                smile.src.push(snapshot.content);
                if(smile.src.length > 6){
                    smile.src.shift();
				}
			}
		}
		
		function insertFirst(array, element, maxElements){
			array.unshift(element);
			
			if(maxElements && array.length > maxElements){
				array.pop();
			}
		}
		
		function createImage(snapshot){ 
			snapshot.src = [snapshot.content];
			snapshot.index = 0;
			snapshot.id = makeid();
			return snapshot;
		}

		function updateAllSmiles(){
			if(ctrl.detectedSmiles && ctrl.detectedSmiles.length > 0){
                var smile = ctrl.detectedSmiles[0];
                smile.index = (smile.index + 1) % smile.src.length
			}
		}

        function makeid() {
            var text = "";
            var possible = "!@#$%^&*()_+?{}|ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

            for (var i = 0; i < 5; i++)
                text += possible.charAt(Math.floor(Math.random() * possible.length));

            return text;
        }
	}
}());