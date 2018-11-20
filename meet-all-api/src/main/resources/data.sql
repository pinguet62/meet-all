insert into user (id, email, password)
values (-11, 'first@test.fr', 'password_11'),
       (-12, 'second@test.fr', 'password_12');

insert into provider_credential (id, user_id, provider, credential, label)
values (-21, -11, 'TINDER', 'credential_21', 'label_2121'),
       (-22, -11, 'TINDER', 'credential_22', 'label_22'),
       (-23, -12, 'TINDER', 'credential_23', 'label_23');
