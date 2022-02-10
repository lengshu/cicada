//For fembed.com

function buildResult(jsonText) {
	var jsonResult = JSON.parse(jsonText);
	var result = new Object();
	var urlMap = new Object();

	var dataElements = jsonResult.data;

	if (null == dataElements || dataElements.length == 0) {
		result.errorMessage = 'No download link founded';
		result.shouldRetry = false;

		return result;
	}

	for (var i = 0; i < dataElements.length; i++) {
		var dataElement = dataElements[i];

		var pixel = dataElement.label.toUpperCase();
		var downloadUrl = dataElement.file;

		urlMap[pixel] = downloadUrl;
	}

	result.urlMap = urlMap;

	result.requestHeaders = new Object();
	result.requestHeaders['Referer'] = document.location.href;

	result.shouldRetry = false;

	return result;
}

function doAnalyseDownloadUrl() {

	var forExternalDownload = window.forExternalDownload;

	var url = document.location.href;
	var fvsUrl = wrapFvsUrlFunction(url);

	var fvsJson = null;

	if (forExternalDownload) {
		var xhr = new XMLHttpRequest();
		xhr.open("POST", fvsUrl, false);
		xhr.send();
		fvsJson = xhr.response;
	} else {
		var httpRequest = new Object();
		httpRequest.url = fvsUrl;
		httpRequest.method = "POST";

		fvsJson = httpFunction(JSON.stringify(httpRequest));
	}

	return buildResult(fvsJson);
}

return JSON.stringify(doAnalyseDownloadUrl());
