
# Voting server

Voting server is responsible for managing polls.  It allows logged user:
- create new poll
- list all existing polls 
- cast a vote in the existing poll

Each operation is exposed via REST API, that is divided into two main sections
- `/auth` provides operations to register new user or login existing user
- `/poll` provides poll management operations. Each method requires authentication with JWT token.

# Authentication

In order to use `/poll` API you must be authenticated. 
1. Register new user by calling `/auth/register` where you pass desired username and password. Keep in mind that selected username must be unique
2. (Optional) Register endpoint is returning JWT token that is ready to use, however later you may want to use `/auth/login` endpoint in order to acquire new token
3. As a result of registration or login , you will receive JWT `token` that you must pass with each request to `/poll` as header: `Authorization`, where value is `Bearer $token`
# Api Documentation

### /auth/login

#### POST
##### Summary

Login operation

##### Description

Performs user login. On success, returns username and JWT token.

##### Parameters

| Name | Located in | Required | Schema |
| ---- | ---------- | -------- | ---- |
| AuthCommand | body | Yes | [AuthCommand](#authcommand) |

##### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | Success | [AuthResult](#authresult) |
| 401 | Unauthorized | [AuthResult](#authresult)  |

### /auth/register

#### POST
##### Summary

Register operation

##### Description

Performs new user registration. On success, returns username and JWT token.

##### Parameters

| Name | Located in | Required | Schema |
| ---- | ---------- | -------- | ---- |
| AuthCommand | body | Yes | [AuthCommand](#authcommand) |

##### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | Success | [AuthResult](#authresult) |
| 401 | Unauthorized | [AuthResult](#authresult) |

### /poll

#### GET
##### Summary

List all polls

##### Description

Lists all polls sorted by creation date descending. Voting results are included only for polls that user took part in.

##### Parameters

| Name | Located in | Required | Schema |
| ---- | ---------- | -------- | ---- |
| Authorization | header | Yes | string |

##### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | Success | [ [Poll](#poll) ] |
| 401 | Unauthorized |  |

#### POST
##### Summary

Create new poll

##### Parameters

| Name | Located in | Required | Schema |
| ---- | ---------- | -------- | ---- |
| Authorization | header | Yes | string |
| command | body | Yes | [CreatePollCommand](#createpollcommand) |

##### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 201 | Created | [Poll](#poll) |
| 401 | Unauthorized |  |

### /poll/vote

#### POST
##### Summary

Add vote

##### Parameters

| Name | Located in | Required | Schema |
| ---- | ---------- | -------- | ---- |
| Authorization | header | Yes | string |
| command | body | Yes | [VoteCommand](#votecommand) |

##### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | Success | [VoteResult](#voteresult) |
| 400 | Bad Request | [VoteResult](#voteresult) |
| 401 | Unauthorized | [VoteResult](#voteresult) |

### Models

#### AuthCommand

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| password | string | User account password | Yes |
| username | string | User account name | Yes |

#### AuthResult

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| AuthResult | object | Result of authentication |  |

#### CreatePollCommand

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| answers | [ string ] | Content of poll's answers | Yes |
| question | string | Poll's question | Yes |

#### Poll

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| Poll | object | Poll data |  |

#### VoteCommand

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| answerIds | [ long ] | IDs of selected answers | Yes |
| pollId | long | ID of selected poll | No |

#### VoteResult

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| VoteResult | object | Voting result |  |
