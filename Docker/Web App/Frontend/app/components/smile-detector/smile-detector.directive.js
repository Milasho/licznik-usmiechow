(function () {
	angular.module("app.components")
	.directive("smileDetector", Directive);
	
	function Directive(){
		 return {
	    	restrict: 'E',
	    	templateUrl: 'app/components/smile-detector/smile-detector.directive.html',
	    	controller: DirectiveController,
	    	controllerAs: 'ctrl',
	    	bindToController: true,
	    	scope: {
	    		captureInterval: '=',
	    		onSmileDetected: '=',
				serviceName: '@'
	    	}
	    };
	}
	
	DirectiveController.$inject = ['$interval', '$scope', 'rest', 'config'];
	function DirectiveController($interval, $scope, rest, config){
		var ctrl = this;	
		var capturingInterval = null;
		var webcam = null;
		var hiddenCanvas = null;
		var lastSentSnapshotTime = new Date();
		var smilesManager = new SmilesManager();
		var defaultCaptureInterval = config.snapshotSendInterval;
		var requestCounter = null;
		var timeBetweenFrames = 300;
		
		ctrl.getChannel = getChannel;
		ctrl.onStream = onStream;
		ctrl.onStreaming = onStreaming;
		ctrl.onError = onError;
		ctrl.isRequestQueueFull = isRequestQueueFull;

		init();
		
		////////////////////
		
		function init(){
			hiddenCanvas = document.createElement('canvas');
			webcam = initWebcam();
		}
		
		function initWebcam(){
			return {
				active: false,
				error: false,
				stream: null,
				channel: {
                    videoHeight: config.snapshotSendSize.width,
                    videoWidth: config.snapshotSendSize.height,
				    video: null 
				}
			};
		}
		
		function initCapturingInterval(){
            requestCounter = 0;
			var intervalTime = ctrl.captureInterval || defaultCaptureInterval;
			capturingInterval = $interval(onCapturingInterval, intervalTime);
		}
		
		function stopCapturingInteval(){
			if (angular.isDefined(capturingInterval)) {
				$interval.cancel(capturingInterval);
				capturingInterval = undefined;
			}
		}
		
		function onCapturingInterval(){
			if(webcam.error){
				stopCapturingInteval();
				return;
			}

			var newSnapshotTime = new Date();

			if(ctrl.onSmileDetected != null && (newSnapshotTime - lastSentSnapshotTime > timeBetweenFrames)
				&& requestCounter < config.maxFramesRequest){
				lastSentSnapshotTime = newSnapshotTime;

				var currentSnapshot = getCurrentSnapshot();
				var snapshot = {
					content: currentSnapshot,
					width: webcam.channel.videoWidth,
					height: webcam.channel.videoHeight
				};

                createNewRequest(snapshot);
			}

			if(timeBetweenFrames < 1000 && requestCounter >= config.maxFramesRequest){
                timeBetweenFrames += 50;
			}
		}

		function createNewRequest(snapshot){
			var startTime = new Date();
			var serviceName = ctrl.serviceName;
            requestCounter++;
            return rest.smiles.detect(snapshot, serviceName).then(function(data){
                requestCounter--;

                if(timeBetweenFrames > 300){
                    timeBetweenFrames -= 25;
				}

                if(data != null && data.detectedSmiles != null && data.detectedSmiles.length > 0){
                    data.atLeastOneLasting = smilesManager.addSnapshot(data);
                    data.requestTime = new Date() - startTime;
                    data.serviceName = serviceName;
                    ctrl.onSmileDetected(data);
                }
                else{
                    smilesManager.removeLastFrame();
                }
            });
		}

		function isRequestQueueFull(){
			return requestCounter >= config.maxFramesRequest - 1;
		}
		
		function getChannel(){
			return webcam.channel;
		}
		
		function getCurrentSnapshot(){
			var result = null;
			
			if(hiddenCanvas){
				var video = webcam.channel.video;
	            hiddenCanvas.width = video.width;
	            hiddenCanvas.height = video.height;
	            var ctx = hiddenCanvas.getContext('2d');
	            ctx.drawImage(video, 0, 0, video.width, video.height);
	            result = hiddenCanvas.toDataURL();
			}
			
			return result;
		}
		
		function onStream(stream){
			webcam.stream = stream;
			return stream;
		}
		
		// Webcam started streaming
		function onStreaming(){
			webcam.active = true;
			initCapturingInterval();
		}
		
		function onError(){
			webcam.error = true;
		}
		
		$scope.$on('$destroy', stopCapturingInteval);

        function SmilesManager(){
            var obj = this;
            var previousFrames = new PreviousFramesManager();

            obj.addSnapshot = addSnapshot;
            obj.removeLastFrame = removeLastFrame;

            return obj;
            //////////////////////////

            function addSnapshot(snapshot){
                var facesWithSmile = snapshot != null ? snapshot.detectedSmiles || [] : [];
                var atLeastOneLasting = false;

                for(var i = 0; i < facesWithSmile.length; i++){
                    var face = facesWithSmile[i];
                    face.lastingSmile = previousFrames.isSmileAlreadyDetected(face);
                    if(face.lastingSmile){
                        atLeastOneLasting = true;
                    }
                }

                previousFrames.addFrame(snapshot);

                return atLeastOneLasting;
            }

            function removeLastFrame(){
                previousFrames.removeLastFrame();
            }

            function PreviousFramesManager(){
                var obj = this;
                var frames = [];
                var maxFrames = config.previousFramesInSnapshotDetecting;

                obj.isSmileAlreadyDetected = isSmileAlreadyDetected;
                obj.addFrame = addFrame;
                obj.removeLastFrame = removeLastFrame;

                return obj;

                ////////////////////

                function isSmileAlreadyDetected(face){
                    for(var i = 0; i < frames.length; i++){
                        var faces = frames[i].detectedSmiles || [];

                        for(var j = 0; j < faces.length; j++){
                            if(areFacesSimilar(face, faces[i])){
                                return true;
                            }
                        }
                    }

                    return false;
                }

                function areFacesSimilar(f1, f2){
                    if(!f1 || !f2){
                        return false;
                    }

                    var dx = (f1.width + f2.width) / 8;
                    var dy = (f1.width + f2.width) / 8;

                    var xSimilarity = (Math.abs(f1.x - f2.x) < dx) && (Math.abs(f1.width - f2.width) < dx);
                    var ySimilarity = (Math.abs(f1.y - f2.y) < dy) && (Math.abs(f1.height - f2.height) < dy);

                    return xSimilarity && ySimilarity;
                }

                function addFrame(frame){
                    frames = [frame].concat(frames);
                    if(frames.length > maxFrames){
                        frames.splice(-1,1);
                    }
                }

                function removeLastFrame(){
                    if(frames.length > 0){
                        frames.splice(-1,1);
                    }
                }
            }
        }
	}
}());