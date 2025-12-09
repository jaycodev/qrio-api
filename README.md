# Qrio API

Guía rápida de autenticación JWT con cookies y CORS para integrar el frontend.

## Endpoints clave
- `POST /auth/login`: recibe `{ email, password }`. Devuelve `Set-Cookie` para `access_token` y `refresh_token` y un cuerpo con el token.
- `GET /auth/me`: devuelve el perfil del usuario autenticado.
- `POST /auth/refresh`: usa la cookie `refresh_token` para emitir nuevo `access_token`.

## Cookies emitidas
- `access_token`: HttpOnly; en local `Secure=false`, `SameSite=Lax`.
- `refresh_token`: HttpOnly; en local `Secure=false`, `SameSite=Lax`.
- En producción, usa `SameSite=None` y `Secure=true` (requiere HTTPS).

## CORS y credenciales
La API habilita CORS con credenciales. Configura orígenes permitidos:
- `application-local.properties`:
  - `security.cors.allowed-origins=http://localhost:3000,http://localhost:4200`
- `application-prod.properties`:
  - `security.cors.allowed-origins=https://app.tudominio.com,https://admin.tudominio.com`

### Frontend (fetch/axios)
```javascript
// fetch
fetch('https://api.tudominio.com/auth/me', {
  credentials: 'include',
});

// axios
import axios from 'axios';
const api = axios.create({ baseURL: 'https://api.tudominio.com', withCredentials: true });
const me = await api.get('/auth/me');
```

### Flujo típico
1. Login: `POST /auth/login` con email y password.
2. La API setea cookies. El navegador las enviará automáticamente.
3. Consultar `/auth/me` u otros endpoints protegidos.
4. Al expirar el `access_token`, llamar `POST /auth/refresh`.

## Pruebas rápidas en local (PowerShell)
```powershell
# Arrancar la app
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/qrio"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="1234"
$env:SECURITY_JWT_SECRET="M1Wbq3dXyZl9h2GfT8o3qNw7ZrKp1eQvU4s8b0c6x2y5v9t3p7r1u0a6d4f8j2l5"
$env:SECURITY_JWT_EXPMS="3600000"
./mvnw.cmd spring-boot:run

# Login y guardar cookies
curl -i -s -X POST "http://localhost:8080/auth/login" `
  -H "Content-Type: application/json" `
  -d "{\"email\":\"tester@qrio.com\",\"password\":\"Qrio1234!\"}" `
  -c cookies.txt

# Llamar /auth/me enviando cookies
curl -i -s "http://localhost:8080/auth/me" -b cookies.txt
```

## Buenas prácticas
- Mantén `STATELESS` y evita exponer el JWT a JS (HttpOnly).
- En prod usa HTTPS + `SameSite=None; Secure`.
- `access_token` corto (5–15 min) y `refresh_token` más largo; considera rotación y revocación.
- No uses `*` en CORS cuando `allowCredentials=true`; especifica dominios.
