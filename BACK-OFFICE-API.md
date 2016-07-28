# Back-Office API

---------------------

## Objects

### Player
    {
        "id": 2,
        "firstName": "Peter",
        "lastName": "Vanilla",
        "emailAddress": "peter@mail.com",
        "password": null,
        "nickname": "CoolMan",
        "birthday": "2014-03-05",
        "avatarId": 1,
        "level": 1,
        "address": {
            "street": "Professorsslingan",
            "street2": null,
            "zipCode": "11111",
            "city": "Stockholm",
            "state": null,
            "country": "SWEDEN"
        },
        "currency": "EUR",
        "phoneNumber": "0712313999",
        "wallet": {
            "moneyBalance": 1000,
            "bonusBalance": 1000,
            "turnoverCashback": 0,
            "turnoverMoney": 0,
            "turnoverBonus": 100,
            "turnoverBonusGoal": 2000,
            "reservedBalance": 0,
            "streetCredBalance": 0,
            "turnoverWeekly": 0,
            "turnoverMonthly": 0
        },
        "testAccount" ; false
    }

### User

    {
       "id":13,
       "firstName":"Test2",
       "lastName":"Test2",
       "emailAddress":"test2@animatedgames.se",
       "password":null,
       "nickname":"test2",
       "type":"ADMIN",
       "phoneNumber":"020234234",
       "status":"ACTIVE",
       "createdBy":"joakim@animatedgames.se",
       "createdDate":"2014-03-15T19:07:37.000+01",
       "modifiedBy":"joakim@animatedgames.se",
       "modifiedDate":"2014-03-15T20:15:28.000+01",
       "role":"CUSTOMER_SUPPORT"
    }

### PlayerLimitation

    {
      "limitList": [ {Limit}, ...],
      "sessionLength": null
    }

### Limit

    {
       "limitationType": LOSS_AMOUNT/BET_AMOUNT,
       "timeUnit":DAY/WEEK/MONTH,
       "amount": 400,
       "percent": null
    }

### Block

    {
       "blockType": DEFINITE_SELF_EXCLUSION/INDEFINITE_SELF_EXCLUSION,
       "days": 7
    }

### Password

    {
       "updaterPassword": "test1",
       "userPassword": "test2"
    }

### Game

    {
        "gameId": "lrroulette2french_sw",
        "fullName": "Roulette (rev.3.0) French - Low Limit",
        "name": "Roulette (rev.3.0) French - Low Limit",
        "category": "SLOTS",
        "slug": "roulette",
        "status": "INACTIVE"
    }

### RefundCancelDetail

    {
        "originalReference": "893293WRT4162TRV",
        "amount": 15.00,
        "paymentMethod": "Visa",
        "creationDate": "2014-03-05"
    }

### PlayerActivityType

    {
        LOGIN,
        LOGOUT,
        START_GAME,
        END_GAME,
        SELF_BLOCK,
        REQ_GET_PLAYER_INFO,
        REQ_UPDATE_PLAYER,
        REQ_DELETE_PLAYER,
        REQ_CREATE_PLAYER,
        REQ_GET_PLAYER_ADDRESS,
        REQ_UPDATE_PLAYER_ADDRESS,
        REQ_GET_PLAYER_WALLET,
        REQ_GET_PLAYER_ITEMS,
        REQ_LOGIN,
        REQ_CREATE_RESET_PASSWORD,
        REQ_RESET_PASSWORD,
        REQ_USE_ITEM,
        REQ_VERIFY_PLAYER_EMAIL,
        REQ_GET_PLAYER_LIMITS,
        REQ_UPDATE_PLAYER_LIMITS,
        REQ_SELF_EXCLUSION,
        REQ_LOGOFF,
        REQ_CONVERT_CREDITS_TO_MONEY,
        REQ_CONVERT_CREDITS_TO_BONUS,
        REQ_GET_PAYMENT_TRANSACTION,
        REQ_GET_CAPTURED_PAYMENT_TRANSACTION,
        REQ_GET_PAYMENT_TRANSACTIONS_LIST,
        REQ_GET_WITHDRAW_DETAILS,
        REQ_SEND_WITHDRAW_REQUEST,
        REQ_SEND_DEPOSIT_REQUEST,
        REQ_PLAY_GAME, REQ_GET_GAME_TRANSACTIONS
    }

