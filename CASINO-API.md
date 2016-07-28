# Casino API

---------------------

## Objects

### Player

    {
        "id":2,
        "firstName":"Joakim",
        "lastName":"Gottzen",
        "emailAddress":"joakim+test-local@animatedgames.se",
        "password":null,
        "nickname":"jake",
        "birthday":"1977-01-31",
        "avatar":{
            "id":13,
            "avatarBaseTypeId":1,
            "level":13,
            "skinColor":1,
            "hairColor":1,
            "pictureUrl":"s1h1.png"
        },
        "level":{
            "level":13,
            "turnover":570000.0,
            "cashbackPercentage":0.25,
            "depositBonusPercentage":10.0,
            "creditDices":1
        },
        "currency":"EUR",
        "language":"ENGLISH",
        "phoneNumber":"0739739448",
        "receivePromotion":"SUBSCRIBED",
        "address":{
            "street":"Telefonvägen 30",
            "zipCode":"126 26",
            "city":"Stockholm",
            "country":"SE"
        },
        "wallet":{
            "moneyBalance":0.00,
            "accumulatedCashBack":0.00,
            "reservedBalance":0.00,
            "creditsBalance":10,
            "levelProgress":5700.01,
            "accumulatedWeeklyTurnover":0.00,
            "accumulatedMonthlyTurnover":0.00,
            "accumulatedDailyLoss":0.00,
            "accumulatedWeeklyLoss":0.00,
            "accumulatedMonthlyLoss":0.00,
            "accumulatedDailyBet":0.00,
            "accumulatedWeeklyBet":0.00,
            "accumulatedMonthlyBet":0.00,
            "nextLevelPercentage":0.00,
            "bonusBalance":22.50,
            "bonusConversionGoal":855.00,
            "bonusConversionProgress":0.00,
            "bonusConversionProgressPercentage":0.00
        }
    }

### Address

    {
       "street":"Kungsgatan",
       "street2":null,
       "zipCode":"11219",
       "city":"Stockholm",
       "state":null,
       "country":"Sweden"
    }

### Wallet

    {
        "moneyBalance":0.00,
        "accumulatedCashBack":0.00,
        "reservedBalance":0.00,
        "creditsBalance":10,
        "levelProgress":5700.01,
        "accumulatedWeeklyTurnover":0.00,
        "accumulatedMonthlyTurnover":0.00,
        "accumulatedDailyLoss":0.00,
        "accumulatedWeeklyLoss":0.00,
        "accumulatedMonthlyLoss":0.00,
        "accumulatedDailyBet":0.00,
        "accumulatedWeeklyBet":0.00,
        "accumulatedMonthlyBet":0.00,
        "nextLevelPercentage":0.00,
        "bonusBalance":22.50,
        "bonusConversionGoal":855.00,
        "bonusConversionProgress":0.00,
        "bonusConversionProgressPercentage":0.00
    }

### Item

    {
       "id":0,
       "url":"www.hotmail.com",
       "state":"UNUSED",
    }

### PlayerUuid

    {
       "uuid":0,
       "createdDate":"2014-02-17",
    }

### EmailAddress

    {
       "emailAddress":"hello@hello.com",
    }

### Item

    {
       "id":"1",
       "url":"url",
       "state":USED/UNUSED
    }

### ResetPassword

    {
       "uuid":"5125-5621-45-1233",
       "password":"newPassword",
    }

### Nickname

    {
       "nickname":"Something",
       "password":"newPassword",
    }

### Avatar

    {
       "id":1,
       "avatarBaseTypeId":1,
       "level":1,
       "skinColor":1,
       "hairColor":2,
       "pictureUrl":""
    }

### AvatarBaseType

    {
       "id":1,
       "name":"AvatarName",
       "status":"ACTIVE",
       "createdBy":1,
       "createdDate":2014-01-01,
       "modifiedBy": null,
       "modifiedDate": null,
    }

### PaymentTransaction

    {
       "id":1,
       "name":"AvatarName",
       "status":"ACTIVE",
       "createdBy":1,
       "createdDate":2014-01-01,
       "modifiedBy": null,
       "modifiedDate": null,
    }

