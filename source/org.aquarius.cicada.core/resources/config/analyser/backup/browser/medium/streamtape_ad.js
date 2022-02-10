//This script has better compatibility, but it will popup ads.
//Although all popup ads will be closed in 3 seconds.

function doGetPixelList() {
	var queryPixel = "720P,1080P,480P";

	try {
		queryPixel = queryPixelsFunction();
	} catch (err) {
		//Nothing to do
	}

	if (null == queryPixel) {
		queryPixel = "720P,1080P,480P";
	}

	var pixelList = queryPixel.split(',');
	if ((pixelList == null) || (pixelList.length == 0)) {
		pixelList = queryPixel.split("720P,1080P,480P");
	}
	
	return pixelList;
}

function buildUrlMap(downloadUrl)
{
	var urlMap = new Object();
	
	var pixelList = doGetPixelList();
	
	for (var i = 0; i < pixelList.length; i++) {
		urlMap[pixelList[i]] = downloadUrl;
	}
	
	urlMap[""] = downloadUrl;
	urlMap[" "] = downloadUrl;
	
	return urlMap;
	
}


function doAnalyseDownloadUrl() {

	var currentPageUrl = document.location.href;


	if (currentPageUrl.indexOf('/e/') > -1) {
		var result = new Object();

		currentPageUrl = currentPageUrl.replace("/e/", "/v/");

		result.redirectUrl = currentPageUrl + "#";

		return result;
	}

	var lastChar = currentPageUrl.substring(currentPageUrl.length - 1);

	if (lastChar != '#') {
		var result = new Object();

		currentPageUrl = currentPageUrl.replace("/e/", "/v/");

		result.redirectUrl = currentPageUrl + "#";

		return result;
	}

	var downloadElement = document.querySelector("a#downloadvideo");
	var downloadUrl = downloadElement.getAttribute("href");

	if (downloadUrl == null || downloadUrl.length < 10) {
		downloadElement.click();

		var result = new Object();

		result.prohibitReloadPage = true;

		return result;
	}

	downloadUrl = downloadElement.href;

	var urlMap=buildUrlMap(downloadUrl);
	
	result.urlMap = urlMap;
	result.shouldRetry = false;

	return result;
}

return JSON.stringify(doAnalyseDownloadUrl());

//  https://streamtape.com/v/KQR3RWVOwKf0yRo/JUL-826-C.mp4
