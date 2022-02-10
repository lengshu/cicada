//For mixdrop

function doAnalyseDownloadUrl() {
	var result = new Object();

	var retryCount = document["retryCount"];
	if (null == retryCount) {
		retryCount = 0;
	}

	result.retryCount = retryCount;
	document["retryCount"] = retryCount + 1;

	var currentUrl = document.location.href;

	var checkIndex=currentUrl.indexOf('/e/');
	if(checkIndex>-1)
	{
		currentUrl=currentUrl.replace('/e/','/f/');

		if(currentUrl.indexOf("?download")<0)
		{
			currentUrl=currentUrl+"?download#";
		}

		result.shouldRetry = true;
		result.redirectUrl=currentUrl;

		return result;
	}

	var downloadElement = document.querySelector('a.download-btn');

	if (downloadElement == null) {

		result.shouldRetry = false;
		result.errorMessage = "No download button found";

		return result;
	}

	var downloadUrl = downloadElement.href;
	var needClick=false;
	
	if (downloadUrl == null || downloadUrl == "" ) {
		needClick=true;
	}
	else
	{
		var index=downloadUrl.toLowerCase().indexOf("?download");
		needClick=(index>-1);
	}

	if(needClick)
	{
		downloadElement.click();
		result.waitTime = 5000;
		if (retryCount < 10) {
			result.shouldRetry = true;
		} else {
			result.shouldRetry = false;
		}
		
		return result;
	}

	var urlMap = new Object();

	var queryPixel = "720P,1080P,480P";

	try {
		queryPixel = queryPixelsFunction();
	} catch (err) {
		//Nothing to do
	}

	if (null == queryPixel ) {
		 queryPixel = "720P,1080P,480P";
	}	
	
	var pixelList = queryPixel.split(',');
	
	for (var i = 0; i < pixelList.length; i++) {
		urlMap[pixelList[i]] = downloadUrl;
	}

	result.urlMap = urlMap;
	result.shouldRetry =false;

	return result;
}

return JSON.stringify(doAnalyseDownloadUrl());