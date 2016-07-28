update bonuses
set max_redemption_amount = amount * 10
where promotion_id = 2 and type = 'BONUS_MONEY';
