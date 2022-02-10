
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

function internalCreateError() {
	var result = new Object();

	result.errorMessage = 'Video is not available.';
	result.shouldRetry = false;

	return (result);
}


function doAnalyseDownloadUrl() {

	var documentTitle = document.title.toLowerCase();

	if ((documentTitle.indexOf('not') > -1) && (documentTitle.indexOf('found') > -1)) {
		return internalCreateError();
	}

	var downloadElement = document.querySelector('div[id$=link]');
	if (null == downloadElement) {
		return internalCreateError();
	}

	var downloadUrl = downloadElement.textContent;
	var index = downloadUrl.length - 71;
	downloadUrl = downloadUrl.substring(index);

	var firstChar = downloadUrl.substring(0, 1);
	if (firstChar != '=') {
		downloadUrl = '=' + downloadUrl;
	}

	downloadUrl = 'https://streamtape.com/get_video?id' + downloadUrl;
	downloadUrl = downloadUrl + '&stream=1';

	var result = new Object();

	var urlMap=buildUrlMap(downloadUrl);
	
	result.urlMap = urlMap;
	result.shouldRetry = false;

	return (result);
}



return JSON.stringify(doAnalyseDownloadUrl());
