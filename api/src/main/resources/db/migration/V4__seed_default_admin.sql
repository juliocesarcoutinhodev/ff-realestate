-- Hash BCrypt (cost=12) gerado offline para a senha definida em ADMIN_DEFAULT_PASSWORD.
-- Nunca regenerar em runtime — alterar apenas via nova migration.
INSERT INTO users (id, name, email, password, role, active)
VALUES (
    gen_random_uuid(),
    'Administrador',
    'admin@fabriciofaceroli.com.br',
    '$2a$12$Xrzc.27ypeyXIDqblG5Br.Kf3WICQBKvaf.QSveR//8RvDzHhvx5q',
    'ADMIN',
    true
);
