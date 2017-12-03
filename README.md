# StortkApp
Android application for the dIntDes Stork project

#RULES
* Remember commiting after EACH change as to maintain a good gitlog
    * Provide good and descriptive commit messages
* Remember to update Trello board with new tasks as you go
* Remember to update Trello board upon completion of task
* Maintain conventions and use ctrl+alt+l to reformat code
    * Don't be an idiot
	
## REST API
 ### Log in
 Logs the user in
 
 | key    |            value            |
 |:------:| :---------------------------|
 | Type   | POST                        |
 | Path   | /loginRequest               |
 | Params | None                        |
 | Body   | Json object LoginRequest    |
 | Resp   | Json object LoginRequest    |
 | Code   | 200/404/500/                |
 
 
 
#### Example body:
```json
{
  "mail": "ernstsen.johannes@gmail.com",
  "password": "kodeord1"
}
``` 
#### Example response:
```json
{
  "mail": "ernstsen.johannes@gmail.com",
  "password": "kodeord1",
  "sessionId": "e7d35d2d-9521-4aa9-a6c5-dc4b08aaf638",
  "userId" : 1
}
``` 

### Log out
Logs the user out
 
 | key    |            value            |
 |:------:| :-------------------------- |
 | Type   | POST                        |
 | Path   | /logout                     |
 | Params | None                        |
 | Body   | Json object LogoutRequest   |
 | Resp   | Json with success boolean   |
 | Code   | 200/404/500/                |
 
 
#### Example body:
```json
{
  "success": false,
  "sessionId": "e7d35d2d-9521-4aa9-a6c5-dc4b08aaf638",
  "userId": 1
}
``` 

#### Example response:
```json
{
  "success": true,
  "userId": 1
}
``` 


### Register new user
Registers new user
 
 | key    |              value              |
 |:------:| :------------------------------ |
 | Type   | POST                            |
 | Path   | /register                       |
 | Params | None                            |
 | Body   | Json object RegisterUserRequest |
 | Resp   | Json object RegisterUserRequest |
 | Code   | 201/404/500/                    |
 
 
#### Example body:
```json
{
  "name": "Johannes Ernstsen",
  "password": "password1",
  "mail": "ernstsen.johannes@gmail.com"
}
``` 

#### Example response:
```json
{
  "name": "Johannes Ernstsen",
  "password": "password1",
  "mail": "ernstsen.johannes@gmail.com",
  "userId": 1,
  "sessionId": "e7d35d2d-9521-4aa9-a6c5-dc4b08aaf638"
}
``` 

### Update location
Updates the users location on the server
 
 | key    |               value               |
 |:------:| :-------------------------------- |
 | Type   | POST                              |
 | Path   | /updateLocation                   |
 | Params | None                              |
 | Body   | Json object UpdateLocationRequest |
 | Resp   | None                              |
 | Code   | 202/404/500/                      |
 
 
#### Example body:
```json
{
  "userId": 1,
  "sessionId": "e7d35d2d-9521-4aa9-a6c5-dc4b08aaf638",
  "location": {
    "longtitude": 1.0,
    "lattitude": 2.0,
    "timeStamp": 1512041409284
  }
}
``` 

### Change Friend
Adds or removes a friend
 
 | key    |               value               |
 |:------:| :-------------------------------- |
 | Type   | POST                              |
 | Path   | /friend                           |
 | Params | None                              |
 | Body   | Json object FriendChangeRequest   |
 | Resp   | None                              |
 | Code   | 201/404/500/                      |
 
 
#### Example body:
```json
{
  "action": "ADD",
  "userId": 1,
  "sessionId": "e7d35d2d-9521-4aa9-a6c5-dc4b08aaf638",
  "friends": [
    2,
    3
  ]
}
``` 

### Get User
Gets the user information from the server
 
 | key    |                  value                 |
 |:------:| :------------------------------------- |
 | Type   | GET                                    |
 | Path   | /getUser                               |
 | Params | sessionId, userId                      |
 | Resp   | Json object UserObject                 |
 | Code   | 200/404/500/                           |
 
 #### Example Response:
```json
{
  "userId": 1,
  "name": "Johannes Ernstsen",
  "mail": "Ernstsen.johannes@gmail.com",
  "sessionId": "e7d35d2d-9521-4aa9-a6c5-dc4b08aaf638"
}

``` 
 
 
### Get Locations
Gets locations for all group members

 | key    |                  value                 |
 |:------:| :------------------------------------- |
 | Type   | GET                                    |
 | Path   | /getLocations                          |
 | Params | sessionId, userId                      |
 | Resp   | Json object LocationsResponse          |
 | Code   | 200/404/500/                           |
 
  #### Example Response:
 ```json
 {
   "locations": {
     "Søby": {
       "longtitude": 3.8,
       "lattitude": 4.5,
       "timeStamp": 1512050488140
     },
     "Morten": {
       "longtitude": 1.7,
       "lattitude": 2.3,
       "timeStamp": 1512050488140
     }
   }
 }
 ``` 
 
### Get Friends
Gets friends for supplied user

 | key    |                  value                 |
 |:------:| :------------------------------------- |
 | Type   | GET                                    |
 | Path   | /getFriends                            |
 | Params | sessionId, userId                      |
 | Resp   | Json object UsersResponse              |
 | Code   | 200/404/500/                           |
 
  #### Example Response:
 ```json
 {
   "locations": {
     "Søby": {
       "longtitude": 3.8,
       "lattitude": 4.5,
       "timeStamp": 1512050488140
     },
     "Morten": {
       "longtitude": 1.7,
       "lattitude": 2.3,
       "timeStamp": 1512050488140
     }
   }
 }
 ```  
### Get Users
Get all users name and email for use in searches

 | key    |                  value                 |
 |:------:| :------------------------------------- |
 | Type   | GET                                    |
 | Path   | /getUsers                              |
 | Params |                                        |
 | Resp   | Json object UsersResponse              |
 | Code   | 200/404/500/                           |
 
  #### Example Response:
 ```json
 {
     "users": [
         {
             "userId": 1,
             "name": "Johannes",
             "mail": "Ernstsen.johannes@gmail.com"
         },
         {
             "userId": 2,
             "name": "Morten",
             "mail": "mortens.email.som.jeg.ikke.kan@gmail.com"
         },
         {
             "userId": 3,
             "name": "Mathias",
             "mail": "Mathias.mail.som.jeg.heller.ikke.lige.kan@gmail.com"
         }
     ]
 }
 ``` 