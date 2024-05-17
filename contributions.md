# M2

## 25.3-07.04 Week 1 & 2 (Including Easter Break)

**na50r - Rosan Shanmuganathan**
- I worked on the login & registration button [Pull Request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/59)
- I worked on the lobby-overview screen [Pull Request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/60)
- I worked on the profile page [Pull Request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/61)
- While working all these three 'sets of issues' I also came up with a basic structure for the front end, making appropriate folders for appropriate components and CSS elements.


**Grizzlytron - Lucas Timothy Leo Bär**
- I worked on the Lobby page, where the different types of users can see the gamemodes and all currently joined players.
- Also implemented quit functionality with a cool popup that rechecks if a user wants to leave or not.
- Worked on the design generally. [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/62)

**Tmmn - Timon Leupp**

All changes are in this [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/119)

- I worked on the representation of lobbies and their users. I created the JPA entities, repositories and all the related tests. #47 #41 #113
- I implemented the GET /lobbies endpoint where the client can request a list of all the currently available public lobbies and all the related tests. #118
- I implemented the functionality where users can authenticate with their token and create a new lobby and wrote all the related tests. #39 #40

**Aquamarine-12 - Alexandru-Mihai Hurjui**

Worked on:

- User registration: [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/115)
- User login: [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/116)
- User logout: [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/117)

