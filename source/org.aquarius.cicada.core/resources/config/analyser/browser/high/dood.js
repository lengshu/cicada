//For dood.so


function buildResult(downloadUrl, pixel) {
	var result = new Object();

	var urlMap = new Object();
	urlMap[""] = downloadUrl;
	urlMap[" "] = downloadUrl;

	if (null != pixel) {
		urlMap[pixel] = downloadUrl;
	}

	result.urlMap = urlMap;

	result.requestHeaders = new Object();
	result.requestHeaders['Referer'] = document.location.href;

	result.shouldRetry = false;

	return result;
}

function doAnalyseDownloadUrl() {

	var url = document.location.href;
	if (null == url) {
		url = "";
	}

	if (url.indexOf("http") != 0) {
		url = getCurrentPageUrlFunction();
	}

	if ((null == url) || (url.length < 5)) {
		var result = new Object();

		result.errorMessage = "This page is not accessable";
		result.shouldRetry = false;

		return result;
	}

	url = url.toLowerCase();

	if ((url.indexOf("token=") > -1) && (url.indexOf("expiry=") > -1)) {
		return buildResult(url, "720P");
	}

	var videoElement = document.querySelector("video");
	if (null != videoElement) {
		var downloadUrl = videoElement.src;

		if (null != downloadUrl) {
			return buildResult(downloadUrl, "720P");
		}
	}

	var downloadButton = document.querySelector("a.download_vd");

	if (null != downloadButton) {
		downloadButton.click();

		var result = new Object();
		result.prohibitReloadPage = true;
		return (result);
	}

	downloadButton = document.querySelector("a.btn-primary");
	var downloadUrl = downloadButton.href;

	var pixel = null;

	var index = downloadButton.textContent.toLowerCase().indexOf("high");
	if (index > -1) {
		pixel = "720P";
	} else {
		pixel = "480P";
	}

	return buildResult(downloadUrl, pixel);
}

return JSON.stringify(doAnalyseDownloadUrl());
