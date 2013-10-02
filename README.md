A7weibo
=======

Sina weibo Android client for Android 2.3 and up. (experimental) 
新浪微博Android客户端，适用于Android 2.3及以上（实验性质）

-----

界面虽然是Holo风格，但是只有我一人开发，时间紧迫（交作业），没功夫打磨，先实现功能要紧，所以就比较丑了。

### 功能

已实现功能：

- 浏览微博（不完善）
	+ 加载更多
	+ 下拉刷新


### currently used open source library


- [Android-Universal-Image-Loader][image-loader]
Powerful and flexible instrument for asynchronous loading, caching and displaying images on Android.

- [AndroidCommon][android-common] specifically ImageCache, it will replace `Android-Universal-Image-Loader` after more tests
trinea android common lib, include ImageCache, DropDownListView, DownloadManager, install apk silent and so on 
http://www.trinea.cn/

- [ActionBar-PullToRefresh][pull-refresh]
Cloned from git on Sun Sep 29  CST 2013 evening, as it is under development and the API could change, it is reasonable to note down the cloned date.

`ActionBar-PullToRefresh` provides an easy way to add a modern version of the pull-to-refresh interaction to your application.(New Gmail like)
The compnents used in my app are:

	- `ActionBar-PullToRefresh-actionbarcompat` (name in original repository: `ActionBar-PullToRefresh/extras/actionbarcompat`) support SDK level 7+.
	- `ActionBar-PullToRefresh-library` (name in original repository:  `ActionBar-PullToRefresh/library`) support SDK level 14+

The `ActionBar-PullToRefresh-actionbarcompat` relies on:

	- `ActionBar-PullToRefresh-library`
	- `android-support-v7-appcompat` (included in my project)


[image-loader]: https://github.com/nostra13/Android-Universal-Image-Loader
[pull-refresh]: https://github.com/chrisbanes/ActionBar-PullToRefresh
[android-common]: https://github.com/Trinea/AndroidCommon/