### PlayerActivity

    {
        "id": "1",
        "playerId": 1,
        "activityType": "LOGIN",
        "activityDate": "2014-03-05",
        "sessionId": "198718276199" // can be null
        "ipAddress": "198.718.276.199" // can be null
    }

### UserActivityType

    {
        LOGIN, LOGOFF,
        REFUNDED, CONFIRM_LARGE_WITHDRAW,
        CREATE_USER, UPDATE_ANOTHER_USER, UPDATE,
        UNBLOCK_PLAYER, BLOCK_PLAYER, UPDATE_PLAYER_LIMITATIONS
    }

### UserActivity

    {
        "id": "1",
        "playerId": 1,
        "activityType": "LOGIN",
        "activityDate": "2014-03-05",
        "description": "User 12 was disabled." // can be null
    }

### GameSessionMoney

    [{
        "playerId": 1,
        "gameId": "BJ",
        "date": "2013-01-01T00:00:00.000+01",
        "betMoney": 0.8,
        "winMoney": 1.39,
        "moneyBalance": 1,
        "bonusBalance": 2
    },
    {
        "playerId": 1,
        "gameId": "BJ",
        "date": "2013-01-16T00:00:00.000+01",
        "betMoney": 0.4,
        "winMoney": 0.69,
        "moneyBalance": 100.23,
        "bonusBalance": 2
    }]


### PaymentTransaction

    {
        "id": 1,
        "date": "2014-03-18T12:06:59.000+01",
        "currency": "EUR",
        "status": "REFUNDED",
        "paymentMethod": "VISA",
        "code": "AUTHORISATION",
        "amount": {
            "value": 2000,
            "euroValueInBigDecimal": 20,
            "euroValueInDouble": 20
        }
    }

### PayoutMessage Transaction
    [{
        "playerId": 3,
        "date": "2014-03-25T12:16:29.000+01",
        "currency": "EUR",
        "status": "AWAITING_APPROVAL",
        "paymentMethod": "mc",
        "code": "REFUND_WITH_DATA",
        "amount": {
            "value": 1000,
            "euroValueInDouble": 10,
            "euroValueInBigDecimal": 10
        }
    }]

---------

### CreditTransactions

	{
    "transactions": [
        {
            "id": 100001,
            "playerId": 1,
            "level": 1,
            "credit": 200,
            "realMoney": 12.50,
            "bonusMoney": null,
            "currency": "EUR",
            "moneyCreditRate": 0.3,
            "bonusCreditRate": 0.3,
            "playerBonus": 1345,
            "createdDate": "2014-03-25T12:16:29.000+01"
        }
    ],
    "count": 1
	}

---------

### PlayerComment

    {
        "createdDate": "2014-03-05", // null for POST
        "comment: "Some comments about the player.",
        "user": "support@animatedgames.se" // null for POST
    }


### BankAccount

    {
       "iban": "SW1234567893409857",
       "bic": "NHA1983",
       "bankName": "Nordea",
       "name": "John Cole"
    }

### Player Bonuses

	{
    "playerBonuses": [
        {
            "id": 100001,
            "name": "Welcome Bonus: 200% Match bonus up to €200",
            "validFrom": "2014-05-26T10:38:36.000+02",
            "validTo": "2015-12-31T23:59:59.000+01",
            "promotionId": 1001,
            "bonusStatus": "ACTIVE",
            "bonusType": "DEPOSIT_BONUS"
        }
    ],
    "count": 1
	}

---------

### WhiteListedIpAddress

    {
       "id": 1,
       "fromIpAddress": "109.228.157.98",
       "toIpAddress": "109.228.160.255" // can be null
    }

---------

