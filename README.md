# Club Los Andes

## Propuesta
Proponemos el desarrollo de un sistema de gestión para un **club deportivo**, orientado tanto a los socios como a los administradores. Los **socios** podrán acceder con su cuenta para realizar diversas acciones, como inscribirse en actividades como fútbol, tenis o hockey, alquilar canchas (por ejemplo, de pádel) para organizar partidos, alquilar salones para eventos. 
Por su parte, los **administradores** del sistema tendrán acceso a funcionalidades específicas para gestionar el estado de las cuotas sociales, verificar que la documentación de los socios esté al día y mantener actualizada la oferta de actividades, servicios, profesores, horarios de las actividades, salones, canchas y eventos. Este sistema busca centralizar y facilitar la administración general del club, mejorar la experiencia del socio y optimizar la organización interna mediante un entorno digital accesible y seguro.

---

## Modelo de Dominio
Puede consultar el modelo de dominio en el siguiente enlace:  
🔗 [Ver modelo de dominio](https://drive.google.com/file/d/1u3OEWpqnE02TRypaX-TFQbBMNAMUOwi_/view?usp=sharing)

---

## Modelo de Datos
Puede consultar el modelo de datos en el siguiente enlace:  
🔗 [Ver modelo de datos]([https://drive.google.com/file/d/1u3OEWpqnE02TRypaX-TFQbBMNAMUOwi_/view?usp=sharing](https://app.diagrams.net/#G1_tTyoyUxZY8UrF0o_NhifOYYzEWoCPQO#%7B%22pageId%22%3A%22mJbDxryiVFSr4kU-VdfV%22%7D))

---
### Entidades del sistema

#### Entidades Simples
- **Usuario**
- **Cuota**
- **Actividad**  
- **Cancha**  
- **Profesor**
- **Salón**
- **Partido**

#### Entidades Dependientes
- **MontoCuota**
- **PagoCuota**
- **Inscripción**
- **Horario**
- **Alquiler_cancha**
- **Alquiler_salon**
- **Reserva_pendiente**
- **Prereserva_cancha**
- **Recuperacion_pass**

---

### ABMC (Altas, Bajas, Modificaciones y Consultas)
- **Usuario**:
  - Alta (Registro) con validación de DNI único.
  - Modificación de datos personales.
  - Cambio de contraseña.
  - Consulta de perfil.
- **Salón**: ABMC completa (El administrador puede crear, editar, eliminar y consultar salones).
- **Cancha**: ABMC completa (El administrador puede crear, editar, eliminar y consultar canchas).
- **Actividad**: ABMC completa (El administrador puede crear, editar, eliminar y consultar actividades).
- **Horario**: ABMC completa (El administrador puede crear, editar, eliminar y consultar los horarios de cada actividad).
- **Partido**: ABMC completa (El administrador puede crear, editar, eliminar y consultar partidos).
- **Profesor**: ABMC completa (El administrador puede crear, editar, eliminar y consultar profesores).

---

### Casos de Uso No-ABMC y Lógica de Negocio

#### Gestión de Accesos y Usuarios
- **Registro de usuario**: Se valida estrictamente que el DNI no exista ya en el sistema.
- Login socio:
  - Se valida que el usuario no tenga dos cuotas impagas consecutivas para permitir el acceso.
  - Control para asegurar que no se generen más de dos cuotas por mes.
- **Recuperación de contraseña**:
  - Se valida que el mail ingresado exista en la base de datos.
  - Una vez validado, se envía un correo que deriva al formulario de cambio de contraseña.
- **Cumpleaños en el inicio**: Al loguearse, el sistema calcula si es el cumpleaños del usuario. De ser así, muestra una animación de globos y un mensaje por 15 segundos.

#### Gestión de Alquileres (Salón y Cancha) - Caso de Uso Complejo
Este flujo involucra eventos en momentos distintos:
1. El socio consulta la disponibilidad por fecha.
2. El sistema bloquea los horarios reservados y solo permite seleccionar horarios libres.
3. Antes de confirmarse, se envía un mail de verificación al usuario.
4. El alquiler se registra efectivamente en la base de datos solo tras la confirmación por correo.

#### Gestión de Actividades y Horarios
- **Creación de Horario**: Al crear un horario, se valida que ni el Profesor ni la Cancha asignada estén ocupados en ese momento.
- **Inscripción a actividad**: Se valida que el usuario no esté ya inscripto en esa actividad previamente.

#### Gestión de Pagos
- **Pago con Mercado Pago**: Si la cuota no está paga, se genera un QR con el link de pago para que el socio abone.
- **Pago en Efectivo (Administración)**:
  - El administrador ingresa al listado de clientes y deja constancia del pago.
  - Una vez registrado, el sistema envía automáticamente un mail con el comprobante de pago y los datos pertinentes.
 
### Listados

#### Listados Simples
- **Historial de alquileres de salón (Admin)**: Muestra reservas pasadas y futuras sin filtros.
- **Listado de Socios**: Muestra todos los socios e incluye botones para ver sus cuotas, alquileres de salón o alquileres de cancha.
- **Listado de Actividades**: Listado general con botón de inscripción.
- **Historial de Cuotas**: Muestra las cuotas de cada socio (pagas o no pagas) indicando estado, monto e intereses si corresponde.
- **Listado de todos los Partidos**: Vista general de partidos.

#### Listados Complejos
- **Mis Alquileres Futuros (Socio)**: Muestra solo los alquileres con fecha mayor a la actual, con datos del salón/cancha y del alquiler.
- **Mis Inscripciones**: Listado de las actividades a las que el socio está inscripto.
- **Listado de Cuotas Filtrable**: Permite filtrar por estado: “pagas”, “pendientes” o “todas”.
- **Buscador de socios por DNI**: Filtra dinámicamente el listado mientras se escribe.
- **Alquileres de Salón de un socio**: Historial específico por usuario.
- **Alquileres de Cancha de un socio**: Historial específico por usuario.
- **Horarios de una actividad**: Detalle de días y horas.
- **Partidos Semanales**: Listado de partidos filtrados por fecha, mostrando únicamente los de la semana en curso.

---

## Integrantes 
- María de los Ángeles Arfuso, 51454
- Regina Diodati, 50473
- Delfina Mansilla, 50353
- Francesca Maurutto, 51752


