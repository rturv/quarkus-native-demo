# 📘 Análisis Funcional del Código

## 🧭 1. Propósito General
Portal web de red social para compartir recetas, con gestión de usuarios, recetas, ingredientes, comentarios y sistema de likes/dislikes. Permite a usuarios registrarse, publicar recetas, interactuar y administrar contenidos. Arquitectura PHP MVC ligera, con separación de lógica de negocio, acceso a datos y vistas. 

## ⚙️ 2. Funcionalidades Principales
- [x] Registro y autenticación de usuarios
- [x] Gestión de recetas (crear, editar, eliminar, listar, ver detalle)
- [x] Gestión de ingredientes y asociación a recetas
- [x] Comentarios en recetas
- [x] Sistema de likes/dislikes AJAX
- [x] Panel de administración de usuarios e ingredientes
- [x] Subida y gestión de fotos (usuarios y recetas)
- [x] Búsqueda y filtrado de recetas
- [x] API REST para operaciones asíncronas (likes, fotos)

## 🧱 3. Entidades del Dominio
| Entidad         | Descripción                                 | Relacionada con                |
|-----------------|---------------------------------------------|-------------------------------|
| Usuario         | Persona registrada en el portal             | Receta, Comentario, Like      |
| Receta          | Publicación de una receta                   | Usuario, Ingrediente, Comentario, Like, Utensilio |
| Ingrediente     | Elemento que compone una receta             | Receta                        |
| Comentario      | Opinión sobre una receta                    | Usuario, Receta               |
| Like/Dislike    | Valoración de una receta                    | Usuario, Receta               |
| Almacenamiento  | Metadatos de archivos subidos (fotos)       | Usuario, Receta               |
| Utensilio       | Utensilio de cocina necesario para receta   | Receta                        |

## 🎬 4. Casos de Uso
| Caso de Uso         | Actor                | Descripción del Flujo                                      |
|---------------------|---------------------|------------------------------------------------------------|
| CU-01 Registro      | Visitante           | El usuario se registra y crea una cuenta                   |
| CU-02 Login         | Usuario             | Inicia sesión con email y clave                            |
| CU-03 Crear Receta  | Usuario             | Publica una receta con ingredientes y foto                 |
| CU-04 Editar Receta | Usuario             | Modifica una receta propia                                 |
| CU-05 Eliminar Receta| Usuario/Admin      | Borra una receta (y sus ingredientes, likes, comentarios)  |
| CU-06 Comentar      | Usuario             | Añade comentario a una receta                              |
| CU-07 Like/Dislike  | Usuario             | Valora una receta vía AJAX                                 |
| CU-08 Subir Foto    | Usuario             | Sube foto de perfil o receta                               |
| CU-09 Buscar Recetas| Usuario/Visitante   | Filtra recetas por nombre, dificultad, ingredientes        |
| CU-10 Admin Usuarios| Administrador       | Gestiona usuarios desde panel admin                        |
| CU-11 Admin Ingredientes| Administrador    | Gestiona ingredientes desde panel admin                    |
| CU-12 Admin Utensilios | Administrador    | Gestiona utensilios desde panel admin                    |

## 👥 5. Actores
- **Usuario:** Persona registrada que puede crear recetas, comentar y valorar
- **Administrador:** Usuario con permisos para gestionar usuarios e ingredientes
- **Visitante:** Persona no registrada, puede consultar recetas y registrarse

## 🧩 6. Reglas de Negocio
- RN-01: Solo usuarios logueados pueden crear, editar o eliminar recetas
- RN-02: Un usuario solo puede editar/eliminar sus propias recetas (excepto admin)
- RN-03: Cada receta debe tener al menos un ingrediente
- RN-04: Un usuario solo puede dar un like/dislike por receta
- RN-05: Fotos se almacenan en subcarpetas por año/mes y se registran en la tabla de almacenamiento
- RN-06: El campo `es_administrador` define permisos de administración
- RN-07: Sesión expira tras 30 minutos de inactividad
- RN-08: Los comentarios solo pueden ser creados por usuarios logueados

## 📋 7. Requisitos Funcionales (RF)
| ID    | Descripción                                         | Prioridad |
|-------|-----------------------------------------------------|-----------|
| RF-01 | El sistema debe permitir registro y login de usuarios| Alta      |
| RF-02 | El usuario podrá crear, editar y eliminar recetas    | Alta      |
| RF-03 | El usuario podrá comentar recetas                    | Media     |
| RF-04 | El usuario podrá valorar recetas con likes/dislikes  | Media     |
| RF-05 | El sistema debe permitir subir fotos                 | Media     |
| RF-06 | El admin podrá gestionar usuarios, ingredientes y utensilios     | Alta      |
| RF-07 | El sistema debe permitir buscar y filtrar recetas    | Media     |

## 🧱 8. Requisitos No Funcionales (RNF)
| ID     | Descripción                                         | Categoría   |
|--------|-----------------------------------------------------|-------------|
| RNF-01 | El sistema debe ser accesible desde endpoints REST   | Usabilidad  |
| RNF-02 | Los tiempos de respuesta deben ser razonables (<2s)  | Rendimiento |
| RNF-03 | Las fotos deben almacenarse organizadas por fecha, cada fecha una subcarpeta    | Mantenimiento|
| RNF-04 | La sesión debe expirar tras 30 minutos               | Seguridad   |
| RNF-05 | El sistema debe usar la capa de persistencia y jpql en el caso de queryes personalizadas | Seguridad   |
| RNF-06 | Las fotos deben almacenarse en alguna carpeta raiz definible por settings | Mantenimiento|