### TermsAndConditions

    {
        "id": "1",
        "version": "1.5",
        "createdDate": "2014-03-05",
        "active": true,
        "activatedDate": "2014-03-05"
    }

---------

## Player

### Get Active Player

__Resource URI__: `/api/players/{playerId}`

__Description__: Gets active players

__METHOD__: `GET`

__Path Parameters__:

- {playerId}: Long

__Returns__: Player object

----------

### Update Player

__Resource URI__: `/api/players/{playerId}`

__Description__: Update player information

__METHOD__: `PUT`

__Path Parameters__:

- {playerId}: Long

__Request Parameters__:

- {status}: ACTIVE/INACTIVE/DELETED/DORMANT/LOCKED/BANNED //Not Required
- {firstName}: String //Not Required
- {lastName}: String //Not Required
- {emailAddress}: String //Not Required
- {phoneNumber}: String //Not Required
- {newPassword}: String //Not Required
- {playerVerification}: String: UNVERIFIED, VERIFIED, RE_VERIFY //Not Required
- {trustLevel}: String: BLACK, RED, YELLOW, BLUE,GREEN //Not Required
- {testAccount}: Boolean //Not Required

__Returns__: Player object

----------

### Search Players

__Resource URI__: `/api/players`

__Description__: Searches for multiple players matching the search criteria. If playerId is used search results only returns player, else search results consists of a list of players.

__METHOD__: `GET`

__Request Parameters__:

- {playerId}: Long //Not Required
- {emailAddress}: String //Not Required
- {nickname}: String //Not Required
- {firstName}: String //Not Required
- {lastName}: String //Not Required
- {limitStatus}: String //NotRequired - Available types: "APPLIED"
- {page}: Integer //NotRequired

Example:

`/api/players?playerId=1;emailAddress=mail@mail.com;nickname=Nick;firstName=First;lastName=Last&limitStatus=APPLIED&page=0;`

__Returns__: Field "count" with amount in database and list of Player objects

## NetEnt

### Register NetEnt

__Resource URI__: `/api/players/{playerId}/netent/register`

__Description__: Creates an account at NetEnt

__METHOD__: `POST`

__Path Parameters__:

- {playerId}: Long

__Returns__: Nothing

----------

### Login NetEnt

__Resource URI__: `/api/players/{playerId}/netent/login`

__Description__: Login at NetEnt

__METHOD__: `POST`

__Path Parameters__:

- {playerId}: Long

__Returns__: Nothing

----------

### Logout NetEnt

__Resource URI__: `/api/players/{playerId}/netent/logout`

__Description__: Logout at NetEnt

__METHOD__: `POST`

__Path Parameters__:

- {playerId}: Long

__Returns__: Nothing

----------

## Player Limitation

### Get Player Limitations
__Resource URI__: `/api/players/{playerId}/limit`

__Description__: Gets player's limitations

__METHOD__: `GET`

__Path Parameters__:

- {playerId}: Long

__Returns__: PlayerLimitation object

----------

### Update Player Limitations
__Resource URI__: `/api/players/{playerId}/limit`

__Description__: Updates players limitations, harsher limitations will be applied right away and more lenient limitations in 7 days

__METHOD__: `PUT`

__Path Parameters__:

- {playerId}: Long

__Request body__:

- PlayerLimitation object

__Returns__: Updated PlayerLimitation object

----------

### Force Update Player Limitations
__Resource URI__: `/api/players/{playerId}/limit/force`

__Description__: Updates players limitations, they will be applied right away

__METHOD__: `PUT`

__Path Parameters__:

- {playerId}: Long

__Request body__:

- PlayerLimitation object

__Returns__: Updated PlayerLimitation object

----------

### Block Player As Self-exclusion
__Resource URI__: `/api/players/{playerId}/block`

__Description__: Blocks player

__METHOD__: `PUT`

__Path Parameters__:

- {playerId}: Long

__Request body__:

- Block object

__Returns__: void

----------

### Unblock a Self-excluded Player
__Resource URI__: `/api/players/{playerId}/unblock`

