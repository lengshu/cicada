function internalParseMovieList() {
	var result = new Object();
	var movies = [];
	result.movieList = movies;

	var elementList = document.querySelectorAll("article.post-item");

	if (null != elementList) {
		for (var i = 0; i < elementList.length; i++) {
			var element = elementList[i];
			var movie = new Object();

			var titleElement = element.querySelector("h3.entry-title > a");

			movie.title = titleElement.textContent;

			movie.pageUrl = titleElement.href;

			var imageElement = element.querySelector("img");
			movie.imageUrl = imageElement.getAttribute("data-src");

            titleElement = element.querySelector("div.blog-pic-wrap > a");
            
            if(null!=titleElement)
            {
                if(null==movie.title)
                {
            	    movie.title=titleElement.title;            	
                }

                if(null==movie.imageUrl)
                {
                	imageElement = titleElement.querySelector("img");
            	    movie.imageUrl=imageElement.src;            	
                }
            }
          


			movies.push(movie);

			var dateElement = element.querySelector("time.published");
			if (null != dateElement) {
				movie.publishDate = dateElement.textContent;
			}
		}
	}

	var pageElements = document.querySelectorAll("div.wp-pagenavi > a");
	var lastElement = pageElements[pageElements.length - 1];

	if (lastElement.textContent.indexOf("»") < 0) {
		result.lastPage = true;
	}

	var pageNumberElement = document.querySelector("div.wp-pagenavi > span.pages");
	var pageContent = pageNumberElement.textContent;

	var lastIndex = pageContent.lastIndexOf(" ");
	var numberContent = pageContent.substring(lastIndex + 1);

	result.totalPageCount = parseInt(numberContent);

	return result;
}

function doParseMovieList()
{
	return JSON.stringify(internalParseMovieList());
}

return doParseMovieList();