### GameInfo

    {
       "gameId":"game_blackjack",
       "playForFun":false,
       "sessionId":12314564,
       "width":"125",
       "height": "561",
       "helpFile": "www.helpfile.com",
       "staticUrl": "www.staticurl.com",
       "gameServerUrl": "www.gameserverurl.com",
       "language": "english",
       "flashVersion": "1.2.50",
       "fullName":"Black Jack"
    }

### GameCategories

    {
        "categories": [
            {
                "name": "FEATURED_SLOTS",
                "slug": "featured-slots",
                "count": 0
            },
            {
                "name": "SLOTS",
                "slug": "slots",
                "games": [
                    {
                        "gameId": "acescratch_sw",
                        "fullName": "Ace",
                        "name": "Ace",
                        "category": "SLOTS",
                        "slug": acescratch
                    },
                    ...
                ],
                "count": 1
            },
            ...
        ]
    }

### PlayerLimitation

    {
      "limitList": [ {Limit}, ...],
      "sessionLength": null
    }

### UpdatePlayerLimitation

    {
      "limitList": [ {Limit}, ...],
      "sessionLength": null,
      "password": "test"
    }

### Limit

    {
       "limitationType": LOSS_AMOUNT/BET_AMOUNT,
       "timeUnit":DAY/WEEK/MONTH,
       "amount": 400,
       "percent": null
    }


### Bonus

    {
        "id": 37,
        "name": "Level 2 bonus: 40 Starburst FR",
        "validFrom": 2014-01-01,
        "validTo": 2014-10-09,
        "bonusType": "DEPOSIT_BONUS",
        "netEntBonusCode": "29928",
        "promotionId": 1,
        "amount" = 1200,
        "maxAmount" = 3000,
        "percentage" = 200,
        "quantity" = 10,
        "currency" = "SEK"
    }
    
### PlayerBonuses

    {
        "totalAmount":47.50,
        "bonuses":[{
            "id":4,
            "name":"Bling To Bonus Money Conversion Bonus",
            "bonusType":"BONUS_MONEY",
            "validFrom":"2013-12-31T23:59:59.000+01",
            "validTo":"2029-12-31T23:59:59.000+01",
            "bonusStatus":"ACTIVE",
            "initialAmount":7.50,
            "currentBalance":17.50,
            "bonusConversionGoal":285.00,
            "bonusConversionProgress":34.50,
            "bonusConversionProgressPercentage":12.11,
            "wagerTimes":38,
            "createdDate":"2014-05-27T22:39:27.000+02"
        },
        {
            "id":5,
            "name":"Bling To Bonus Money Conversion Bonus",
            "bonusType":"BONUS_MONEY",
            "validFrom":"2013-12-31T23:59:59.000+01",
            "validTo":"2029-12-31T23:59:59.000+01",
            "bonusStatus":"INACTIVE",
            "initialAmount":30.00,
            "currentBalance":30.00,
            "bonusConversionGoal":1140.00,
            "bonusConversionProgress":0.00,
            "bonusConversionProgressPercentage":0.00,
            "wagerTimes":38,
            "createdDate":"2014-05-27T22:38:06.000+02"
        }]
    }

The `bonusType`s that the casino will se are `ACTIVE`, `INACTIVE` or `RESERVED`. There can only be one `ACTIVE` bonus at any one time, 
there can be 0 to N `INACTIVE`. `RESERVED` bonuses are the ones that have been reserved pending a withdrawal.

### WithdrawDetail

    {
       "withdrawReference": "19278WR12OP",
       "variant": "visa",
       "holderName": "John Cole",
       "creationDate": 2014-01-01,
       "cardNumber": "9825",  (not null if withdraw reference is a card)
       "accountNumber": "2323", (not null if withdraw reference is a bank account)
       "iban": "2726"  (not null if withdraw reference is a bank account and accountNumber is null)
    }

### WithdrawStore

    {
      "withdrawDetailList": [ {WithdrawDetail}, ...],
      "storeBankAccount": true,
	  "playerBonuses": [ {PlayerBonus}, ... ]
    }

### BankAccount

    {
       "iban": "SW1234567893409857",
       "bic": "NHA1983",
       "bankName": "Nordea"
    }