**JacquelineUlken - Jacqueline Ulken**
- Call to the database to get a word, [#77](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/77), [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/114)
- Word merging to get combination, [#80](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/80), [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/114)

## 08.04 - 14.04 Week 3

**Tmmn - Timon Leupp**
- User can join existing lobby by providing user token and lobby code. [#42](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/42) [pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/125)
- Player can leave lobby: player gets deleted. If the user was lobby owner, then the lobby will also be deleted. [#28](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/28) [pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/127)
- General cleanup, wrote tests and moved token to header
- Experiments with using postgres as our data base. Conclusion: Our current entity setup is compatible. [branch](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/tree/postgres)

 **JacquelineUlken - Jacqueline Ulken**
 - Api service to generate new combinations, [#78](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/78), [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/124)
 - Implemented creating a new game and setting Player starting words [#76](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/76), [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/128)
 - Implemented updating the Player words when a player merges two words [#81](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/81), [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/128)

**Grizzlytron - Lucas Timothy Leo Bär**
- I worked on the Copy button and lobby code functionality for the client side. [18](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/18) and [21](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/21)
- Worked on the design generally. Also figured out how images can be deployed and shown on google cloud.

**Aquamarine-12 - Alexandru-Mihai Hurjui**

- Created the Game Board (i.e., the page where the player merges the elements). Implemented all client-side functionality.
- Pull request: [Link](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/65), issue numbers: [#31](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/31), [#32](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/32), [#35](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/35), [#36](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/36)

**na50r - Rosan Shanmuganathan**
- Worked on integrating Client & Server for Login, Registration and Profile page. Adjusted functionality on both sides and implemented authentication using tokens in header 
- Client side [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/64)
- Server side [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/130)
- Started working on the result page, couldn't find the time to complete it. Will try to finish it in Week 4.

## 15.04 - 21.04 Week 4
**na50r - Rosan Shanmuganathan**
- Worked on the result page and completed it. (Still not integrated with server yet, waiting for web socket setup to be complete) [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/71)
- Implemented Profile Picture Persistance, Worked Full stack
- Implemented Persistance on Client Side, Added More profile pictures, fixed bugs [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/74)
- Adjusted Server side to match Client, Added Tests for all things Edit User Info related [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server)

 **JacquelineUlken - Jacqueline Ulken**
 - Added endpoint for creating and starting a new game in an existing lobby [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/134)
 - Implemented endpoint to play the game, which automatically updates the player [#79](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/79), [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/134)
 - Added player authentication to the play endpoint [#82](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/82), [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/134)
 - Implemented receiving incoming words from client to return merged words [#105](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/105), [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/134)

 **Grizzlytron - Lucas Timothy Leo Bär**
- Implemented creating and joining lobbies from client side (on both lobby and lobbyoverview page). Also implemented design and a few other functions. [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/72). 

**Aquamarine-12 - Alexandru-Mihai Hurjui**

- Designed the algorithm for generating the target word and implemented it (apart from the unit tests). Issue: [#137](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/137), pull request: [link](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/138) 

**Tmmn - Timon Leupp**
- Spent many hours getting websockets to work. I have a working demo [here](https://sopra-fs24-leupp-timon-client.oa.r.appspot.com/websocket-demo)
- Implement lobby update endpoint that relies on a websocket connection [#104](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/104) [#121](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/121)
- Updated our endpoint specification [here](https://docs.google.com/document/d/17lbypcjihu_So1mG5_hbFzTprMyhldP0R6PDwxjIpnU/edit?usp=sharing)

## 22.04 - 28.04 Week 5

**Aquamarine-12 - Alexandru-Mihai Hurjui**
- Wrote unit tests for target word generation
- Created functionality to display target word on screen ([issue](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/50))
- Fixed bugs in the word board (front-end: [link](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/commit/cb0ec31898b5650cffe9ef58f2e9ba079b6e1aa0)) and word target generation (back-end: [link](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/commit/c41c2cf605336e606383a17141392c5692018d42))
- Made the slides for the presentation.  

 **Grizzlytron - Lucas Timothy Leo Bär**
- Finished connecting frontend with backend mainly on Lobby and LobbyOverview Page. Implemented further functionalities on client side and adapted page. Refactored some models and also adjusted styling. [Commit](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/72/commits/519aee47512f4524433d8f68997fa0d2604d58e4)
- Also worked on a bit of bug fixing. [Commit](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/72/commits/2d7078da6e341b60f16e2a89b9275c2ae5cc4f5e)

**na50r - Rosan Shanmuganathan**
- Helped with the Integration of Lobby Management (mainly client side), added option to edit lobbyname / set public/private status [Pull Request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/72) 
- Figured out how to integrate Vertex AI as an external API and modified the corresponding `Llm-api` branch accordingly, played with `main.yml` and `pr.yml` until it worked even post-deployment [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/146)
- I mainly worked on aiding other on-going issues / bug fixing, I didn't tackle any new features, Jackie did most of the Java setup for `Llm-api`whereas I tried to find working APIs and figured out how they work for the most part. 

 **JacquelineUlken - Jacqueline Ulken**
 - Fixed various bugs, got the gameplay communication between server and client to run properly
 - Worked on connecting core gameplay to the client [#76](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/76)
 - Worked on displaying other player's activity during a game [#77](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/77)

**Tmmn - Timon Leupp**
 - Implemented websockets for frontend lobby display and made it possible to update lobby information (backend) [pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/139)
 - Changed Google Cloud environment to flexible
 - Setup [postgres](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/147) database


## 29.04 - 05.05 Week 6
**na50r - Rosan Shanmuganathan**
- I added a Quit button the Client side such that it's now possible to also quit mid-game, it uses the same functionality of a Lobby-Quit, i.e., if you're the owner of the lobby, the whole lobby is closed, otherwise, you just leave the lobby.
- I made it possible that the Result page also indicates who the current player is in case of loss. I.e., if you win, your Player is highlighted in blue but if you lose, the winner's player is highlighted in blue and your player is highlighted in red. 
- [Pull Request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/90)
- I also added an optional timer, i.e., it is now possible to set a timer for 1,2,3,4 or 5 minutes and the game ends if you run out of time. The timer runs on the server but informs the Client within certain intervals via WebSocket implementation.
- [Pull Request (Client)](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/105)
- [Pull Request (Server)](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/185)
- Encountered issues when testing as the testing DB on Postgres kept changing back and forth between an implementation that had a `gameTime` attribute and one that had not. Plan is to wait for @JacquelineUlken's implementation to get merged first, adapt to it and merge mine after.

**Tmmn - Timon Leupp**
- Support for anonymous players ([Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/188))
- Wrote tests and improved overall test coverage [commit](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/commit/96aadd0b87658f381dd06f90f8924487af9a4136)
- [Change database setup](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/commit/9703e7f1da4fca0cc95ee6e743923ed394590b6d) so that running tests no longer empties our production database.
- Support by finding bugs and fixing them.

**Grizzlytron - Lucas Timothy Leo Bär**
- Used Joker for this week, no contribution in form of development tasks.

**Aquamarine-12 - Alexandru-Mihai Hurjui**
- Added statistics attributes to `User`'s (number of discovered words, rarest word found etc.);
- Added tracking for these statistics
- Issues: #26, #97, #99. Pull request: [link](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/187)
- Added propagation of word depth whenever a new combination is generated. Pull request: [link](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/189)

 **JacquelineUlken - Jacqueline Ulken**
 - Fixed various bugs [#166](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/166), [#164](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/164), [#163](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/163)
 - Added Finite Fusion in backend [#155](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/155)
 - Added Finite Fusion in frontend [#87](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/84), [#82](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/82), [#81](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/81)
 - Added automatic updating of other players activity [#77](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/77),[#92](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/92)

## 06.05 - 12.05 Week 7
**Tmmn - Timon Leupp**
- Lobbies are now deleted after 3 hours of inactivity. [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/194)
- User can rejoin their lobby. [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/195)
- Set up logging [here](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/commit/560a80bc73cd0f29e03efe27f031f3512aa70108)
- Refactored update lobby to using update booleans in lobby class and no longer passing a Map. [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/196)

**na50r - Rosan Shanmuganathan**
- Continued to work on Timer, modified methods such that they become easier to test and added tests for the timer (timer runs faster in test, so test will not take 1 minute but it'll test everything that should happen when timer is set to 1 min)
- [Pull Request (Client)](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/105)
- [Pull Request (Server)](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/185)
- Working on ui improvement, adding more explanation, CSS fix-ups and possible refactoring of code

**Grizzlytron - Lucas Timothy Leo Bär**
- Added joining through URL functionality, for anonymous as well as registered Users. Added an intermediary page and implemented routing. [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/110)
- Also worked on CSS Styling and bug fixing.

**Aquamarine-12 - Alexandru-Mihai Hurjui**
- Refactored statistics on back-end ([pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/187))
- Added statistics on front-end ([pull request 1](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/117), [pull request 2](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/118))

**JacquelineUlken - Jacqueline Ulken**
- Implemented check for newly discovered words and sending that information in the Get requests [#178](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/issues/178)
- Implemented highlighting newly discovered words [#98](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/issues/98)

## 13.05 - 19.05 Week 8
**Tmmn - Timon Leupp**
- Resolved Mapper warnings [Pull request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/208)
- cleaned up websocket code on [client]() and [server (+ testing)](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/209)
- Supported in [UI improvements](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/115) (=> [example](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/115/commits/580a4bef3a35acf9b295e84228183ddbbcf04f58), [example](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/commit/f18234a5c8d1fd97b20ae3e50d187ce02dfca5dd), [example](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/commit/9b0ca8648b35e53759df8b298c52a5dd0f72b978))

**na50r - Rosan Shanmuganathan**
- Cleaned up most of the UI on Client side and adjusted on Server when needed, Biggest Changes were: Refactored the WordBoard such that it renders properly based on the width of the single Word css and not just the number of words, Used a Typewriter effect for the Timer popups (Refactored Timer on Server side because some bugs happened there), Fixed up Error Messages, Changed from simple `alert` to something more pleasant to look at.
- Frontend [Pull Request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-client/pull/125)
- Backend [Pull Request](https://github.com/sopra-fs24-group-41/sopra-fs24-group-41-server/pull/199)
