### Configuration instructions

At present, the software provides the configuration of four preferred content. Here are a few important configuration items.

**Download Instructions:**

1. Specify the ffmpeg command. For streaming media using the HLS protocol, after the download is complete, you need to use ffmpeg to merge, and you can configure the corresponding command line here. If ffmpeg has been added to the current environment, just use the default configuration. Otherwise, you need to specify the path of the ffmpeg execution file, such as "C:\\MyTools\\ffmpeg\\bin\\ffmpeg.exe -f concat -safe 0 -i "{0}" -c copy "{1}", configure Please be sure to set "{0}" and "{1}" correctly. The former is the corresponding HLS list file, and the latter is the output media file. These two parameters are automatically filled in by the system.
2. Configure the calling system command and check the "Delete source files after merging HLS" to automatically clear the redundant HLS fragment files after merging.
3. Many sites have restrictions on the number of thread connections, so if there is an error due to too many thread connections, you can set the connection limit for the specified site in the bottom table. If the number of connections of [www.google.com](http://www.google.com) is 10, you only need to use google or google.com as the site keyword when setting it, because the system will use the string contained Way to judge.
    

**Interface description:**

1. If you feel that the system resources are too high, or the system is unstable, you can cancel the configuration of "Use Chromium Kernel", and you can disable Chrominum after restarting, which can improve system stability.
2. Whether it is Mp4 or HLS streaming media, there are many players that can be played directly. If you want to directly use the browser of this machine to view the video, you can quickly enable this function by setting the "Start Player Command". PotPlayer is used by default. You can specify your favorite player at will. Just put the parameter "{0}" in the correct position, because the system will automatically fill in the remote playback URL address here before executing.

The management of "video" and "site" is relatively simple and will not be described in detail.