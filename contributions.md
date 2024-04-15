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
- Also working on the result page, couldn't find the time to complete it. Will try to finish it in Week 4.

## 15.04 - 21.04 Week 4

## 22.04 - 28.04 Week 5
