# Project 4 - TwitterTweets Phase - II 

TwitterTweets is an android app that allows a user to view his Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Note: Project 3 now lives in a branch named [TwitterAppPhaseI](https://github.com/skipit/TwitterTweets/tree/TwitterAppPhaseI)

Time spent: 9 hours spent in total

## User Stories from Assignment 4

The following **required** functionality is completed:

* [x] The app includes **all required user stories** from Week 3 Twitter Client
* [x] User can **switch between Timeline and Mention views using tabs**
  * [x] User can view their home timeline tweets.
  * [x] User can view the recent mentions of their username.
* [x] User can navigate to **view their own profile**
  * [x] User can see picture, tagline, # of followers, # of following, and tweets on their profile.
* [x] User can **click on the profile image** in any tweet to see **another user's** profile.
  * [x] User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
  * [x] Profile view includes that user's timeline
* [x] User can [infinitely paginate](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews) any of these timelines (home, mentions, user) by scrolling to the bottom

The following **optional** features are implemented:

* [ ] User can view following / followers list through the profile
* [ ] Implements robust error handling, [check if internet is available](http://guides.codepath.com/android/Sending-and-Managing-Network-Requests#checking-for-network-connectivity), handle error cases, network failures
* [ ] When a network request is sent, user sees an [indeterminate progress indicator](http://guides.codepath.com/android/Handling-ProgressBars#progress-within-actionbar)
* [x] User can **"reply" to any tweet on their home timeline**
  * [x] The user that wrote the original tweet is automatically "@" replied in compose
* [x] User can click on a tweet to be **taken to a "detail view"** of that tweet
  * [x] User can take favorite (and unfavorite) or retweet actions on a tweet
* [ ] Improve the user interface and theme the app to feel twitter branded
* [ ] User can **search for tweets matching a particular query** and see results

The following **bonus** features are implemented:

* [ ] User can view their direct messages (or send new ones)

The following **additional** features are implemented:

* [ ] List anything else that you can get done to improve the app functionality!


## User Stories from Assignment 3 

The stories that were completed earlier are ~~striked out~~. Any new stories that were completed are marked accordingly

The following **required** functionality is completed:

* [x]	~~User can **sign in to Twitter** using OAuth login~~
* [x]	~~User can **view tweets from their home timeline**~~
  * [x] ~~User is displayed the username, name, and body for each tweet~~
  * [x] ~~User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h" ~~
  * [x] ~~User can view more tweets as they scroll with [infinite pagination](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews). Number of tweets is unlimited.~~
    However there are [Twitter Api Rate Limits](https://dev.twitter.com/rest/public/rate-limiting) in place.
* [x] ~~User can **compose and post a new tweet**~~
  * [x] ~~User can click a “Compose” icon in the Action Bar on the top right~~
  * [x] ~~User can then enter a new tweet and post this to twitter~~
  * [x] ~~User is taken back to home timeline with **new tweet visible** in timeline~~

The following **optional** features are implemented:

* [x] ~~User can **see a counter with total number of characters left for tweet** on compose tweet page~~
* [x] User can **click a link within a tweet body** on tweet details view. The click will launch the web browser with relevant page opened.
* [x] ~~User can **pull down to refresh tweets timeline**~~
* [x] ~~User can **open the twitter app offline and see last loaded tweets**. Persisted in SQLite tweets are refreshed on every application launch. While "live data" is displayed when app can get it from Twitter API, it is also saved for use in offline mode.~~
* [x] User can tap a tweet to **open a detailed tweet view**
* [x] User can **select "reply" from detail view to respond to a tweet**

The following **bonus** features are implemented:

* [x] User can see embedded image media within the tweet detail view
* [ ] Compose tweet functionality is build using modal overlay

The following **additional** features are implemented:

* [x] ~~Favorite Count of tweet can be seen~~
* [x] ~~Retweet Count of a tweet can be seen~~
* [x] ~~Reply can be done from the list item view on clicking the reply image~~
* [x] ~~User can Favorite/Un-favorite a link. ~~
* [x] ~~User can retweet, however it does not work as credentials are not sufficient. ~~

## Video Walkthrough 

Here's a walkthrough of implemented user stories:

<img src='anim_walkthrough.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Describe any challenges encountered while building the app.

* Handling offline cases. There are a lot of cases while handling offline. Which makes it very challenging. I have not addressed all offline-error cases. 
* Handling autolink. I had implemented Autolink in text view in the adapter, but reverted it to work on other features. 

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android

