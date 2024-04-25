(function () {
	angular.module("app.services")
	.service("restCache", Service);
	
	Service.$inject = [];
	function Service(){
		var service = this;
		var caches = {};
		
		service.clearCache = clearCache;
		service.clearAllCaches = clearAllCaches;
		service.get = handleCache;
		
		//////////
		
		function handleCache(url, promise, useCache){
			var result = null;
			
			// Try to use promise from cache - if not found, return normal promise and store it in cache
			if(useCache){
				result = getFromCache(url);
				
				if(result == null){
					result = promise();
					storeInCache(url, result);
				}
			}
			// Return simple GET promise if user do not want to use cache
			else{
				result = promise();
				storeInCache(url, result);
			}
			return result;
		}
		
		function getFromCache(url){
			return caches[url];
		}
		
		function storeInCache(url, promise){
			caches[url] = promise;
		}
		
		function clearCache(cacheName){
			caches[cacheName] = null;
		}
		
		function clearAllCaches(){
			caches = {};
		}
	}
}());