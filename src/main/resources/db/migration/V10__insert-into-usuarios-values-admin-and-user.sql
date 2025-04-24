
INSERT INTO usuarios (nome, email, senha, ativo)
SELECT 'Admin', 'admin@taskmanager.com', '$2b$12$FbZidRBqya0nzq8zRnQjse6OzMoOr8VyPErUycOFpQAAcrGCuPgd.', true
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE email = 'admin@taskmanager.com'
);

INSERT INTO usuario_roles (usuario_id, role)
SELECT u.id, 'ROLE_ADMIN'
FROM usuarios u
WHERE u.email = 'admin@taskmanager.com'
  AND NOT EXISTS (
    SELECT 1 FROM usuario_roles ur WHERE ur.usuario_id = u.id AND ur.role = 'ROLE_ADMIN'
);


INSERT INTO usuario_roles (usuario_id, role)
SELECT u.id, 'ROLE_USER'
FROM usuarios u
WHERE u.email = 'admin@taskmanager.com'
  AND NOT EXISTS (
    SELECT 1 FROM usuario_roles ur WHERE ur.usuario_id = u.id AND ur.role = 'ROLE_USER'
);