__Description__: Unblocks a Self-excluded Player, change will be applied after 7 days

__METHOD__: `PUT`

__Path Parameters__:

- {playerId}: Long

__Returns__: void

----------

### Force Unblock a Self-excluded Player
__Resource URI__: `/api/players/{playerId}/unblock/force`

__Description__: Unblocks a Self-excluded Player, change will be applied right away

__METHOD__: `PUT`

__Path Parameters__:

- {playerId}: Long

__Returns__: void

---------

## Level

### Get all Levels
__Resource URI__: `/api/levels`

__Description__: Gets all available levels

__METHOD__: `GET`

__Returns__: An array of Level objects.

----------

## User

### Login User
__Resource URI__: `/api/user/login`

__Description__: Logs a user in

__METHOD__: `POST`

__Request body__:

- EmailAddress object

        {
          "emailAddress":"ellinor@blingcity.com"
        }

__Returns__: User object

---------

### Create User
__Resource URI__: `/api/users/{userId}`

__Description__: Creates a new user

__METHOD__: `POST`

__Path Parameters__:

- {userId}: Long

__Request body__:

- User object

        {
           "firstName":"Test2",
           "lastName":"Test2",
           "emailAddress":"test2@animatedgames.se",
           "password":null,
           "nickname":"test2",
           "type":"ADMIN",
           "phoneNumber":"020234234",
           "status":"ACTIVE",
           "role":"CUSTOMER_SUPPORT"
        }

__Returns__: Full User object

---------

### Get Users
__Resource URI__: `/api/users`

__Description__: Gets all users

Example: ~/back-office/api/users?page=0

__METHOD__: `GET`

__Request Parameters__:

    - {page}: Long //required

__Returns__: Field "Count" that contains total records in database and List of full User object.

---------

### Search Users
__Resource URI__: `/api/users/search`

__Description__: Searches for multiple users matching the search criteria. If userId is used search results only returns that user, else search results consists of a list of players.

__METHOD__: `GET`

__Request Parameters__:

- {playerId}: Long //Not Required
- {emailAddress}: String //Not Required
- {nickname}: String //Not Required
- {firstName}: String //Not Required
- {lastName}: String //Not Required
- {page}: String //Not Required

Example:

`/api/users/search?userId=1;emailAddress=mail@mail.com;nickname=Nick;firstName=First;lastName=Last;`

__Returns__: Page of Player objects

---------

### Get User
__Resource URI__: `/api/users/{userId}`

__Description__: Returns the specified user

__METHOD__: `GET`

__Path Parameters__:

- {userId}: Long

__Returns__: Full User object

---------

### Update User
__Resource URI__: `/api/users/{userId}`

__Description__: Updates user

__METHOD__: `PUT`

__Path Parameters__:

- {userId}: Long

__Request body__:

    - The field to updates

__Returns__: Updated Full User object

----------

### Delete User
__Resource URI__: `/api/users/{userId}`

__Description__: Deletes the specified user

__METHOD__: `DELETE`

__Path Parameters__:

- {userId}: Long

__Returns__: Full User object

----------

## Games

### Refresh Games from NetEnt
__Resource URI__: `/api/games/refresh`

__Description__: Refreshes games from NetEnt and displays them to user.

__METHOD__: `POST`

__Path Parameters__:

__Returns__: List of Game object

----------

### Get Game Categories

__Resource URI__: `/api/games/categories`

__Description__: Returns the available game categories.

__METHOD__: `GET`

__Path Parameters__:

__Returns__: Array of Game Categories

----------

### Update game
__Resource URI__: `/api/games/{gameId}`

__Description__: Update game details. gameId is required to define which game to update, but is not updateable.

__METHOD__: `PUT`

__Path Parameters__:

    gameId: "lrroulette2french_sw" \\String

__Request Body__:

    {
        "fullName": "Roulette (rev.3.0) French - Low Limit",
        "name": "Roulette (rev.3.0) French - Low Limit",
        "category": "SLOTS",
        "slug": "roulette",
        "status": "INACTIVE"
    }

