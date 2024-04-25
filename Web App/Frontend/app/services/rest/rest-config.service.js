(function () {
	angular.module("app.services")
	.service("restConfig", Service);
	
	Service.$inject = ['config', '$http', 'restCache', 'restDemo'];
	function Service(config, $http, restCache, restDemo){
		return {
			GET: getPromise,
			DELETE: deletePromise,
			PUT: putPromise,
			POST: postPromise
		};
		
		//////////////////
		
		function getPromise(url, parameters, useCache){
			var promiseUrl = config.restApi + url;
			return restCache.get(promiseUrl, function(){
					return config.demoVersion ? restDemo.getDemoPromise(url, "GET", parameters) 
							: $http.get(config.restApi + url, addStandardHeaders(parameters));
			}, useCache);
		}
		
		function deletePromise(url, data, headers){
			var parameters = {headers: headers};
			return config.demoVersion ? 
					restDemo.getDemoPromise(url, "DELETE", parameters, data) 
					: $http({
					    method: 'DELETE',
					    url: config.restApi + url,
					    data: data,
					    headers: addStandardHeaders(parameters).headers
					});
		}
		
		function postPromise(url, data, parameters){
			return config.demoVersion ? 
					restDemo.getDemoPromise(url, "POST", parameters, data) 
					: $http.post(config.restApi + url, angular.toJson(data), addStandardHeaders(parameters));
		}
		
		function putPromise(url, data){
			return config.demoVersion ? 
					restDemo.getDemoPromise(url, "PUT", parameters, data) 
					: $http.put(config.restApi + url, angular.toJson(data), addStandardHeaders(parameters));
		}	
		
		function addStandardHeaders(parameters){
			var params = parameters || {};
			params.headers = params.headers || {};
			params.headers['Content-Type'] = params.headers['Content-Type'] || 'application/json';
			return params;
		}
	}
}());