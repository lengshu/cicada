function startTimer() {
	var _my_timer = setInterval(myTimer, 2000);
	window['_my_timer'] = _my_timer;
}

function stopTimer() {
	
	clearInterval(window['_my_timer']);
	window['_my_timer'] = null;
	window['_recursive_finish_'] = true;
	
}

function myTimer() {

	var pattern = /[A-Za-z0-9]{10,20}/;

	var lastUrl = window['_last_url_'];
	
	var elementList = document.querySelectorAll('li.pcVideoListItem');

	if (null != lastUrl) {

		var lastVideoId=pattern.exec(lastUrl)[0];		
		
		for (var i = 0; i < elementList.length; i++) 
		{
			var element=elementList[i];

			var videoId=element.getAttribute("data-video-vkey");

			if(videoId==lastVideoId)
			{
				stopTimer();
				return;
			}
		}
	}


	var allCount = computePlayListVideoCount();
	var currentCount = elementList.length;

	var oldCount=window['_old_count'];
	if(null==oldCount)
	{
		oldCount=0;
	}

	if ((currentCount < allCount)&&(oldCount!=currentCount)) {
		var height=document.querySelector('div.footerContentWrapper').clientHeight;
		height=2*height;

		window.scrollTo(0,document.querySelector("div.wrapper").clientHeight-height);  
		
		window['_old_count']=currentCount;
	} else {
		stopTimer();
	}
}

function computePlayListVideoCount() {
	var countText =document.querySelector('div.aboutInfo > div > a').nextSibling.textContent;
	var allCount = countText.match(/\d+/g);

	return allCount;
}

function getSuggestWaitTime() {
	var allCount = computePlayListVideoCount();

	return Math.ceil((allCount * 3000) / (36 * 4))
}


function internalParseMovieList() {
	//The following link element is to get abs url
	var absLinkElement = document.createElement('a');

	//The following code is to init the return object
	var result = new Object();
	var movies = [];
	result.movieList = movies;

	var rootElement = document.querySelector("ul#showAllChanelVideos");

	if (null == rootElement) {
		rootElement = document.querySelector("ul.pornstarsVideos");
	}

	if (null == rootElement) {
		rootElement = document.querySelector("ul#mostRecentVideosSection");
	}

	if (null == rootElement) {
		rootElement = document.querySelector("ul#pornstarsVideoSection");
	}

	if (null == rootElement) {
		rootElement = document.querySelector("ul#claimedRecentVideoSection");
	}

	if (null == rootElement) {
		rootElement = document.querySelector("ul#videoPlaylist");
	}

	if (null == rootElement) {
		result.shouldRetry = false;

		return result;
	}

	var elementList = rootElement.querySelectorAll("li.videoblock");
	for (var i = 0; i < elementList.length; i++) {

		var movie = new Object();
		var element=elementList[i];

		//The following code is to fetch the information of the movie
		var linkElement = element.querySelector("a");
		movie.title = linkElement.getAttribute("data-title");

		if(null==movie.title)
		{
			movie.title=linkElement.getAttribute("title");
		}

		//Get the abs url
		absLinkElement.href = linkElement.href;
		movie.pageUrl = absLinkElement.href;

		if(movie.pageUrl.indexOf('javascript')>-1)
		{
			movie.pageUrl ="https://www.pornhub.com/view_video.php?viewkey="+element.getAttribute("data-video-vkey");
		}

		var pattern = /&pkey=(\d{2,10}$)/;

		movie.pageUrl=movie.pageUrl.replace(pattern,"");

		var imgElement = linkElement.querySelector("img");
		absLinkElement.href = imgElement.getAttribute("data-src");
		movie.imageUrl = absLinkElement.href;

		//The duration format should be "HH:mm:ss"
		var durationElement = linkElement.querySelector("var.duration");
		movie.duration = durationElement.innerHTML;

		if (movie.duration.split(":").length == 2) {
			movie.duration = "00:" + movie.duration;
		}

		movies[i] = movie;
	}

	//The following code is to judge if the previous page exists or not.
	var previousPageElement = document.querySelector("li.page_previous");

	if (null != previousPageElement) {
		result.previousPageUrl = previousPageElement.children[0].href;
	}

	//The following code is to judge if the next page exists or not.
	var nextPageElement = document.querySelector("li.page_next");

	if (null != nextPageElement) {
		result.nextPageUrl = nextPageElement.children[0].href;
	} else {
		result.lastPage = true;
	}

	if (isPlayList()) {
		result.lastPage = true;
	} else {
		var countElement = document.querySelector("#stats > div:nth-child(3)");

		if (null == countElement) {
			var countElements = document.querySelectorAll("div.showingInfo");
			if (null != countElements) {
				countElement = countElements[countElements.length - 1];
			}
		}

		if (null == countElement) {
			countElement = document.querySelector("div.showingCounter");
		}

		var counterText = countElement.innerText;

		var movieCounts = counterText.match(/\d+/g);
		var countPerPage = 40;


		try {

			var countIndex = counterText.indexOf("-");

			if (countIndex > -1) {
				counterText = counterText.substring(countIndex + 1);
				countPerPage = parseInt(counterText.match(/\d+/g)[0]);
			}


		} catch (error) {

		}


		var totalMovieCount = movieCounts[movieCounts.length - 1];
		var totalPageCount = Math.ceil(totalMovieCount / countPerPage);

		result.totalPageCount = totalPageCount;
		result.totalMovieCount = totalMovieCount;

		var currentUrl = window.location.href;
		var index = currentUrl.indexOf("page=");
		if (index > 0) {
			currentUrl = currentUrl.substring(0, index + 5);
			result.lastPageUrl = currentUrl + totalPageCount;
		} else {
			result.lastPageUrl = currentUrl + '&page=' + totalPageCount;
		}

		var lastElement = document.querySelector('li.page_next.omega');
		if (null == lastElement) {
			result.lastPage = true;
		}
	}

	return result;
}

function isPlayList() {
	var currentUrl = window.location.href;

	return (currentUrl.toLowerCase().indexOf('/playlist/') > -1);
}

function doParseMovieList() {
	if (isPlayList()) {
		var finishState = window['_recursive_finish_'];
		if (finishState != true) {
			startTimer();

			var result = new Object();

			result.prohibitReloadPage = true;
			result.shouldRetry = true;
			result.waitTime = getSuggestWaitTime();

			result.lastPage = true;

			result.totalPageCount = 1;
			result.totalMovieCount = computePlayListVideoCount();

			result.errorMessage="It's loading movies.";

			return (result);
		} else {
			return (internalParseMovieList());
		}
	}

	return (internalParseMovieList());
}

return JSON.stringify(doParseMovieList());