__Returns__: Updated Game

----------

### Get Game Session Money

__Resource URI__: `api/games/session`

__Description__: Used to get an array of games including real money bet, real money win, bonus money win, bonus money bet, total money balance, total bonus balance.

__METHOD__: `GET`

__Request Parameters__:

    - {playerId}: Integer //required
    - {startDate}: String //required
    - {endDate}: String //required
    - {page}: String //Not required

**Example**: api/games/session?playerId=4&startDate=2013-01-01&endDate=2015-01-01&page=0

__Returns__: Field "Count" for total number of objects in DB and GameSessionMoney object

----------

## Payment

### Search payments
__Resource URI__: `api/payments/search/`

__Description__:  Search all payments

__METHOD__: `GET`

__Request Parameters__:

    - {startDate}: String //required
    - {endDate}: String //required
    - {playerId}: Long //Not required
    - {eventCode}: AUTHORISATION, CANCELLATION, REFUND, CANCEL_OR_REFUND, CAPTURE, REFUNDED_REVERSED, CAPTURE_FAILED, REFUND_FAILED, REQUEST_FOR_INFORMATION,
    NOTIFICATION_OF_CHARGEBACK, ADVICE_OF_DEBIT, CHARGEBACK, CHARGEBACK_REVERSED, REPORT_AVAILABLE, REFUND_WITH_DATA
    - {paymentStatus}: SUCCESS, FAILURE, AWAITING_PAYMENT, SENDING_FAILURE, REFUNDED, CHARGEBACKED
    - {page}: Integer //Not required

__Example__: `/api/payments/search?startDate=2014-01-01&endDate=2015-01-01&playerId=1&page=0`

__Returns__: Field "Count" for total number of objects in DB and List of PaymentTransactions objects

----------


### Get Refund Detail List
__Resource URI__: `api/payments/refund/{playerId}`

__Description__:  Gets a list of player's successful payments which are refundable

__METHOD__: `GET`

__Path Parameters__:

- {playerId}: Long

__Returns__: List of RefundCancelDetail objects

----------

### Refund Deposit
__Resource URI__: `api/payments/refund/{playerId}/{originalReference}`

__Description__:  Sends refund request

__METHOD__: `PUT`

__Path Parameters__:

    - {playerId}: Long //required
    - {originalReference}: String //required, , an originalReference#RefundCancelDetail instance from the list retrieved by Get Refund Detail List

__Returns__: Void

----------

### Get Cancel Detail List
__Resource URI__: `api/payments/cancel/{playerId}`

__Description__:  Gets a list of player's successful payments which are cancel-able

__METHOD__: `GET`

__Path Parameters__:

- {playerId}: Long

__Returns__: List of RefundCancelDetail objects


----------

### Cancel Deposit
__Resource URI__: `api/payments/cancel/{playerId}/{originalReference}`

__Description__:  Sends cancel request

__METHOD__: `PUT`

__Path Parameters__:

    - {playerId}: Long //required
    - {originalReference}: String //required, , an originalReference#RefundCancelDetail instance from the list retrieved by Get Refund Detail List

__Returns__: Void

----------

### Update Player Wallet
__Resource URI__: `api/payments/{playerId}`

__Description__:  Updates a players wallet. If nullable values are sent no update are made.

__METHOD__: `PUT`

__Path Parameters__:

    - {playerId}: Long //required

__Request Parameters__:

      {
        "moneyBalance": 134,
        "bonusBalance": 20
      },


__Returns__: Players updated wallet

----------

### Confirm payouts
__Resource URI__: `api/payments/confirm`

__Description__:  Sends awaiting payment withdrawals to Adyen

__METHOD__: `POST`

__Request Parameters__:

    {
      "payoutReferences": ["8513957512343802", "1564561564564561", ...]
    }

__Returns__: List of payouts with status code. Example:

    {
        "payouts": {
            "523658451254": 2
        }
    }
Status codes:

1. Success

