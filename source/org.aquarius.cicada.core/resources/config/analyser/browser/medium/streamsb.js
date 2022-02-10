//For streamsb.net

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

function doParseDownloadPage() {
	var result = new Object();

	var tableElement = document.querySelector("table");
	if (null == tableElement) {
		result.errorMessage = "This video is not allowed to be downloaded by the uploader";
		result.shouldRetry = false;

		return (result);
	}

	var hrefElements = tableElement.querySelectorAll("a");

	var urlTemplate = "https://streamsb.net/dl?op=download_orig&id={0}&mode={1}&hash={2}";

	var pattern = /\'(.*?)\'/g;

	for (var i = 0; i < hrefElements.length; i++) {
		var hrefElement = hrefElements[i];
		var pixelElement = hrefElement.parentElement.nextElementSibling;

		var pixelString = pixelElement.textContent;

		var index = pixelString.indexOf(',');
		if (index > -1) {
			pixelString = pixelString.substring(0, index);
		}

		index = pixelString.indexOf('x');
		if (index > -1) {
			pixelString = pixelString.substring(index + 1);
		}

		var isSuitable = isAcceptablePixelFunction(pixelString);


		if (isSuitable == true) {
			var clickContent = hrefElement.getAttribute("onclick");
			var results = clickContent.match(pattern);

			for (var j = 0; j < results.length; j++) {
				results[j] = results[j].replace("'", "");
				results[j] = results[j].replace("'", "");
			}

			var newUrl = urlTemplate.replace("{0}", results[0]);
			newUrl = newUrl.replace("{1}", results[1]);
			newUrl = newUrl.replace("{2}", results[2]);


			result.redirectUrl = newUrl;
			result.shouldRetry = true;

			return (result);
		}
	}

	result.errorMessage = "No suitable pixel video founded.";
	result.redirectUrl = newUrl;
	result.shouldRetry = false;

	return (result);

}

function doAnalyseDownloadUrl() {

	var currentUrl = document.location.href;
	var urlRegExp = new RegExp("/[a-zA-z]{1}/");

	var currentPageType = 0;

	if (urlRegExp.test(currentUrl)) {
		var newUrl = currentUrl.replace(urlRegExp, "/d/");
		if (newUrl != currentUrl) {
			var result = new Object();

			result.redirecturl = newUrl;
			result.shouldRetry = true;
			return (result);
		} else {
			return doParseDownloadPage();
		}
	}


	var formButtonElement = document.querySelector("form > button");
	if (null != formButtonElement) {
		var result = new Object();

		result.shouldRetry = true;
		result.prohibitReloadPage = false;
		formButtonElement.click();
		return (result);
	}

	var linkElement = document.querySelector("#container > div > span > a");
	if (null == linkElement) {
		var result = new Object();

		result.shouldRetry = false;
		return (result);
	} else {
		var result = new Object();
		var downloadUrl = linkElement.href;

		var urlMap=buildUrlMap(downloadUrl);
		
		result.urlMap = urlMap;
		result.shouldRetry = false;

		return (result);
	}
}

// https://streamsb.net/e/bcv4fj3dczo0.html
return JSON.stringify(doAnalyseDownloadUrl());