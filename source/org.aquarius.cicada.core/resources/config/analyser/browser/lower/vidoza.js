//For vidoza.net

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
	var result = new Object();

	var element = document.querySelector("source");
	if (null == element) {
		result.shouldRetry = false;
		result.errorMessage='No download video founded.';
		return result;
	}

	var downloadUrl = element.src;

	var urlMap=buildUrlMap(downloadUrl);
	
	result.urlMap = urlMap;
	result.shouldRetry = false;

	return result;
}

return  JSON.stringify(doAnalyseDownloadUrl());