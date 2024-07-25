INSERT INTO public.users(phone, last_name, first_name, nickname, created, phone_is_verified)
VALUES
    ('9000000001', 'Test_last_name1', 'Test_first_name1', 'Test_nickname1', now(), true),
    ('9000000002', 'Test_last_name2', 'Test_first_name2', 'Test_nickname2', now(), true);

INSERT INTO public.otp_codes(otp_code, user_id, created, expires, is_expired)
SELECT '00000', id, now(), '9999-12-31', false From public.users WHERE phone IN ('9000000001', '9000000002');