2. Player not verified

3. Network connection exception with payment provider

----------

### Decline withdraws (payouts)
__Resource URI__: `api/payments/decline`

__Description__:  Declines awaiting payment withdrawals

__METHOD__: `POST`

__Request Parameters__:

    {
      "withdrawReferences": ["8513957512343802", "1564561564564561", ...]
    }

__Returns__: List of successfully declined withdraw references. Example:

    {
      "withdrawReferences": ["8513957512343802", "1564561564564561", ...]
    }

----------

### Get awaiting withdrawal payments
__Resource URI__: `api/payments/awaiting`

__Description__:  Gets awaiting payments

__Request Parameters__:

- {page}: Integer //Not Required, defaults to 0

__METHOD__: `GET`

__Returns__: Page of PayoutMessage Transaction


----------

### Store Bank Account for Withdrawal

__Resource URI__: `/api/payments/bank/{playerId}`

__Description__: Stores Swedish player's bank account information for withdrawal, country always is Sweden

__METHOD__: `POST`

 __Request body__:

    {
       "iban": "SW1234567893409857",        // not null
       "bic": "NHA1983",                    // not null
       "bankName": "Nordea",                // not null
       "name": "John Cole"
    }

__Returns__: Void

----------

## Audit

### Get Player Activity List
__Resource URI__: `api/audit/player/{playerId}`

__Description__:  Gets a list of player's activities

__METHOD__: `GET`

__Path Parameters__:

- {playerId}: Long

__Request Parameters__:

      {
        "activityType": null, // An instance of enum PlayerActivityType, if null then all activity types will be returned
        "startDate": "2014-01-01", // required
        "endDate": "2014-01-01" // required
        "page": Integer //Not Required, defaults to 0
      }

__Returns__: Page of PlayerActivity objects

----------

### Get User Activity List
__Resource URI__: `api/audit/user`

__Description__:  Gets a list of users activities

__METHOD__: `GET`

__Request Parameters__:

      {
		"userId":1, //Not Required
        "activityType": null, // An instance of enum UserActivityType, if null then all activity types will be returned
        "startDate": "2014-01-01", // required
        "endDate": "2014-01-01" // required
        "page": Integer //Not Required, defaults to 0
      }

__Returns__: Page of UserActivity objects

----------

### Get Player Activity List in CSV file
__Resource URI__: `api/audit/report`

__Description__:  Writes CSV generated bytes of player activities to HttpServletResponse

__METHOD__: `GET`

__Request Parameters__:

      {
        "playerId": null, // if null then all players activity will be in the list
        "activityType": null, // An instance of enum PlayerActivityType, if null then all activity types will be returned
        "startDate": "2014-01-01", // required
        "endDate": "2014-01-01" // required
      }

__Returns__: void

----------

## Report

### Get Players' Summary Report
__Resource URI__: `api/reports/summary`

__Description__:  Sends the summary report to the user's email address.

__METHOD__: `GET`

__Returns__: void.

----------

### Get Players' Bonus Report
__Resource URI__: `api/reports/bonus`

__Description__:  Sends the bonus report to the user's email address.

__METHOD__: `GET`

__Returns__: void.

----------

### Get Marketing Report
__Resource URI__: `api/reports/marketing`

__Description__:  Sends the marketing report to the user's email address.

__METHOD__: `GET`

__Returns__: void.

----------

## Comment

### Get Player's comments
__Resource URI__: `/api/players/{playerId}/comment`

__Description__: Search for all player's comments.

__METHOD__: `GET`

__Path Parameters__:

    - {playerId}: Long

__Request Parameters__:

    - {page}: Integer //NotRequired

__Returns__: Field "count" with number of comment in database and list of PlayerComment objects

----------

### Add comment for Player
__Resource URI__: `/api/players/{playerId}/comment`

__Description__: Adds a comment for the Player.

__METHOD__: `POST`

__Path Parameters__:

    - {playerId}: Long //required

__Request Parameters__:

      {
        "comment": "Some comment about the player."
      }

