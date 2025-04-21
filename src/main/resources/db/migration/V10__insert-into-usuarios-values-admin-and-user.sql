
INSERT INTO usuarios (nome, email, senha, ativo)
SELECT 'Admin', 'admin@taskmanager.com', '$2b$12$FbZidRBqya0nzq8zRnQjse6OzMoOr8VyPErUycOFpQAAcrGCuPgd.', true
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE email = 'admin@taskmanager.com'
);


INSERT INTO usuario_roles (usuario_id, role)
SELECT id, 'ROLE_ADMIN'
FROM usuarios
WHERE email = 'admin@taskmanager.com'
  AND NOT EXISTS (
    SELECT 1 FROM usuario_roles WHERE usuario_id = usuarios.id AND role = 'ROLE_ADMIN'
);

INSERT INTO usuario_roles (usuario_id, role)
SELECT id, 'ROLE_USER'
FROM usuarios
WHERE email = 'admin@taskmanager.com'
  AND NOT EXISTS (
    SELECT 1 FROM usuario_roles WHERE usuario_id = usuarios.id AND role = 'ROLE_USER'
);