### Withdraw

    {
       "withdrawReference": "19278WR12OP",
       "amount": 15,
       "password": "pass",
       "paymentMethod": "visa"
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

---------

## Player

### Login Player
__Resource URI__: `/api/players/login`

__Description__: Logs a player in

__METHOD__: `POST`

__Request body__:

    {
      "emailAddress":"ellinor@blingcity.com"
    }

__Returns__: Player object

----------

### Create Player
__Resource URI__: `/api/players`

__Description__: Create a new player

__METHOD__: `POST`

__Request body__:

__"Password"__ has Regex restraint:
"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,40}$"

__"skinColor"__ types: LIGHT, DARK, YELLOW

__"hairColor"__ types: BROWN, BLACK, BLOND

__"language"__ types: SWEDISH, ENGLISH

__"currency"__ types: EUR

__"receivePromotion"__ types: SUBSCRIBED, UNSUBSCRIBED

__"country"__ needs to follow following list:

See [Country.java](animatedx-entities/src/main/java/com/cs/persistence/Country.java)

- Object with following parameters (fields marked as null are nullable):

    {
        "id": null,
        "firstName": "haj",
        "lastName": "aadsd",
        "emailAddress": "acsbssxvs@aaddas.se",
        "nickname": "aabscscxsdz222",
        "password": "abAcxvcx1234!",
        "oldPassword": null,
        "newPassword": null,
        "birthday": "2013-01-29T08:36:46.688Z",
        "language":"SWEDISH",
        "avatar":{
          "id":null,
          "avatarBaseTypeId":1,
          "level":null,
          "skinColor":"LIGHT",
          "hairColor":"BROWN",
          "pictureUrl":null
        },
        "level":{},
        "address": {
          "street": "adress",
          "street2": null,
          "zipCode": "126 26",
          "city": "Hagersten",
          "state": null,
          "country": "SWEDEN"
        },
        "currency": "EUR",
        "phoneNumber": "0739 739 448"
    }

__Returns__: Full Player object

----------

### Verify Player
__Resource URI__: `/api/players/verify/{uuid}`

__Description__: Verifies a player (after signup)

__METHOD__: `GET`

__Path Parameters__:

- {Uuid}: String

__Returns__: Void

----------

### Get Player
__Resource URI__: `/api/players/{id}`

__Description__: Returns the specified player

__METHOD__: `GET`

__Path Parameters__:

- {id}: Long

__Returns__: Full Player object

----------

### Get Updateable Avatars
__Resource URI__: `/api/avatars/changeable`

__Description__: Returns updateable avatars

__METHOD__: `GET`

__Returns__: List of avatars

----------

### Update Player
__Resource URI__: `/api/players`

__Description__: Updates the specified player. If old password and new password is not null password changes too if old password matches password in database. Only
the non-null fields are updated. If the password is to be changed, both the old and new password has to been sent.

__METHOD__: `PUT`

__Request body__:

    {
      "firstName": string or null,
      "lastName": string or null,
      "emailAddress": email or null,
      "password": password or null,
      "newPassword": password or null,
      "nickname": string or null,
      "phoneNumber": string or null,
	  "avatarId": Long (id of avatar player wants to update to)
    }

__Returns__: Updated full Player object

----------

### Inactivate Player
__Resource URI__: `/api/players`

__Description__: Inactivates the specified player

__METHOD__: `DELETE`

__Returns__: Full Player object

----------

### Get Player Address
__Resource URI__: `/api/players/address`

__Description__: Retrieves the specified player's address

__METHOD__: `GET`

__Returns__: Address object

----------

### Update Player Address
__Resource URI__: `/api/players/address`

__Description__: Updates the specified player's address

__METHOD__: `PUT`

__Request body__:

- Address object

__Returns__: Updated Address object

----------

### Get Player Wallet
__Resource URI__: `/api/players/wallet`

__Description__: Gets the specified player's wallet

__METHOD__: `GET`

__Returns__: Wallet object

----------

### Get Player Items
__Resource URI__: `/api/items`

__Description__: Gets the specified player's items

__METHOD__: `GET`

__Returns__: Item object

----------

### Create reset password email
__Resource URI__: `/api/players/reset/create`

__Description__: Generates an email for resetting a players password

__METHOD__: `POST`

__Request body__:

- EmailAddress object

__Returns__: Nothing

----------

### Reset password
__Resource URI__: `/api/players/password/reset`

__Description__: Resets password for player if UUID exists

__METHOD__: `POST`

__Request body__:

- ResetPassword object

__Returns__: Nothing

----------

### Set Item Used
__Resource URI__: `/api/items/{itemId}`

__Description__: Set a specified item for a specified player to Used

__METHOD__: `PUT`

- {itemId}: Long

__Returns__: Item object

----------

### Validate Email
__Resource URI__: `/api/players/validate/email`

__Description__: Validates that an e-mail is free to register during signup

__METHOD__: `POST`

__Request body__:

- EmailAddress object

__Returns__: "DOES`_`NOT`_`EXIST" and StatusCode 3 if e-mail address is available. "EXISTS" and StatusCode 4 if e-mail address is already registered

----------

### Validate Nickname
__Resource URI__: `/api/players/validate/nickname`

__Description__: Validates that a nickname is free to register during signup

__METHOD__: `POST`

__Request body__:

- Nickname object

__Returns__: "DOES`_`NOT`_`EXIST" and StatusCode 3 if nickname is available. "EXISTS" and StatusCode 4 if nickname is already registered

----------

### Logoff Player
__Resource URI__: `/api/players/logoff`

__Description__: Logs off a player from NetEnt (if logged in) and from system

__METHOD__: `POST`

__Returns__: Void

----------

### Update Session Time
__Resource URI__: `/api/players/sessionTime`

__Description__: Increases the player's session time by one minute

__METHOD__: `PUT`

__Returns__: "UNBLOCKED_PLAYER" and StatusCode 13 if session time limitation has not been reached. "BLOCKED_PLAYER" and StatusCode 7 if session time is over.

----------

### Update Player Limitations
__Resource URI__: `/api/players/limit`

__Description__: Updates players limitations

__METHOD__: `PUT`

__Request body__:

- UpdatePlayerLimitation object

__Returns__: Updated PlayerLimitation object

----------

### Get Player Limitations
__Resource URI__: `/api/players/limit`

__Description__: Gets player's limitations

__METHOD__: `GET`

__Returns__: PlayerLimitation object

----------

### Set Player Self Block
__Resource URI__: `/api/players/block?days={days}}`

__Description__: Self blocks a players

__METHOD__: `POST`

__Request Parameters__:

- days: The number of days to block

__Returns__:

- __STATUS__: 204
- Value: Nothing

----------

### Make Player Accept Terms and Conditions 
__Resource URI__: `/api/players/acceptTermsAndConditions`

__Description__: Make Player Accept Terms and Conditions

__METHOD__: `POST`

__Returns__: 
- __STATUS__: 200
- Value: Nothing

----------

## Avatars

### Get Avatar
__Resource URI__: `/api/avatars/{id}`

__Description__: Gets the specified Avatar

__METHOD__: `GET`

__Path Parameters__:

- {id}: Long

__Returns__: Avatar object

----------

### Get Avatar Types For Sign up
__Resource URI__: `/api/avatars/status/{status}`

__Description__: Gets a list of avatar types with the specified status, used during signup to list the available avatars

__METHOD__: `GET`

__Path Parameters__:

- {status}: ACTIVE, INACTIVE, DELETED

__Returns__: List of AvatarBaseType object

----------

### Get Avatars
__Resource URI__: `/api/avatars/{avatarBaseTypeId}/{level}`

__Description__: Gets an Avatar at the specified level with the specified avatar base type

__METHOD__: `GET`

__Path Parameters__:

- {avatarBaseTypeId}: Integer
- {level}: Long

__Returns__: List of Avatars

----------

### Get Sign up Avatars
__Resource URI__: `/api/avatars/signup`

__Description__: Gets all available avatars for signup

__METHOD__: `GET`

__Returns__: List of Avatars

----------

### Get Avatars history
__Resource URI__: `/api/avatars/history`

__Description__: Gets avatars history for specific player

__METHOD__: `GET`

__Returns__: List of Avatars

----------

## Level

### Get all Levels
__Resource URI__: `/api/levels`

__Description__: Gets all available levels

__METHOD__: `GET`

__Returns__: An array of Level objects.

----------

## Payment

### Get Payment Transactions
__Resource URI__: `/api/payments/{providerReference}`

__Description__: Gets all transactions with the specified provider reference. Used to display a player's transaction.

__METHOD__: `GET`

__Path Parameters__:

- {providerReference}: String

__Returns__: PaymentTransaction object

----------

### Get Captured And PaymentStatus PaymentTransaction
__Resource URI__: `/api/payments/{providerReference}`

__Description__: Gets all transactions with the specified provider reference, event code and payment status.

__METHOD__: `GET`

__Path Parameters__:

- {providerReference}: String

- {eventCode}:  AUTHORISATION, CANCELLATION, REFUND, CANCEL_OR_REFUND, CAPTURE, REFUNDED_REVERSED, CAPTURE_FAILED, REFUND_FAILED, REQUEST_FOR_INFORMATION,
NOTIFICATION_OF_CHARGEBACK, ADVICE_OF_DEBIT, CHARGEBACK, CHARGEBACK_REVERSED, REPORT_AVAILABLE, REFUND_WITH_DATA

- {paymentStatus}: SUCCESS, FAILURE, AWAITING_PAYMENT, SENDING_FAILURE, REFUNDED, CHARGEBACKED

__Returns__: PaymentTransaction object

----------

### Get getPlayerPaymentTransactions
__Resource URI__: `/api/payments/search`

__Description__: Gets all transactions with the specified provider reference, event code and payment status between dates. Uses request parameters, the format should be:
`/api/payments/transactions/{playerId}?startDate=2014-01-01&endDate=2015-01-01`

__METHOD__: `GET`

__Request Parameters__:

- {startDate}: String //required
- {endDate}: String //required
- {eventCode}: AUTHORISATION, CANCELLATION, REFUND, CANCEL_OR_REFUND, CAPTURE, REFUNDED_REVERSED, CAPTURE_FAILED, REFUND_FAILED, REQUEST_FOR_INFORMATION,
NOTIFICATION_OF_CHARGEBACK, ADVICE_OF_DEBIT, CHARGEBACK, CHARGEBACK_REVERSED, REPORT_AVAILABLE, REFUND_WITH_DATA
- {paymentStatus}: SUCCESS, FAILURE, AWAITING_PAYMENT, SENDING_FAILURE, REFUNDED, CHARGEBACKED

__Returns__: List of PaymentTransaction object

----------

### Get Credits conversion rates
__Resource URI__: `/api/credits/rates`

__Description__: Returns the credits conversion rates.

__METHOD__: `GET`

__Returns__:
    {
      "realRate": // Double, the rate for converting to real money
      "bonusRate": // Double, the rate for converting to bonus money
    }

### Convert credits to real/bonus money
__Resource URI__: `/api/credits/convert`

__Description__: Converts amount of credits to real money or bonus money.

__METHOD__: `POST`

 __Request body__:

- {creditAmount}: Long (not null)
- {conversionType}: String (REAL\_MONEY/BONUS\_MONEY) (not null)

    {
      "creditAmount": 1,
      "conversionType": "REAL_MONEY"
    }

__Returns__:
    The whole wallet object.

----------

### Get Available Deposit Bonuses
__Resource URI__: `/api/bonuses/deposit`

__Description__: Returns the list of all available bonus deposit options for the player

__METHOD__: `GET`

__Returns__: List of Bonus objects

----------

### Prepare deposit URL
__Resource URI__: `/api/payments/deposit`

__Description__: Creates and returns the deposit url

__METHOD__: `POST`

 __Request body__:

    {
      "amount": 123.45,                     // amount, not null
      "skin":"x3jjDfF",                     // string, not null: the skin code at adyen
      "paymentMethod": "visa"               // string: not null: the payment method
      "bonusId" : 37                        // bonus id, comes from /api/bonuses/available giving the list of Bonus objects with bonusId
    }

__Returns__:

    {
      "url": "https://test.adyen.com/hpp/pay.shtml?merchantReference=CC&paymentAmount=123.45...",                     // string, not null
      "popOut": //non null boolean, true|false
    }

----------

### Get Withdraw Details List and Bank Account Saving for Withdrawals
__Resource URI__: `/api/payments/withdraw`

__Description__: Gets WithdrawStore object which contains a list of player's different accounts(cards) which are target accounts(cards) for withdraw and a boolean
storeBankAccount which determines whether to offer a player saving bank account information for withdrawals (true) or not (false)

__METHOD__: `GET`

__Returns__: List of WithdrawStore objects

----------

### Store Bank Account for Withdrawal
__Resource URI__: `/api/payments/bank`

__Description__: Stores Swedish player's bank account information for withdrawal, country always is Sweden

__METHOD__: `POST`

 __Request body__:

    {
       "iban": "SW1234567893409857",        // not null
       "bic": "NHA1983",                    // not null
       "bankName": "Nordea"                 // not null
    }

__Returns__: Void

----------

### Send Withdraw Request
__Resource URI__: `/api/payments/withdraw`

__Description__: Sends a withdraw request

__METHOD__: `POST`

 __Request body__:

- {withdrawReference}: String (not null), a withdrawReference#WithdrawDetail instance from the list retrieved by Get Withdraw Details List
- {amount}: String (not null)
- {password}: String (not null)
- {paymentMethod}: String (not null), the corresponding withdrawReference#variant instance from the list retrieved by Get Withdraw Details List

    {
       "withdrawReference": "19278WR12OP",
       "amount": 15.50, amount is Euro.Cents
       "password": "pass",
       "paymentMethod": "visa"
    }

__Returns__: Void

----------

### Get Payment Methods
__Resource URI__: `/api/payments/methods`

__Description__: Gets all available payments methods.

__METHOD__: `GET`

__Path Parameters__:

__Returns__: List of available payment methods.

----------

## Games

### Play Flash Game
__Resource URI__: `/api/games`

__Description__: Used for when player wants to start playing a game

__METHOD__: `POST`

 __Request body__:

- {playerId}: Long
- {gameId}: String

__Returns__: GameInfo object

----------

### Get Active flash games
__Resource URI__: `/api/games`

__Description__: Used to get a list of active(playable) games

__METHOD__: `GET`

__Returns__: GameCategories object.

----------

### Play Touch Game
__Resource URI__: `/api/games/touch`

__Description__: Used for when player wants to start playing a touch game

__METHOD__: `POST`

 __Request body__:

- {playerId}: Long
- {gameId}: String

__Returns__: GameInfo object

----------

### Get Active touch games
__Resource URI__: `/api/games/touch`

__Description__: Used to get a list of active(playable) touch games

__METHOD__: `GET`

__Returns__: GameCategories object.

----------

### Get Game Session Money
__Resource URI__: `api/games/session`

__Description__: Used to get an array of games including real money bet, real money win, bonus money win, bonus money bet, total money balance, total bonus balance.

__METHOD__: `GET`

__Request body__:

    - {startDate}: String //required
    - {endDate}: String //required
    - {page}: String //Not required

**Example**: api/games/session&startDate=2013-01-01&endDate=2015-01-01&page=0

__Returns__: Field "Count" for total number of objects in DB and GameSessionMoney object

----------

### Get Leader Players of a Week 
__Resource URI__: `api/games/leaderboard/{week}`

__Description__: Returns the leader players for a specific week showing the biggest win, free spin and game most played for a player. If {week} is not set, 
will return the current week.

__METHOD__: `GET`

**Example**: api/games/leaderboard, api/games/leaderboard/26 

__Returns__: LeaderboardDto Object

----------

## Bonus

### Get current player bonuses
Only one bonus can be active at a time, the rest is inactive

__Resource URI__: `/api/bonuses`

__Description__: Retrieves the players current bonuses

__METHOD__: `GET`

__Returns__: PlayerBonuses object

The "bonusStatus":" can either be `ACTIVE`, `INACTIVE` or `RESERVED`.

----------

### Activate a Link Bonus for Player
__Resource URI__: `/api/bonuses/activate/{bonusCode}`

__Description__: Activates a Link Promotion for Player

__METHOD__: `POST`

__Path Parameters__:

    - {bonusCode}: String //required

__Returns__: Void

----------

### Cancel a bonus
__Resource URI__: `/api/bonuses/cancel/{playerBonusId}`

__Description__: Cancels a bonus

__METHOD__: `POST`

__Path Parameters__:

    - {playerBonusId}: Long //required

__Returns__: Void