__Returns__: PlayerComment object

----------

## Bonus

### Activate a Bonus for Player

__Resource URI__: `/api/bonuses/{playerId}/activate/{bonusId}`

__Description__: Activates a Customer Support Bonus for the Player

__METHOD__: `GET`

__Path Parameters__:

    - {playerId}: Long //required

    - {bonusId}: Long //required

__Returns__: Void

----------

### Get All Player Bonuses
__Resource URI__: `api/player/bonuses/all/{playerId}`

__Description__:  Gets all bonuses

__Request Parameters__:

    - {page}: Integer //NotRequired
    - {size}: Integer //NotRequired

__METHOD__: `GET`

__Returns__: List of PlayerBonuses object.

----------

##Credit

### Get Credit Transactions
__Resource URI__: `api/credits`

__Description__:  Search all credit transactions

__METHOD__: `GET`

__Request Parameters__:

    - {startDate}: String //required
    - {endDate}: String //required
    - {playerId}: Long //Not required
    - {page}: Integer //Not required

__Example__: `/api/credits?startDate=2014-01-01&endDate=2015-01-01&playerId=1&page=0`

__Returns__: Field "Count" for total number of objects in DB and List of CreditTransactions objects

----------

##White List

### Get White Listed Players
__Resource URI__: `api/whitelist/player`

__Description__: Retrieves white listed players. If playerId is used search results only returns player, else search results consists of a list of white listed players.

__METHOD__: `GET`

__Request Parameters__:

- {playerId}: Long //Not Required
- {page}: Integer //NotRequired
- {size}: Integer //NotRequired

__Returns__: Field "count" with amount in database and list of Player objects

----------

### Add Player to White List
__Resource URI__: `/api/whitelist/{playerId}/player/add`

__Description__: Adds a player to white list

__METHOD__: `PUT`

__Path Parameters__:

- {playerId}: Long

__Returns__: void

----------

### Remove Player from White List
__Resource URI__: `/api/whitelist/{playerId}/player/remove`

__Description__: Removes a player from white list

__METHOD__: `PUT`

__Path Parameters__:

- {playerId}: Long

__Returns__: void

----------

### Get White Listed IP Addresses
__Resource URI__: `api/whitelist/ip`

__Description__: Retrieves white listed IP addresses. If ipAddress is used search results only returns WhiteListedIpAddress containing ipAddress(if exist),
else search results consists of a list of WhiteListedIpAddress.

__METHOD__: `GET`

__Request Parameters__:

- {ipAddress}: Long //Not Required
- {page}: Integer //NotRequired
- {size}: Integer //NotRequired

__Returns__: Field "count" with amount in database and list of WhiteListedIpAddress objects

----------

### Add IP Address to White List
__Resource URI__: `api/whitelist/ip/add`

__Description__: Adds an IP address or IP address range to white list

__METHOD__: `POST`

__Request body__:

- WhiteListedIpAddress object

        {
           "fromIpAddress": "109.228.157.98",
           "toIpAddress": "109.228.160.255" // null if it is a single IP address
        }

__Returns__: WhiteListedIpAddress object

----------

### Remove IP Address from White List
__Resource URI__: `/api/whitelist/{id}/ip/remove`

__Description__: Removes a WhiteListedIpAddress from white list

__METHOD__: `PUT`

__Path Parameters__:

- {id}: Long // WhiteListedIpAddress object's id

__Returns__: void

----------

##Terms and Conditions

### Add a terms and conditions version
__Resource URI__: `api/agreement`

__Description__: Adds a new terms and conditions version

__METHOD__: `POST`

__Request body__:

- TermsAndConditions object with version field

        {
            "version": "1.5"
        }

__Returns__: TermsAndConditions object

----------

### Activate a terms and conditions version
__Resource URI__: `/api/agreement/{id}`

__Description__: Activates an existing terms and conditions version

__METHOD__: `PUT`

__Path Parameters__:

- {id}: Long // TermsAndConditions object's id

__Returns__: TermsAndConditions object
