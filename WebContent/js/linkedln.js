// Setup an event listener to make an API call once auth is complete
function onLinkedInLoad() {
	IN.Event.on(IN, "auth", function() {
		onLinkedInLogin();
	});

	IN.Event.on(IN, "logout", function() {
		onLinkedInLogout();
	});
}

// Handle the successful return from the API call
function onSuccess(data) {
	console.log(data);
}

// Handle an error response from the API call
function onError(error) {
	console.log(error);
}

// Use the API call wrapper to request the member's basic profile data
function getProfileData() {
	IN.API.Raw("/people/~").result(onSuccess).error(onError);
}

function onLinkedInLogout() {
	location.reload(true);
}

function onLinkedInLogin() {
	// pass user info to angular
	angular.element(document.getElementById("appBody")).scope().$apply(
			function($scope) {
				$scope.getLinkedInData();
			});
}