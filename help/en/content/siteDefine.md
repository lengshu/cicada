### How to customize the video display interface

Here is a detailed description of how users customize the video display interface.
First, explain how to display the video list in the browser.

1. The program will load the workspace/config/site/index.html file in the directory where the program is located into the browser.
2. Before loading the index.html file, a series of functions will be automatically injected into the browser. These functions will exist in the browser as a JS-Java bridge, that is, Java services (specific services) can be called through JS scripts. (Given in the form of a table below).
3. When the Html is loaded, it will use JavaScript to call the Java service to dynamically generate Html code to display the video information.

Therefore, anyone who is not satisfied with the video list interface displayed by the current browser, or expects more functions, can modify workspace/config/site/index.html in the directory where the program is located, and then click "renew" in the browser interface Load the browser "menu to view the effect (you need to make sure that the browser is not using the browser's own menu, which can be modified in the preferences).

| Name and parameters | Function description | Example |
| :-: | :-: | :-: |
| int queryMovieCountFunction() | Query the number of videos under the current site (if there is a filter condition, it is the filtered result) | |
| JSON queryMovieListFunction(int currentPage,int pageCount) | Query the video content in the specified page under the current site, the result is returned in JSON format, see the following content | queryMovieListFunction(10,40) When each page contains 40 records, the first What data are on page 10 |
| void downloadMovieListFunction(String movieIdList) | Download the video with the specified ID number, the parameter is a string, which is a combination of ID numbers, separated by "," or "\|" | queryMovieListFunction("11456,23768") download ID number is Two videos of 11456 and 23768 |
| void selectMovieFunction(int movieId) | It is equivalent to a message event, notifying the program that the user has selected a video, and the information of this video can be displayed in the property view. | selectMovieFunction(12345) Notifies that the video with the program ID number 12345 is selected |

```json
[
{
"albumCount": 1,
"category": "Cat1,Cat2",
"channel": "Channel1",
"detailed": true,
"duration": "00:12:01",
"id": 514899,
"imageUrl": "https://...",
"outOfValidTime": true,
"pageUrl": "https://...",
"publishDate": "2021-12-06",
"score": 0,
"site": "xxx",
"state": 8,
"tag": "tag1,tag2",
"title": "my Movie"
}
]
```